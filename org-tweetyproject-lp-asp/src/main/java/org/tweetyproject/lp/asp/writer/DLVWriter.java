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
package org.tweetyproject.lp.asp.writer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.tweetyproject.lp.asp.syntax.ASPRule;
import org.tweetyproject.lp.asp.syntax.Program;

/**
 * Prints ASP programs and single rules to the DLV input format
 * (<a href="http://www.dlvsystem.com/html/DLV_User_Manual.html">http://www.dlvsystem.com/html/DLV_User_Manual.html</a>).
 *
 * @see org.tweetyproject.lp.asp.reasoner.DLVSolver
 *
 * @author Anna Gessler
 */

public class DLVWriter {

	Writer writer;

	/**
	 * Create a new DLVWriter with the given writer.
	 *
	 * @param writer a writer
	 */
	public DLVWriter(Writer writer) {
		this.writer = writer;
	}

	/**
	 * Create a new DLVWriter.
	 */
	public DLVWriter() {
		this.writer = new StringWriter();
	}

	/**
	 * Prints the given program in DLV format.
	 *
	 * @param p a program
	 * @throws IOException if an IO issue occurs.
	 */
	public void printProgram(Program p) throws IOException {
		for (String cmd : p.getAdditionalOptions()) {
			if (cmd.startsWith("#const"))
				writer.write(cmd + ".");
		}

		for (ASPRule r : p)
			writer.write(printRule(r) + "\n");
	}

	/**
	 * Creates string representation of a single rule in DLV format.
	 *
	 * @param r an ASP rule
	 * @return String representation of the rule
	 */
	private String printRule(ASPRule r) {
		return r.printToDLV();
	}

	/**
	 * Close writer
	 * @throws IOException error
	 */
	public void close() throws IOException {
		this.writer.close();
	}

}
