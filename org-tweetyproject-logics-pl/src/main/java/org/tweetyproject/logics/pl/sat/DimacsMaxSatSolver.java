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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tweetyproject.commons.Interpretation;
import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * Generic class for Dimacs-based MaxSAT solvers.
 *
 * @author Matthias Thimm
 *
 */
public abstract class DimacsMaxSatSolver extends MaxSatSolver{

	/** For temporary files. */
	private static File tempFolder = null;

	/**
	 * Set the folder for temporary files created by a MaxSAT solver.
	 * @param tempFolder some temp folder.
	 */
	public static void setTempFolder(File tempFolder){
		DimacsMaxSatSolver.tempFolder = tempFolder;
	}

	/**
	 * Converts the given MaxSAT instance (i.e. hard and soft constraints, the
	 * latter can only be clauses) to their string representation in
	 * Dimacs WCNF. Note that a single formula may be represented as multiple
	 * clauses, so there is no simple correspondence between the formulas of the
	 * set and the Dimacs representation.
	 * @param hardConstraints a collection of formulas
	 * @param softConstraints a map mapping clauses to weights
	 * @param prop_index a map of propositions (=signature) to the indices that are used for writing the clauses.
	 * @return a list of strings in Dimacs CNF.
	 * @throws IllegalArgumentException if any soft constraint is not a clause.
	 */
	protected static List<String> convertToDimacsWcnf(Collection<PlFormula> hardConstraints, Map<PlFormula,Integer> softConstraints, Map<Proposition,Integer> prop_index) throws IllegalArgumentException{
		List<String> result = new LinkedList<>();
		String s;
		int num_clauses = 0;
		int sum_weight = 0;
		for(PlFormula f: softConstraints.keySet()) {
			sum_weight += softConstraints.get(f);
			if(f instanceof Proposition) {
				result.add(softConstraints.get(f) + " " + prop_index.get(f) + " 0");
			}else if(f.isLiteral()){
				result.add(softConstraints.get(f) + " -" +  prop_index.get((Proposition)((Negation)f).getFormula()) + " 0");
			}else if(!(f instanceof Disjunction)) {
				throw new IllegalArgumentException("Clause expected.");
			}else {
				s = softConstraints.get(f) + " ";
				for(PlFormula p2: (Disjunction) f){
					if(p2 instanceof Proposition)
						s += prop_index.get(p2) + " ";
					else if(p2.isLiteral()){
						s += "-" + prop_index.get((Proposition)((Negation)p2).getFormula()) + " ";
					}else throw new IllegalArgumentException("Clause expected.");
				}
				s += "0";
				result.add(s);
			}
		}
		sum_weight++;
		for(PlFormula p: hardConstraints){
			Conjunction conj;
			if (p.isClause()) {
				conj = new Conjunction();
				conj.add(p);
			} else
				conj = p.toCnf();
			for(PlFormula p1: conj){
				num_clauses++;
				// max weight as we have a hard clause
				s = sum_weight + " ";
				// as conj is in CNF all formulas should be disjunctions
				Disjunction disj = (Disjunction) p1;
				for(PlFormula p2: disj){
					if(p2 instanceof Proposition)
						s += prop_index.get(p2) + " ";
					else if(p2.isLiteral()){
						s += "-" + prop_index.get((Proposition)((Negation)p2).getFormula()) + " ";
					}else throw new RuntimeException("This should not happen: formula is supposed to be in CNF but another formula than a literal has been encountered.");
				}
				s += "0";
				result.add(s);
			}
		}
		result.add(0, "p wcnf " + prop_index.keySet().size() + " " + num_clauses + " " + sum_weight);
		return result;
	}


	/**
	 * Converts the given MaxSAT instance (i.e. hard and soft constraints, the
	 * latter can only be clauses) to their string representation in
	 * Dimacs WCNF and writes it to a temporary file. Note that a single formula may be represented as multiple
	 * clauses, so there is no simple correspondence between the formulas of the
	 * set and the Dimacs representation.
	 * @param hardConstraints a collection of formulas
	 * @param softConstraints a map mapping clauses to weights
	 * @param prop_index a map of propositions (=signature) to the indices that are used for writing the clauses.
	 * @return a string in Dimacs CNF.
	 * @throws IOException if some file issue occurs
	 */
	protected static File createTmpDimacsWcnfFile(Collection<PlFormula> hardConstraints, Map<PlFormula,Integer> softConstraints, Map<Proposition,Integer> prop_index) throws IOException{
		List<String> r = DimacsMaxSatSolver.convertToDimacsWcnf(hardConstraints, softConstraints, prop_index);
		File f = File.createTempFile("tweety-sat", ".wcnf", DimacsMaxSatSolver.tempFolder);
		f.deleteOnExit();
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		for(String s: r)
			writer.println(s);
		writer.close();
		return f;
	}

	@Override
	public boolean isSatisfiable(Collection<PlFormula> formulas) {
		Pair<Map<Proposition,Integer>,Map<Integer,Proposition>> i = DimacsSatSolver.getDefaultIndices(formulas);
		return this.isSatisfiable(formulas, i.getFirst());
	}


	/**
	 *
	 * Return "true" if the set is consistent.
	 * @param formulas  a set of formulas.
	 * @param prop_index maps propositions to the number that shall be used to
	 * 		represent it (a natural number > 0).
	 * @return "true" if the set is consistent.
	 */
	public abstract boolean isSatisfiable(Collection<PlFormula> formulas, Map<Proposition,Integer> prop_index);

	/**
	 * interpretation of formulas
	 */
	public Interpretation<PlBeliefSet, PlFormula> getWitness(Collection<PlFormula> hardConstraints, Map<PlFormula,Integer> softConstraints){
		Collection<PlFormula> set = new HashSet<PlFormula>();
		set.addAll(hardConstraints);
		set.addAll(softConstraints.keySet());
		Pair<Map<Proposition,Integer>,Map<Integer,Proposition>> i = DimacsSatSolver.getDefaultIndices(set);
		return this.getWitness(hardConstraints, softConstraints,i.getFirst(),i.getSecond());

	}

	@Override
	public Interpretation<PlBeliefSet, PlFormula> getWitness(Collection<PlFormula> formulas) {
		return this.getWitness(formulas, new HashMap<>());
	}
	/**
	 *
	 * Return the interpetation
	 * @param hardConstraints hard constraints for interpretation
	 * @param softConstraints  soft constraints for interpretation
	 * @param prop_index the index
	 * @param prop_inverted_index inverted index
	 * @return the interpetation
	 */
	public abstract Interpretation<PlBeliefSet, PlFormula> getWitness(Collection<PlFormula> hardConstraints, Map<PlFormula,Integer> softConstraints, Map<Proposition,Integer> prop_index, Map<Integer,Proposition> prop_inverted_index);


    /** Default Constructor */
    public DimacsMaxSatSolver(){}
}
