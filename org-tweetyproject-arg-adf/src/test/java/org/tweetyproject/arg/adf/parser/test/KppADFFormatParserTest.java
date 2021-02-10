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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.parser.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.tweetyproject.arg.adf.io.KppADFFormatParser;
import org.tweetyproject.arg.adf.sat.solver.NativeMinisatSolver;
import org.tweetyproject.arg.adf.semantics.link.SatLinkStrategy;

public class KppADFFormatParserTest {

	public static final int DEFAULT_TIMEOUT = 2000;

	private KppADFFormatParser parser = new KppADFFormatParser(new SatLinkStrategy(new NativeMinisatSolver()), false);

	private void parseAllInDirectory(String dir) throws FileNotFoundException, IOException {
		File[] instances = new File(dir).listFiles((File f, String name) -> name.endsWith(".adf"));
		for (File f : instances) {
			parser.parse(f);
		}
	}

//	@Test(timeout = DEFAULT_TIMEOUT)
	public void testAllValid() throws IOException {
		parseAllInDirectory("src/test/resources/valid");
	}

//	@Test(timeout = 4000)
	public void testAllKppADFInstances() throws IOException {
		parseAllInDirectory("src/test/resources/k++adf_instances");
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testNoLinebreak() throws IOException {
		String input = "s(1). s(2). ac(1,and(1,2)).";
		parser.parse(input);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testNoWhitespace() throws IOException {
		String input = "s(1).s(2).ac(1,and(1,2)).";
		parser.parse(input);
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = IOException.class)
	public void testMissingDot4() throws IOException {
		String input = "s(1) s(2) ac(1,and(1,2))";
		parser.parse(input);
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = IOException.class)
	public void testMissingDot3() throws IOException {
		String input = "s(1). s(2). ac(1,and(1,2))";
		parser.parse(input);
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = IOException.class)
	public void testMissingDot2() throws IOException {
		String input = "s(1). s(2) ac(1,and(1,2)).";
		parser.parse(input);
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = IOException.class)
	public void testMissingDot1() throws IOException {
		String input = "s(1) s(2). ac(1,and(1,2)).";
		parser.parse(input);
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = IOException.class)
	public void testUndefinedArgumentInACC() throws IOException {
		String input = "s(1). ac(1,and(1,2)).";
		parser.parse(input);
	}

}
