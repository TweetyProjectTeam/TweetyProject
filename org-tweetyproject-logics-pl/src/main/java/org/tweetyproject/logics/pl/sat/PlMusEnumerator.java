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
package org.tweetyproject.logics.pl.sat;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.logics.commons.analysis.AbstractMusEnumerator;
import org.tweetyproject.logics.commons.analysis.NaiveMusEnumerator;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Contradiction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;

/**
 * This abstract class models a MUS enumerator for propositional logic, i.e. an approach
 * that lists all minimal unsatisfiable subsets of a given set of formulas. It also
 * provides some static methods for accessing a centrally configured default
 * MUS enumerator.
 * 
 * @author Matthias Thimm
 */
public abstract class PlMusEnumerator extends AbstractMusEnumerator<PlFormula>{

	/** The default MUS enumerator. */
	private static AbstractMusEnumerator<PlFormula> defaultEnumerator = null;
	
	/**
	 * Sets the default MUS enumerator.
	 * @param enumerator some MUS enumerator
	 */
	public static void setDefaultEnumerator(AbstractMusEnumerator<PlFormula> enumerator){
		PlMusEnumerator.defaultEnumerator = enumerator;
	}
	
	/**
	 * Returns "true" if a default MUS enumerator is configured.
	 * @return "true" if a default MUS enumerator is configured.
	 */
	public static boolean hasDefaultEnumerator(){
		return PlMusEnumerator.defaultEnumerator != null;
	}

	/**
	 * Converts the given set of formulas to their string representation in Dimacs
	 * CNF. The return value is a pair of<br>
	 * 1.) the string representation<br>
	 * 2.) a list of collections of formulas (all from the given set); the
	 * interpretation of this list is that the generated clause no K originated from
	 * the propositional formula given at index k.
	 * 
	 * @param formulas a collection of formulas.
	 * @return a string in Dimacs CNF and a mapping between clauses and original
	 *         formulas.
	 */
	public static Pair<String, List<PlFormula>> convertToDimacsAndIndex(Collection<PlFormula> formulas) {
		List<Proposition> props = new ArrayList<Proposition>();
		for (PlFormula f : formulas) {
			props.removeAll(f.getAtoms());
			props.addAll(f.getAtoms());
		}
		List<PlFormula> clauses = new ArrayList<PlFormula>();
		List<PlFormula> mappings = new ArrayList<PlFormula>();
		for (PlFormula p : formulas) {
			Conjunction pcnf;
			if (p.isClause()) {
				pcnf = new Conjunction();
				pcnf.add(p);
			} else
				pcnf = p.toCnf();
			for (PlFormula sub : pcnf) {
				clauses.add(sub);
				mappings.add(p);
			}
		}
		int clauseCount = clauses.size();
		String s = "";
		for (PlFormula p1 : clauses) {
			// as conj is in CNF all formulas should be disjunctions
			Disjunction disj = (Disjunction) p1;
			String stemp = "";
			boolean foundContradiction = false;
			for (PlFormula p2 : disj) {
				if (p2 instanceof Proposition)
					stemp += (props.indexOf(p2) + 1) + " ";
				else if (p2.isLiteral()) 
					stemp += "-" + (props.indexOf((Proposition) ((Negation) p2).getFormula()) + 1) + " ";
				else if (p2 instanceof Tautology) {
					stemp = "";
					break;
				} else if (p2 instanceof Contradiction) {
					foundContradiction = true;
					continue;
				} else
					throw new RuntimeException(
							"This should not happen: formula is supposed to be in CNF but another formula than a literal has been encountered. The type of the formula is " + p2.getClass());
			}
			if (stemp == "") {
				if (foundContradiction) {
					//case: whole clause is a contradiction, therefore the entire kb is false
					String contradiction = "p cnf 1 2\n1 0\n-1 0\n";
					return new Pair<String, List<PlFormula>>(contradiction,new ArrayList<PlFormula>(formulas));
				}
				else
					//case: whole clause is a tautology
					clauseCount--;		
			}
			if (stemp != "")
				s += stemp + "0\n";
		}
		if (s == "") {
			//case: entire kb is a tautology
			String tautology = "p cnf 0 0\n";
			return new Pair<String, List<PlFormula>>(tautology,new ArrayList<PlFormula>(formulas));
		}
		s = "p cnf " + props.size() + " " + clauseCount + "\n" + s;
		return new Pair<String, List<PlFormula>>(s, mappings);
	}
	
	/**
	 * Creates a temporary file in Dimacs format and also returns a mapping between
	 * formulas and clauses.
	 * 
	 * @param formulas a collection of formulas
	 * 
	 * @return the file handler and a mapping between clauses and original formulas.
	 * @throws IOException if something went wrong while creating a temporary file.
	 */
	protected static Pair<File, List<PlFormula>> createTmpDimacsFileAndIndex(Collection<PlFormula> formulas)
			throws IOException {
		Pair<String, List<PlFormula>> r = PlMusEnumerator.convertToDimacsAndIndex(formulas);
		File f = File.createTempFile("tweety-sat", ".cnf");
		f.deleteOnExit();
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		writer.print(r.getFirst());
		writer.close();
		return new Pair<File, List<PlFormula>>(f, r.getSecond());
	}
	
	/**
	 * Returns the default MUS enumerator.<br><br>
	 * If a default MUS enumerator has been configured this enumerator
	 * is returned by this method. If no default  MUS enumerator is 
	 * configured, a naive enumerator based on the default SAT solver
	 * is returned as a fallback and a message is
	 * printed to stderr pointing out that no default MUS enumerator is configured.
	 * @return the default MUS enumerator.
	 */
	public static AbstractMusEnumerator<PlFormula> getDefaultEnumerator(){
		if(PlMusEnumerator.defaultEnumerator != null)
			return PlMusEnumerator.defaultEnumerator;
		System.err.println("No default MUS enumerator configured, using "
				+ "naive enumerator based on default SAT solver as fallback. "
				+ "It is strongly advised that a default MUS enumerator is manually configured, see "
				+ "'http://tweetyproject.org/doc/mus-enumerators.html' "
				+ "for more information.");
		return new NaiveMusEnumerator<PlFormula>(SatSolver.getDefaultSolver());
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.AbstractMusEnumerator#minimalInconsistentSubsets(java.util.Collection)
	 */
	@Override
	public abstract Collection<Collection<PlFormula>> minimalInconsistentSubsets(Collection<PlFormula> formulas);

    /** Default Constructor */
    public PlMusEnumerator(){}
}
