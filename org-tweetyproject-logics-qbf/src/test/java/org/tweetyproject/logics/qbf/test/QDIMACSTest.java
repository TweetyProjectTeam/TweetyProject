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
package org.tweetyproject.logics.qbf.test;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.qbf.parser.QdimacsParser;
import org.tweetyproject.logics.qbf.syntax.ExistsQuantifiedFormula;

import org.junit.Test;

/**
 * Test class for parsing QDIMACS files.
 * 
 * @author Anna Gessler
 *
 */
public class QDIMACSTest {
	public static final int DEFAULT_TIMEOUT = 2000;
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void SimpleBeliefBaseTest() throws FileNotFoundException, ParserException, IOException {
		QdimacsParser parser = new QdimacsParser();
		PlBeliefSet b = parser.parseBeliefBaseFromFile("src/main/resources/qdimacs-example1.qdimacs");;
		assertTrue(b.iterator().next() instanceof ExistsQuantifiedFormula);
		QdimacsParser.Answer answer = parser
				.parseQDimacsOutput("c a comment \n" + "s cnf 1 2 2 0 \n" + "2 -1 0 \n" + "1 -2 0   ");
		assertTrue(answer.equals(QdimacsParser.Answer.SAT));
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ComplexBeliefBaseTest() throws FileNotFoundException, ParserException, IOException {
		QdimacsParser parser = new QdimacsParser();
		PlBeliefSet b = parser.parseBeliefBaseFromFile("src/main/resources/qdimacs-example2.qdimacs");
		assertTrue(b.iterator().next() instanceof ExistsQuantifiedFormula);
		ExistsQuantifiedFormula e = (ExistsQuantifiedFormula) b.iterator().next();
		assertTrue(e.getQuantifierVariables().size()==5);
	}
	
}
