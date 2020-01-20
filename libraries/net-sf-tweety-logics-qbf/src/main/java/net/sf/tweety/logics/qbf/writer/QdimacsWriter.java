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
package net.sf.tweety.logics.qbf.writer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.qbf.syntax.ExistsQuantifiedFormula;
import net.sf.tweety.logics.qbf.syntax.ForallQuantifiedFormula;

/**
 * Converts belief bases to QDIMACS format and prints them. <br>
 * <br>
 * Notes: <br>
 * - Currently only works for belief bases that have only quantifiers in the
 * left portion of each formula and have only unquantified formulas in the right
 * portion of each formula. <br>
 * - The right portion of the formulas does
 * not need to be in any special form (it will be converted to cnf).
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

	public String printBase(PlBeliefSet kb) throws IOException {
		// Map the literals to numbers according to their natural order returned by the
		// signature's iterator
		Map<Proposition, Integer> mappings = new HashMap<Proposition, Integer>();
		int mi = 1;
		for (Proposition p : kb.getMinimalSignature()) {
			mappings.put(p, mi);
			mi++;
		}

		// Collect nested quantifications 
		String quantifiers = "";
		PlBeliefSet clauses_only = new PlBeliefSet();
		for (PlFormula f : kb) {
			boolean nestedFormula = true;
			PlFormula temp = f;
			String lastQuantifier = ""; //used for tracking consecutive quantifiers of the same type (not allowed in QDIMACS)
			while (nestedFormula) {
				nestedFormula = false;
				if (temp instanceof ExistsQuantifiedFormula) {
					//concatenate consecutive quantifiers of the same type 
					if (lastQuantifier.equals("e"))
						quantifiers = quantifiers.substring(0,quantifiers.length()-3) + printVariables(((ExistsQuantifiedFormula) temp).getQuantifierVariables(), mappings) + " 0\n";
					else
						quantifiers += "e" + printVariables(((ExistsQuantifiedFormula) temp).getQuantifierVariables(), mappings) + " 0\n";
					nestedFormula = true;
					temp = ((ExistsQuantifiedFormula) temp).getFormula();
					lastQuantifier = "e";

				} else if (temp instanceof ForallQuantifiedFormula) {
					//concatenate consecutive quantifiers of the same type 
					if (lastQuantifier.equals("a"))
						quantifiers = quantifiers.substring(0,quantifiers.length()-3) + printVariables(((ForallQuantifiedFormula) temp).getQuantifierVariables(), mappings) + " 0\n";
					else
						quantifiers += "a" + printVariables(((ForallQuantifiedFormula) temp).getQuantifierVariables(), mappings) + " 0\n";
					nestedFormula = true;
					temp = ((ForallQuantifiedFormula) temp).getFormula();
					lastQuantifier = "a";
				}
			}
			clauses_only.add(f.toCnf());
		}
		
		//Collect clauses with standard dimacs converter
		String dimacs_clauses = SatSolver.convertToDimacs(clauses_only).getFirst();
		int first_line_end = dimacs_clauses.indexOf("\n");
		String preamble = dimacs_clauses.substring(0, first_line_end) + " 0\n";
		if (DISABLE_PREAMBLE_ZERO)
			preamble = dimacs_clauses.substring(0, first_line_end) + "\n";
		String clauses = dimacs_clauses.substring(first_line_end + 1);

		writer.write(preamble);
		writer.write(quantifiers);
		writer.write(clauses);
		
		return preamble + quantifiers + clauses;
	}

	public String printVariables(Set<Proposition> vars, Map<Proposition, Integer> mappings) {
		String result = "";
		for (Proposition v : vars)
			result += " " + mappings.get(v);
		return result;
	}

	public void close() throws IOException {
		writer.close();
	}

}
