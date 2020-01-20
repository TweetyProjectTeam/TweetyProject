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
package net.sf.tweety.logics.qbf.test;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.qbf.parser.QCirParser;
import net.sf.tweety.logics.qbf.syntax.ExistsQuantifiedFormula;
import net.sf.tweety.logics.qbf.syntax.ForallQuantifiedFormula;

import org.junit.Test;

/**
 * Test class for parsing QCIR files.
 * 
 * @author Anna Gessler
 *
 */
public class QCIRTest {
	public static final int DEFAULT_TIMEOUT = 5000;

	@Test(timeout = DEFAULT_TIMEOUT)
	public void SimpleBeliefBaseTest() throws FileNotFoundException, ParserException, IOException {
		QCirParser parser = new QCirParser();
		PlBeliefSet b = parser.parseBeliefBaseFromFile("src/main/resources/qcir-example1.qcir");
		System.out.println(b.size());
		assertTrue(b.size() == 3);
		assertTrue(parser.getOutputVariable() instanceof ExistsQuantifiedFormula);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ComplexBeliefBaseTest() throws FileNotFoundException, ParserException, IOException {
		QCirParser parser = new QCirParser();
		PlBeliefSet b = parser.parseBeliefBaseFromFile("src/main/resources/qcir-example2-sat.qcir");
		System.out.println(b.size() == 34);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void SingleFormulasTest() throws ParserException, IOException {
		QCirParser parser = new QCirParser();
		PlFormula f1 = parser.parseFormula("g1 = and(v1,v2)");
		PlFormula f2 = parser.parseFormula("g2 = or(v1,v2)");
		PlFormula f3 = parser.parseFormula("g3 = xor(v1,v2)");
		PlFormula f4 = parser.parseFormula("g4 = ite(v1,v2,v3)");
		PlFormula f5 = parser.parseFormula("g5 = forall(v1,v2; g1)");
		assertTrue(f1 instanceof Conjunction);
		assertTrue(f2 instanceof Disjunction);
		assertTrue(f3 instanceof Disjunction);
		assertTrue(f4 instanceof Disjunction);
		assertTrue(f5 instanceof ForallQuantifiedFormula);
	}
	
}
