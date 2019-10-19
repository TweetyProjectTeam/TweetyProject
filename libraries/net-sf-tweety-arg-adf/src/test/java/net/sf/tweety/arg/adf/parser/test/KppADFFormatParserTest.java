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
package net.sf.tweety.arg.adf.parser.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import net.sf.tweety.arg.adf.parser.KppADFFormatParser;
import net.sf.tweety.commons.ParserException;

public class KppADFFormatParserTest {

	public static final int DEFAULT_TIMEOUT = 2000;

	private KppADFFormatParser parser = new KppADFFormatParser();

	private void parseAllInDirectory(String dir) throws ParserException, FileNotFoundException, IOException {
		File[] instances = new File(dir).listFiles((File f, String name) -> name.endsWith(".adf"));
		for (File f : instances) {
			parser.parseBeliefBaseFromFile(f.getPath());
		}
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testAllValid() throws IOException, ParserException {
		parseAllInDirectory("src/test/resources/valid");
	}

	@Test(timeout = 4000)
	public void testAllKppADFInstances() throws IOException, ParserException {
		parseAllInDirectory("src/test/resources/k++adf_instances");
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testNoLinebreak() throws ParserException, IOException {
		String input = "s(1). s(2). ac(1,and(1,2)).";
		parser.parseBeliefBase(input);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testNoWhitespace() throws ParserException, IOException {
		String input = "s(1).s(2).ac(1,and(1,2)).";
		parser.parseBeliefBase(input);
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = ParserException.class)
	public void testMissingDot4() throws ParserException, IOException {
		String input = "s(1) s(2) ac(1,and(1,2))";
		parser.parseBeliefBase(input);
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = ParserException.class)
	public void testMissingDot3() throws ParserException, IOException {
		String input = "s(1). s(2). ac(1,and(1,2))";
		parser.parseBeliefBase(input);
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = ParserException.class)
	public void testMissingDot2() throws ParserException, IOException {
		String input = "s(1). s(2) ac(1,and(1,2)).";
		parser.parseBeliefBase(input);
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = ParserException.class)
	public void testMissingDot1() throws ParserException, IOException {
		String input = "s(1) s(2). ac(1,and(1,2)).";
		parser.parseBeliefBase(input);
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = ParserException.class)
	public void testUndefinedArgumentInACC() throws ParserException, IOException {
		String input = "s(1). ac(1,and(1,2)).";
		parser.parseBeliefBase(input);
	}

}
