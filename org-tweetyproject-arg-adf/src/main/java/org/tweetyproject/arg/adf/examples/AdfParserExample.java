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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.examples;

import java.io.IOException;
import java.nio.file.Path;

import org.tweetyproject.arg.adf.io.KppAdfParser;
import org.tweetyproject.arg.adf.io.KppAdfWriter;
import org.tweetyproject.arg.adf.reasoner.AbstractAdfReasoner;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.semantics.Semantics;

/**
 * This example shows how to read ADFs in the k++ADF format from a file and
 * from a string, and how to write them.
 *
 * @author Lars Bengel
 */
public class AdfParserExample {

	/**
	 * Default constructor
	 */
	public AdfParserExample() {
		super();
	}

	/**
	 * Parses an ADF from a file and one from a string and prints their
	 * preferred interpretations.
	 *
	 * @param args not used
	 * @throws IOException if the file cannot be read
	 */
	public static void main(String[] args) throws IOException {
		KppAdfParser parser = new KppAdfParser();
		AbstractAdfReasoner reasoner = AbstractAdfReasoner.getSatReasonerForSemantics(Semantics.PR);

		// from a file
		AbstractDialecticalFramework fromFile = parser.parse(Path.of("src/main/resources/adf_example.txt"));
		System.out.println(fromFile);
		System.out.println("PR: " + reasoner.getModels(fromFile));

		// from a string
		AbstractDialecticalFramework fromString = parser.parse("s(a). s(b). s(c). ac(a,c(v)). ac(b,or(neg(a),c)). ac(c,b).");
		System.out.println(fromString);
		System.out.println("PR: " + reasoner.getModels(fromString));

		// back to the k++ADF format
		System.out.println(new KppAdfWriter().write(fromString));
	}

}
