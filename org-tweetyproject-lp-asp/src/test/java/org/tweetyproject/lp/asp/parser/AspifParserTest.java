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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.lp.asp.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.tweetyproject.lp.asp.syntax.ASPRule;
import org.tweetyproject.lp.asp.syntax.ChoiceHead;
import org.tweetyproject.lp.asp.syntax.Program;

/**
 * Tests for AspifParser (parser for the the output language of gringo).
 * 
 * @see org.tweetyproject.lp.asp.parser.AspifParser
 *
 * @author Anna Gessler
 *
 */
public class AspifParserTest {
	
	public static final int DEFAULT_TIMEOUT = 5000;
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void SimpleProgramTest() throws ParseException, FileNotFoundException, IOException {
		AspifParser aspifParser = new AspifParser();
		Program p = aspifParser.parseProgram("asp 1 0 0\n" 
				+ "1 1 1 1 0 0\n" 
				+ "1 0 1 2 0 1 1\n"
				+ "1 0 1 3 0 1 -1\n" 
				+ "4 1 a 1 1\n" 
				+ "4 1 b 1 2\n" 
				+ "4 1 c 1 3\n" + "0");
		assertEquals(p.size(),3);
		assertTrue(p.getMinimalSignature().containsPredicate("b"));
		assertTrue(p.getMinimalSignature().containsPredicate("a"));
		assertTrue(p.getMinimalSignature().containsPredicate("c"));
		boolean found = false;
		for (ASPRule r : p) {
			if (r.getHead() instanceof ChoiceHead) {
				found = true;
			}
		}
		assertTrue(found);
	}

}
