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
package org.tweetyproject.logics.qbf.parser;

import java.io.*;
import java.util.*;

import org.tweetyproject.commons.*;
import org.tweetyproject.logics.pl.parser.DimacsParser;
import org.tweetyproject.logics.pl.syntax.*;
import org.tweetyproject.logics.qbf.syntax.ExistsQuantifiedFormula;
import org.tweetyproject.logics.qbf.syntax.ForallQuantifiedFormula;

/**
 * This class implements a parser for the QDIMACS input format. It is an
 * extension of the DIMACS format used in sat solver competitions.
 * See <a href="http://www.qbflib.org/qdimacs.html">http://www.qbflib.org/qdimacs.html</a> for more information.
 * <br>
 * <br>
 * The BNF for a QDIMACS input file is given by (starting symbol is
 * QDIMACS-FILE): <br>
 * <br>
 * QDIMACS-FILE ::== PREAMBLE PREFIX MATRIX <br>
 * PREAMBLE ::== "p" "cnf" PNUM PNUM "\n" <br>
 * PREFIX ::== [QUANT_SETS] <br>
 * QUANT_SETS ::== QUANT_SET QUANT_SETS | QUANT_SET <br>
 * QUANT_SET ::== QUANTIFIER ATOM_SET "0\n" <br>
 * QUANTIFIER ::== "e" | "a" <br>
 * ATOM_SET ::== PNUM ATOM_SET | PNUM <br>
 * MATRIX ::== CLAUSE_LIST <br>
 * CLAUSE_LIST ::== CLAUSE CLAUSE_LIST | CLAUSE <br>
 * CLAUSE ::== LITERAL CLAUSE | LITERAL "0\n" <br>
 * <br>
 * Lines starting with "c" and consisting of non-special-ASCII characters
 * (comments) are ignored. <br>
 * LITERAL is a signed integer != 0. <br>
 * PNUM is a signed integer &gt; 0. <br>
 * The two numbers in PREAMBLE specify the number of variables and the number of
 * clauses. <br>
 * <br>
 *
 * To parse QDIMACS output files, use {@link org.tweetyproject.logics.qbf.parser.QdimacsParser#parseQDimacsOutput(String)}
 *
 * @author Anna Gessler
 */
public class QdimacsParser extends DimacsParser {


	/** Default */
	public QdimacsParser(){
		super();
	}
	/**
	 * List for temporarily storing the quantification lines of the input file.
	 * The rest of the input file is parsed with {@link org.tweetyproject.logics.pl.parser.DimacsParser}.
	 */
	private List<String> quantifications = new ArrayList<String>();

	@Override
	public PlBeliefSet parseBeliefBase(Reader reader) throws IOException, ParserException {
		String dimacs = "";
		String s = "";
		try {
			for (int c = reader.read(); c != -1; c = reader.read()) {
				if (c == 10 || c == 13) {
					if (s.trim().startsWith("a") || s.trim().startsWith("e")) {
						quantifications.add(s.trim());
					} else
						dimacs += s + (char) c;
					s = "";
				} else {
					s += (char) c;
				}
			}
			if (s.trim().startsWith("a") || s.trim().startsWith("e")) {
				quantifications.add(s.trim());
			} else
				dimacs += s;
		} catch (Exception e) {
			throw new ParserException(e);
		}

		//Parse the cnf lines of the input with the normal dimacs parser
		PlBeliefSet dimacsBeliefSet = super.parseBeliefBase(new StringReader(dimacs));
		PlFormula temp = new Conjunction(dimacsBeliefSet);

		//Parse the quantifier lines
		for (String q : quantifications) {
			Set<Proposition> vars = new HashSet<Proposition>();
			String ids = "";
			for (int i = 2; i < q.length(); i++) {
				if (q.charAt(i) == '0')
					break;
				if (q.charAt(i) == ' ') {
					int idx = Integer.parseInt(ids);
					vars.add(super.prop_idx[idx - 1]);
					ids = "";
				}
				else
					ids += q.charAt(i);
			}
			if (q.startsWith("a")) {
				temp = new ForallQuantifiedFormula(temp, vars);
			} else if (q.startsWith("e")) {
				temp = new ExistsQuantifiedFormula(temp, vars);
			}
			else
				throw new IllegalArgumentException("Unknown quantifier in line " + q);
		}

		PlBeliefSet qdimacsBeliefSet = new PlBeliefSet();
		qdimacsBeliefSet.add(temp);
		return qdimacsBeliefSet;
	}

	/**
	 * Possible answers that solvers can find for a given QDIMACS problem.
	 */
	public enum Answer {
		/** Satisfiable */
		SAT,
		/** Unsat */
		UNSAT,
		/** Unknown */
		UNKNOWN
	}

	/**
	 * Parses the answer from a given QDIMACS output file. The rest of the file is
	 * ignored.
	 *
	 * @param output string containing a QDIMACS output file
	 * @return either SAT, UNSAT or UNKNOWN
	 */
	public Answer parseQDimacsOutput(String output) {
		if (!output.contains("s cnf"))
			throw new IllegalArgumentException("The given QDIMACS output file contains no solution line.");
		String[] solutionLine = output.split("s cnf");
		String answer = solutionLine[1].strip();
		if (answer.charAt(0) == '1')
			return Answer.SAT;
		else if (answer.charAt(0) == '0')
			return Answer.UNSAT;
		return Answer.UNKNOWN;
	}

}
