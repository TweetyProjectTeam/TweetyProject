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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.qbf.writer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.logics.pl.sat.DimacsSatSolver;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Contradiction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;
import org.tweetyproject.logics.qbf.syntax.ExistsQuantifiedFormula;
import org.tweetyproject.logics.qbf.syntax.ForallQuantifiedFormula;

/**
 * Converts belief bases to QDIMACS format and prints them. <br>
 * <br>
 * Notes: <br>
 * - Currently only works for belief bases that have only quantifiers in the
 * left portion of each formula and have only unquantified formulas in the right
 * portion of each formula. <br>
 * - The right portion of the formulas does not need to be in any special form
 * (it will be converted to cnf).
 * 
 * @author Anna Gessler
 */
public class QdimacsWriter {
	/**
	 * Output is redirected to this writer
	 */
	Writer writer;

	/**
	 * Creates a new QDIMACS writer.
	 * 
	 * @param writer
	 */
	public QdimacsWriter(Writer writer) {
		this.writer = writer;
	}
	/**
	 * Creates a new QDIMACS writer.
	 */
	public QdimacsWriter() {
		this.writer = new StringWriter();
	}

	/**
	 * Removes zero at the end of the problem line (workaround for some solvers).
	 */
	public boolean DISABLE_PREAMBLE_ZERO = false;
	
	/**
	 * Will be set to true/false if the input can be simplified to a tautology/contradiction.
	 * Used in some of the solver wrappers to immediately return true/false instead of calling the solver
	 * in case of a tautology/contradiction.
	 */
	public Boolean special_formula_flag = null;

	public String printBase(PlBeliefSet kb) throws IOException {
		// Map the literals to numbers (indices of the list)
		Map<Proposition,Integer> mappings = new HashMap<Proposition,Integer>();
		int index = 1;
		for (PlFormula f : kb) {
			for(Proposition p: f.getAtoms())
				if(!mappings.containsKey(p))
					mappings.put(p, index++);
		}

		// Collect nested quantifications
		String quantifiers = "";
		PlBeliefSet clauses_only = new PlBeliefSet();
		for (PlFormula f : kb) {
			f = getForallReduct(f); // last quantifier must be existential in QDIMACS
			boolean nestedFormula = true;
			PlFormula temp = f;
			String lastQuantifier = ""; // used for tracking consecutive quantifiers of the same type (not allowed in
										// QDIMACS)

			while (nestedFormula) {
				nestedFormula = false;
				if (temp instanceof ExistsQuantifiedFormula) {
					// concatenate consecutive quantifiers of the same type
					if (lastQuantifier.equals("e"))
						quantifiers = quantifiers.substring(0, quantifiers.length() - 3)
								+ printVariables(((ExistsQuantifiedFormula) temp).getQuantifierVariables(), mappings)
								+ " 0\n";
					else
						quantifiers += "e"
								+ printVariables(((ExistsQuantifiedFormula) temp).getQuantifierVariables(), mappings)
								+ " 0\n";
					nestedFormula = true;
					temp = ((ExistsQuantifiedFormula) temp).getFormula();
					lastQuantifier = "e";

				} else if (temp instanceof ForallQuantifiedFormula) {

					// concatenate consecutive quantifiers of the same type
					if (lastQuantifier.equals("a"))
						quantifiers = quantifiers.substring(0, quantifiers.length() - 3)
								+ printVariables(((ForallQuantifiedFormula) temp).getQuantifierVariables(), mappings)
								+ " 0\n";
					else
						quantifiers += "a"
								+ printVariables(((ForallQuantifiedFormula) temp).getQuantifierVariables(), mappings)
								+ " 0\n";
					nestedFormula = true;
					temp = ((ForallQuantifiedFormula) temp).getFormula();
					lastQuantifier = "a";
				}
			}

			clauses_only.add(f.toCnf());
		}

		//check for tautologies and contradictions 
		//because they can't be directly represented in QDIMACS format
		Conjunction cnf = new Conjunction();
		cnf.addAll(clauses_only);
		Conjunction simplified_cnf = simplify_special_formulas(cnf);
		if (simplified_cnf.size() == 1 && simplified_cnf.iterator().next() instanceof Tautology) {
			this.special_formula_flag = true;
			return "TRUE"; 
		}
		else if (simplified_cnf.size() == 1 && simplified_cnf.iterator().next() instanceof Contradiction) {
			this.special_formula_flag = false;
			return "FALSE"; 
		}
		
		// Collect clauses with standard dimacs converter
		String dimacs_clauses = DimacsSatSolver.convertToDimacs(simplified_cnf, mappings, "", 0);
		int first_line_end = dimacs_clauses.indexOf("\n");
		String preamble = dimacs_clauses.substring(0, first_line_end) + " 0\n";
		if (DISABLE_PREAMBLE_ZERO)
			preamble = dimacs_clauses.substring(0, first_line_end) + "\n";
		String clauses = dimacs_clauses.substring(first_line_end + 1);

		writer.write(preamble);
		writer.write(quantifiers);
		writer.write(clauses);

		return preamble + quantifiers.strip() + clauses.strip();
	}

	/**
	 * Simplify clauses that contain tautologies or contradictions.
	 * 
	 * @param a formula in cnf (if it is not in cnf already, it will be converted
	 *          to cnf)
	 * @return simplified belief set
	 */
	private Conjunction simplify_special_formulas(PlFormula f) {
			Conjunction c = f.toCnf();
			Conjunction c_simplified = new Conjunction();
			boolean found_contradiction = false;
			for (PlFormula fd : c) {
				Disjunction d = (Disjunction) fd;
				Disjunction d_simplified = new Disjunction();
				for (PlFormula l : d) {
					// (+ || x || ...)  <=> +
					if (l instanceof Tautology) {
						d_simplified = new Disjunction();
						break;
					} 
					// (- || x || ...) <=> (x ...)
					else if (l instanceof Contradiction) 
						found_contradiction = true;
					else 
						d_simplified.add(l);
				}
				// border case: disjunct consists only of "-"
				// the whole conjunction is false
				if (d_simplified.isEmpty() && found_contradiction) {
					c_simplified = new Conjunction();
					c_simplified.add(new Contradiction()); 
					return c_simplified;
				}
				else if (!d_simplified.isEmpty())
					c_simplified.add(d_simplified);
			}
			// border case: all disjuncts are tautologies
			if (c_simplified.isEmpty()) 
				c_simplified.add(new Tautology());
		return c_simplified;
	}

	/**
	 * Checks if the innermost quantifier of the formula is universal (not allowed
	 * in QDimacs format) and eliminates it if needed. An innermost universal
	 * quantifier can be eliminated by removing all occurrences of the bound
	 * variables (e.g. "forall a: (a || b)" is true iff "b" is true).
	 * 
	 * @param f a PlFormula
	 * @return the forall-reduct of f
	 */
	private PlFormula getForallReduct(PlFormula f) {
		while (getFinalQuantification(f) instanceof ForallQuantifiedFormula) {
			ForallQuantifiedFormula innermostQuantification = (ForallQuantifiedFormula) getFinalQuantification(f);
			Set<Proposition> vars = innermostQuantification.getQuantifierVariables();
			Conjunction fReduct = new Conjunction();
			for (PlFormula fd : f.toCnf()) {
				// As the formula is in CNF, we can simply remove the
				// literals containing the bound variables from the disjunctions
				Disjunction d = (Disjunction) fd;
				for (Proposition v : vars) {
					d.remove(v);
					d.remove(new Negation(v));
				}
				fReduct.add(d);
			}
			f = fReduct;
		}
		return f;
	}

	/**
	 * Get the innermost quantification of the given formula.
	 * 
	 * @param f PlFormula
	 * @return the innermost quantification of f, including the quantifier
	 *         variables, i.e. an instance of ExistsQuantifiedFormula or
	 *         ForallQuantifiedFormula
	 */
	private PlFormula getFinalQuantification(PlFormula f) {
		PlFormula finalFormula = f;
		PlFormula finalFormulaTemp = f;
		while (finalFormulaTemp instanceof ExistsQuantifiedFormula
				|| finalFormulaTemp instanceof ForallQuantifiedFormula) {
			finalFormula = finalFormulaTemp;
			if (finalFormulaTemp instanceof ExistsQuantifiedFormula)
				finalFormulaTemp = ((ExistsQuantifiedFormula) finalFormulaTemp).getFormula();
			else
				finalFormulaTemp = ((ForallQuantifiedFormula) finalFormulaTemp).getFormula();
		}
		return finalFormula;
	}

	public String printVariables(Set<Proposition> vars, Map<Proposition,Integer> mappings) {
		String result = "";
		for (Proposition v : vars)
			result += " " + (mappings.get(v));
		return result;
	}

	public void close() throws IOException {
		writer.close();
	}

}