/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.pl.sat;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester;
import net.sf.tweety.logics.commons.analysis.ConsistencyWitnessProvider;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * Abstract class for specifying SAT solvers.
 * @author Matthias Thimm
 */
public abstract class SatSolver implements BeliefSetConsistencyTester<PropositionalFormula>, ConsistencyWitnessProvider<PlBeliefSet,PropositionalFormula>{

	/** The default SAT solver. */
	private static SatSolver defaultSatSolver = null;
	/** For temporary files. */
	private static File tempFolder = null;
	
	/**
	 * Sets the default SAT solver.
	 * @param solver some SAT solver
	 */
	public static void setDefaultSolver(SatSolver solver){
		SatSolver.defaultSatSolver = solver;
	}
	
	/**
	 * Set the folder for temporary files created by SAT solver.
	 * @param tempFolder some temp folder.
	 */
	public static void setTempFolder(File tempFolder){
		SatSolver.tempFolder = tempFolder;
	}

	/**
	 * Returns "true" if a default SAT solver is configured.
	 * @return "true" if a default SAT solver is configured.
	 */
	public static boolean hasDefaultSolver(){
		return SatSolver.defaultSatSolver != null;
	}

	/**
	 * Returns the default SAT solver.</br></br>
	 * If a default SAT solver has been configured this solver
	 * is returned by this method. If no default solver is 
	 * configured, the Sat4j solver (<code>net.sf.tweety.pl.sat.Sat4jSolver</code>)
	 * is returned as a fallback and a message is
	 * printed to stderr pointing out that no default SAT solver is configured.
	 * @return the default SAT solver.
	 */
	public static SatSolver getDefaultSolver(){
		if(SatSolver.defaultSatSolver != null)
			return SatSolver.defaultSatSolver;
		System.err.println("No default SAT solver configured, using "
				+ "'Sat4jSolver' with default settings as fallback. "
				+ "It is strongly advised that a default SAT solver is manually configured, see "
				+ "'http://tweetyproject.org/doc/sat-solvers.html' "
				+ "for more information.");
		return new Sat4jSolver();
	}
	
	/**
	 * Converts the given set of formulas to their string representation in 
	 * Dimacs CNF. Note that a single formula may be represented as multiple
	 * clauses, so there is no simple correspondence between the formulas of the
	 * set and the Dimacs representation. Use <code>convertToDimacs(.)</code> for
	 * obtaining a map between those.
	 * @param formulas a collection of formulas
	 * @param a list of propositions (=signature) where the indices are used for writing the clauses.
	 * @return a string in Dimacs CNF.
	 */
	protected static String convertToDimacs(Collection<PropositionalFormula> formulas, List<Proposition> props){
		String s = "";
		int num_clauses = 0;
		for(PropositionalFormula p: formulas){
			Conjunction conj = p.toCnf();
			for(PropositionalFormula p1: conj){
				num_clauses++;
				// as conj is in CNF all formulas should be disjunctions
				Disjunction disj = (Disjunction) p1;
				for(PropositionalFormula p2: disj){
					if(p2 instanceof Proposition)
						s += (props.indexOf(p2) + 1) + " ";
					else if(p2 instanceof Negation){
						s += "-" + (props.indexOf((Proposition)((Negation)p2).getFormula()) + 1) + " ";
					}else throw new RuntimeException("This should not happen: formula is supposed to be in CNF but another formula than a literal has been encountered.");				
				}			
				s += "0\n";
			}
		}
		return "p cnf " + props.size() + " " + num_clauses + "\n" + s;
	}

	/**
	 * Converts the given set of formulas to their string representation in 
	 * Dimacs CNF. The return value is a pair of<br/>
	 * 1.) the string representation<br/>
	 * 2.) a list of collections of formulas (all from the given set); the interpretation of this list
	 * is that the generated clause no K originated from the propositional formula given at index k.
	 * @param formulas a collection of formulas.
	 * @return a string in Dimacs CNF and a mapping between clauses and original formulas.
	 */
	protected static Pair<String,List<PropositionalFormula>> convertToDimacs(Collection<PropositionalFormula> formulas){
		List<Proposition> props = new ArrayList<Proposition>();
		for(PropositionalFormula f: formulas){
			props.removeAll(f.getAtoms());
			props.addAll(f.getAtoms());		
		}		
		List<PropositionalFormula> clauses = new ArrayList<PropositionalFormula>();
		List<PropositionalFormula> mappings = new ArrayList<PropositionalFormula>();
		for(PropositionalFormula p: formulas){
			Conjunction pcnf = p.toCnf();
			for(PropositionalFormula sub: pcnf){
				clauses.add(sub);
				mappings.add(p);
			}			
		}		
		String s = "p cnf " + props.size() + " " + clauses.size() + "\n";
		for(PropositionalFormula p1: clauses){
			// as conj is in CNF all formulas should be disjunctions
			Disjunction disj = (Disjunction) p1;
			for(PropositionalFormula p2: disj){
				if(p2 instanceof Proposition)
					s += (props.indexOf(p2) + 1) + " ";
				else if(p2 instanceof Negation){
					s += "-" + (props.indexOf((Proposition)((Negation)p2).getFormula()) + 1) + " ";
				}else throw new RuntimeException("This should not happen: formula is supposed to be in CNF but another formula than a literal has been encountered.");				
			}			
			s += "0\n";
		}
		return new Pair<String,List<PropositionalFormula>>(s,mappings);
	}
	
	/**
	 * Creates a temporary file in Dimacs format with the given proposition2variable mapping.
	 * @param formulas a collection of formulas
	 * @param a list of propositions (=signature) where the indices are used for writing the clauses.
	 * @return the file handler. 
	 * @throws IOException if something went wrong while creating a temporary file. 
	 */
	protected static File createTmpDimacsFile(Collection<PropositionalFormula> formulas, List<Proposition> props) throws IOException{
		String r = SatSolver.convertToDimacs(formulas, props);
		File f = File.createTempFile("tweety-sat", ".cnf", SatSolver.tempFolder);		
		f.deleteOnExit();
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		writer.print(r);
		writer.close();		
		return f;
	}
	
	/**
	 * Creates a temporary file in Dimacs format and also returns a mapping between formulas and clauses.
	 * @param formulas a collection of formulas
	 * @param a list of propositions (=signature) where the indices are used for writing the clauses
	 * (a list of collections of formulas (all from the given set); the interpretation of this list
	 * is that the generated clause no K originated from the propositional formula given at index k).
	 * @return the file handler and a mapping between clauses and original formulas. 
	 * @throws IOException if something went wrong while creating a temporary file. 
	 */
	protected static Pair<File,List<PropositionalFormula>> createTmpDimacsFile(Collection<PropositionalFormula> formulas) throws IOException{
		Pair<String,List<PropositionalFormula>> r = SatSolver.convertToDimacs(formulas);
		File f = File.createTempFile("tweety-sat", ".cnf");
		f.deleteOnExit();
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		writer.print(r.getFirst());
		writer.close();		
		return new Pair<File,List<PropositionalFormula>>(f,r.getSecond());
	}
	
	/**
	 * If the collection of formulas is consistent this method
	 * returns some model of it or, if it is inconsistent, null.
	 * @return some model of the formulas or null.
	 */
	public abstract Interpretation<PlBeliefSet,PropositionalFormula> getWitness(Collection<PropositionalFormula> formulas);
	
	/**
	 * Checks whether the given set of formulas is satisfiable.
	 * @param formulas a set of formulas.
	 * @return "true" if the set is consistent.
	 */
	public abstract boolean isSatisfiable(Collection<PropositionalFormula> formulas);

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent(net.sf.tweety.commons.BeliefSet)
	 */
	@Override
	public boolean isConsistent(BeliefSet<PropositionalFormula> beliefSet) {
		return this.isSatisfiable(beliefSet);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent(java.util.Collection)
	 */
	@Override
	public boolean isConsistent(Collection<PropositionalFormula> formulas) {
		return this.isSatisfiable(formulas);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent(net.sf.tweety.commons.Formula)
	 */
	@Override
	public boolean isConsistent(PropositionalFormula formula) {
		Collection<PropositionalFormula> formulas = new HashSet<PropositionalFormula>();
		formulas.add(formula);
		return this.isSatisfiable(formulas);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.ConsistencyWitnessProvider#getWitness(net.sf.tweety.commons.Formula)
	 */
	@Override
	public Interpretation<PlBeliefSet,PropositionalFormula> getWitness(PropositionalFormula formula) {
		Collection<PropositionalFormula> f = new HashSet<PropositionalFormula>();
		f.add(formula);
		return this.getWitness(f);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.ConsistencyWitnessProvider#getWitness(net.sf.tweety.commons.BeliefSet)
	 */
	@Override
	public Interpretation<PlBeliefSet,PropositionalFormula> getWitness(BeliefSet<PropositionalFormula> bs) {
		return this.getWitness((Collection<PropositionalFormula>) bs);
	}
}
