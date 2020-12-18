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
package net.sf.tweety.lp.asp.writer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.lp.asp.syntax.ASPRule;
import net.sf.tweety.lp.asp.syntax.Program;

/**
 * Prints ASP programs and single rules to the Clingo input format
 * (<a href="https://potassco.org/clingo/">https://potassco.org/clingo/</a>).
 * The Clingo input format adheres (mostly) to the ASP-Core-2 language standard.
 * This writer also works for printing basic elements of the DLV input format.
 * 
 * @see net.sf.tweety.lp.asp.reasoner.ClingoSolver
 * @see net.sf.tweety.lp.asp.reasoner.DLVSolver
 * 
 * @author Anna Gessler
 */

public class ClingoWriter {

	Writer writer;

	/**
	 * If set to true, irrelevant atoms are hidden from the output using clingo's
	 * #show statement.
	 */
	private boolean usePredicateWhitelist = false;

	/**
	 * Create a new ClingoWriter with the given writer.
	 * 
	 * @param writer a writer
	 */
	public ClingoWriter(Writer writer) {
		this.writer = writer;
	}

	/**
	 * Create a new ClingoWriter.
	 */
	public ClingoWriter() {
		this.writer = new StringWriter();
	}

	/**
	 * Create a new ClingoWriter with the given writer and options.
	 * 
	 * @param writer
	 * @param usePredicateWhitelist if set to true, irrelevant atoms are hidden from
	 *                              the output using clingo's #show statement.
	 */
	public ClingoWriter(Writer writer, boolean usePredicateWhitelist) {
		this.writer = writer;
		this.usePredicateWhitelist = usePredicateWhitelist;
	}

	/**
	 * Prints the given program in clingo format.
	 * 
	 * @param p a program
	 * @throws IOException if an IO issue occurs.
	 */
	public void printProgram(Program p) throws IOException {
		for (ASPRule r : p)
			writer.write(printRule(r) + "\n");

		// Optionally suppress irrelevant atoms from output.
		if (usePredicateWhitelist) {
			for (Predicate pr : p.getOutputWhitelist())
				writer.write("\n #show " + pr.getName() + "/" + pr.getArity() + ".\n");
		}
	}

	/**
	 * Creates string representation of a single rule in clingo format.
	 * 
	 * @param r an ASP rule
	 * @return String representation of the rule
	 */
	private String printRule(ASPRule r) {
		return r.printToClingo();
	}

	public void close() throws IOException {
		this.writer.close();
	}

	public void usePredicateWhitelist(boolean b) {
		this.usePredicateWhitelist = b;
	}

}
