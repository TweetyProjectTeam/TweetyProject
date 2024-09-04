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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.sat;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tweetyproject.commons.Interpretation;
import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Contradiction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;

/**
 *
 * Interface for SAT solvers which work on the Dimacs format. It
 * adds some utility methods for directly manipulating clauses
 * in Dimacs format.
 *
 * @author Matthias Thimm
 *
 */
public abstract class DimacsSatSolver extends SatSolver{

	/** For temporary files. */
	private static File tempFolder = null;

	/**
	 * Set the folder for temporary files created by SAT solver.
	 *
	 * @param tempFolder some temp folder.
	 */
	public static void setTempFolder(File tempFolder) {
		DimacsSatSolver.tempFolder = tempFolder;
	}

	/**
	 * Converts the given set of formulas to their string representation in Dimacs
	 * CNF. Note that a single formula may be represented as multiple clauses, so
	 * there is no simple correspondence between the formulas of the set and the
	 * Dimacs representation.
	 *
	 * @param formulas a collection of formulas
	 * @return a list of strings in Dimacs CNF.
	 */
	public static List<String> convertToDimacs(Collection<PlFormula> formulas) {
		return DimacsSatSolver.convertToDimacs(formulas, DimacsSatSolver.getDefaultIndices(formulas).getFirst(),new LinkedList<String>());
	}


	/**
	 * Convert to dimacs
	 * @param formulas a collection of formulas
	 * @param prop_index a map mapping propositions (=signature) to the indices that are
	 *                 used for writing the clauses.
	 * @param additional_clauses additional clauses to be considered
	 * @return  a list of strings in Dimacs CNF.
	 */
	public static List<String> convertToDimacs(Collection<PlFormula> formulas, Map<Proposition,Integer> prop_index, List<String> additional_clauses) {
		List<String> result = new LinkedList<>();
		int num_clauses = 0;
		for (PlFormula p : formulas) {
			Conjunction conj;
			if (p.isClause()) {
				conj = new Conjunction();
				conj.add(p);
			} else
				conj = p.toCnf();
			boolean foundContradiction = false;
			for (PlFormula p1 : conj) {
				// as conj is in CNF all formulas should be disjunctions
				Disjunction disj = (Disjunction) p1;
				if (disj.isEmpty())
					continue;
				num_clauses++;
				String stemp = "";
				for (PlFormula p2 : disj) {
					if (p2 instanceof Proposition) {
						stemp += prop_index.get(p2) + " ";
					} else if (p2.isLiteral())
						stemp += "-" + prop_index.get((Proposition) ((Negation) p2).getFormula()) + " ";
					else if (p2 instanceof Tautology) {
						stemp = "";
						break;
					} else if (p2 instanceof Contradiction) {
						foundContradiction = true;
						continue;
					} else
						throw new RuntimeException(p2 +
								"This should not happen: formula is supposed to be in CNF but another formula than a literal has been encountered. The type of the formula is " + p2.getClass());
				}
				if (stemp != "")
					result.add(stemp + "0");
				else  {
					if (foundContradiction) {
						//case: whole clause is a contradiction, therefore the entire kb is false
						List<String> s = new LinkedList<>();
						s.add("p cnf 1 2");
						s.add("1 0");
						s.add("-1 0");
						return s;
					}else
						//case: whole clause is a tautology, remove it from kb
						num_clauses--;
				}
			}
		}
		if (result.isEmpty() && num_clauses == 0) {
			//case: entire kb is a tautology
			List<String> s = new LinkedList<>();
			s.add("p cnf 0 0");
			return s;

		}
		result.add(0,"p cnf " + prop_index.keySet().size() + " " + (num_clauses+additional_clauses.size()));
		result.addAll(additional_clauses);
		// add additional clauses
		return result;
	}

	/**
	 * Creates a temporary file in Dimacs format with the given proposition2variable
	 * mapping.
	 *
	 * @param formulas a collection of formulas
	 * @param prop_index a map mapping propositions (=signature) to the indices that are
	 *                 used for writing the clauses.
	 * @param additional_clauses additional clauses in text form to be added (already correctly formatted in CNF!)
	 * @return the file handler.
	 * @throws IOException if something went wrong while creating a temporary file.
	 */
	protected static File createTmpDimacsFile(Collection<PlFormula> formulas, Map<Proposition,Integer> prop_index, List<String> additional_clauses)
			throws IOException {
		List<String> r = DimacsSatSolver.convertToDimacs(formulas, prop_index, additional_clauses);
		File f = File.createTempFile("tweety-sat", ".cnf", DimacsSatSolver.tempFolder);
		f.deleteOnExit();
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		for(String s: r)
			writer.println(s);
		writer.close();
		return f;
	}

	/**
	 * Creates the default index and inverted index for the propositions
	 * appearing in the given set of formulas
	 * @param formulas a set of formulas
	 * @return the default index and inverted index
	 */
	protected static Pair<Map<Proposition,Integer>,Map<Integer,Proposition>> getDefaultIndices(Collection<PlFormula> formulas){
		Map<Proposition,Integer> prop_index = new HashMap<>();
		Map<Integer,Proposition> prop_inverted_index = new HashMap<>();
		int index = 1;
		for(PlFormula f: formulas)
			for(Proposition p: f.getAtoms())
				if(!prop_index.containsKey(p)) {
					prop_index.put(p, index);
					prop_inverted_index.put(index++, p);
				}
		return new Pair<Map<Proposition,Integer>,Map<Integer,Proposition>>(prop_index,prop_inverted_index);
	}

	/**
	 * If the collection of formulas is consistent this method returns some model of
	 * it or, if it is inconsistent, null.
	 *
	 * @return some model of the formulas or null.
	 */
	public Interpretation<PlBeliefSet, PlFormula> getWitness(Collection<PlFormula> formulas){
		Pair<Map<Proposition,Integer>,Map<Integer,Proposition>> i = getDefaultIndices(formulas);
		return this.getWitness(formulas, i.getFirst(), i.getSecond(),new LinkedList<String>());
	}

	/**
	 * If the collection of formulas is consistent this method returns some model of
	 * it or, if it is inconsistent, null.
	 * @param formulas a set of formulas
	 * @param prop_index mapping propositions to numbers used for representing the SAT instance
	 * @param prop_inverted_index  inverted index of prop_index
	 * @param additional_clauses additional clauses in text form to be added (already correctly formatted in CNF!)
	 * @return some model of the formulas or null.
	 */
	public abstract Interpretation<PlBeliefSet, PlFormula> getWitness(Collection<PlFormula> formulas, Map<Proposition,Integer> prop_index, Map<Integer,Proposition> prop_inverted_index, List<String> additional_clauses);

	/**
	 * Checks whether the given set of formulas is satisfiable.
	 *
	 * @param formulas a set of formulas.
	 * @return "true" if the set is consistent.
	 */
	public boolean isSatisfiable(Collection<PlFormula> formulas) {
		Pair<Map<Proposition,Integer>,Map<Integer,Proposition>> i = getDefaultIndices(formulas);
		return this.isSatisfiable(formulas, i.getFirst(), new LinkedList<String>());
	}

	/**
	 * Checks whether the given set of formulas is satisfiable.
	 *
	 * @param formulas a set of formulas.
	 * @param prop_index maps propositions to the number that shall be used to
	 * 		represent it (a natural number > 0).
	 * @param additional_clauses additional clauses in text form to be added (already correctly formatted in CNF!)
	 * @return "true" if the set is consistent.
	 */
	public abstract boolean isSatisfiable(Collection<PlFormula> formulas, Map<Proposition,Integer> prop_index, List<String> additional_clauses);

	@Override
	public abstract boolean isInstalled();


    /** Default Constructor */
    public DimacsSatSolver(){}
}
