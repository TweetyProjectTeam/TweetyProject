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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * 
 * Provides a generic class for implementing MaxSAT solvers, i.e. solvers that get as input a set
 * of hard constraints (=propositional formulas that need to be satisfied) and a set of soft constraints
 * (=clauses with weights) whose satisfaction should be maximized (=sum of weights should be maximized). 
 * 
 * @author Matthias Thimm
 *
 */
public abstract class MaxSatSolver extends SatSolver{
	
	/** For temporary files. */
	private static File tempFolder = null;
	
	/**
	 * Set the folder for temporary files created by a MaxSAT solver.
	 * @param tempFolder some temp folder.
	 */
	public static void setTempFolder(File tempFolder){
		MaxSatSolver.tempFolder = tempFolder;
	}
	
	/**
	 * Returns an interpretation with maximal weight on the soft constraints
	 * (or null if the hard constraints are not satisfiable) 
	 * @param hardConstraints a set of propositional formulas
	 * @param softConstraints a map mapping clauses to weights (if there is a formula, which
	 *    is not a clause, i.e. a disjunction of literals), an exception is thrown.
	 * @return an interpretation with maximal weight on the soft constraints
	 * (or null if the hard constraints are not satisfiable) 
	 */
	public abstract Interpretation<PlBeliefSet, PlFormula> getWitness(Collection<PlFormula> hardConstraints, Map<PlFormula,Integer> softConstraints);
	
	@Override
	public Interpretation<PlBeliefSet, PlFormula> getWitness(Collection<PlFormula> formulas) {
		return this.getWitness(formulas, new HashMap<PlFormula,Integer>());
	}

	@Override
	public boolean isSatisfiable(Collection<PlFormula> formulas) {
		return this.getWitness(formulas, new HashMap<PlFormula,Integer>()) != null;
	}
	
	/**
	 * Converts the given MaxSAT instance (i.e. hard and soft constraints, the
	 * latter can only be clauses) to their string representation in 
	 * Dimacs WCNF. Note that a single formula may be represented as multiple
	 * clauses, so there is no simple correspondence between the formulas of the
	 * set and the Dimacs representation. 
	 * @param hardConstraints a collection of formulas
	 * @param softConstraints a map mapping clauses to weights
	 * @param props a list of propositions (=signature) where the indices are used for writing the clauses.
	 * @return a string in Dimacs CNF.
	 * @throws IllegalArgumentException if any soft constraint is not a clause.
	 */
	protected static String convertToDimacsWcnf(Collection<PlFormula> hardConstraints, Map<PlFormula,Integer> softConstraints, List<Proposition> props) throws IllegalArgumentException{
		String s = "";
		int num_clauses = 0;		
		int sum_weight = 0;
		for(PlFormula f: softConstraints.keySet()) {
			sum_weight += softConstraints.get(f);
			if(f instanceof Proposition) {
				s += softConstraints.get(f) + " " + (props.indexOf(f) + 1) + " 0\n";
			}else if(f.isLiteral()){
				s += softConstraints.get(f) + " -" +  (props.indexOf((Proposition)((Negation)f).getFormula()) + 1) + " 0\n";
			}else if(!(f instanceof Disjunction)) {
				throw new IllegalArgumentException("Clause expected.");
			}else {
				s += softConstraints.get(f) + " ";
				for(PlFormula p2: (Disjunction) f){
					if(p2 instanceof Proposition)
						s += (props.indexOf(p2) + 1) + " ";
					else if(p2.isLiteral()){
						s += "-" + (props.indexOf((Proposition)((Negation)p2).getFormula()) + 1) + " ";
					}else throw new IllegalArgumentException("Clause expected.");				
				}
				s += "0\n";	
			}					
		}		
		sum_weight++;
		for(PlFormula p: hardConstraints){
			Conjunction conj = p.toCnf();
			for(PlFormula p1: conj){
				num_clauses++;
				// max weight as we have a hard clause
				s += sum_weight + " ";
				// as conj is in CNF all formulas should be disjunctions
				Disjunction disj = (Disjunction) p1;
				for(PlFormula p2: disj){
					if(p2 instanceof Proposition)
						s += (props.indexOf(p2) + 1) + " ";
					else if(p2.isLiteral()){
						s += "-" + (props.indexOf((Proposition)((Negation)p2).getFormula()) + 1) + " ";
					}else throw new RuntimeException("This should not happen: formula is supposed to be in CNF but another formula than a literal has been encountered.");				
				}			
				s += "0\n";
			}
		}
		return "p wcnf " + props.size() + " " + num_clauses + " " + sum_weight + "\n" + s;
	}

	/**
	 * Converts the given MaxSAT instance (i.e. hard and soft constraints, the
	 * latter can only be clauses) to their string representation in 
	 * Dimacs WCNF and writes it to a temporary file. Note that a single formula may be represented as multiple
	 * clauses, so there is no simple correspondence between the formulas of the
	 * set and the Dimacs representation. 
	 * @param hardConstraints a collection of formulas
	 * @param softConstraints a map mapping clauses to weights
	 * @param props a list of propositions (=signature) where the indices are used for writing the clauses.
	 * @return a string in Dimacs CNF.
	 * @throws IOException if some file issue occurs
	 * @throws IllegalArgumentException if any soft constraint is not a clause.
	 */
	protected static File createTmpDimacsWcnfFile(Collection<PlFormula> hardConstraints, Map<PlFormula,Integer> softConstraints, List<Proposition> props) throws IOException{
		String r = MaxSatSolver.convertToDimacsWcnf(hardConstraints, softConstraints, props);
		File f = File.createTempFile("tweety-sat", ".wcnf", MaxSatSolver.tempFolder);		
		f.deleteOnExit();
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		writer.print(r);
		writer.close();		
		return f;
	}
	
	/**
	 * Returns the cost of the given interpretation, i.e. the sum of the weights
	 * of all violated soft constraints. If the interpretation does not satisfy the
	 * hard constraints -1 is returned; 
	 * @param interpretation some interpretation 
	 * @param hardConstraints a set of hard constraints
	 * @param softConstraints a set of soft constraints
	 * @return the cost of the interpretation
	 */
	public static int costOf(Interpretation<PlBeliefSet, PlFormula> interpretation, Collection<PlFormula> hardConstraints, Map<PlFormula, Integer> softConstraints){
		for(PlFormula f: hardConstraints)
			if(!interpretation.satisfies(f))
				return -1;
		int costs = 0;
		for(PlFormula f: softConstraints.keySet())
			if(!interpretation.satisfies(f))
				costs += softConstraints.get(f);
		return costs;
	}
}
