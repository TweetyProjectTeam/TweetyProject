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

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Implication;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.qbf.parser.QbfParser;
import net.sf.tweety.logics.qbf.syntax.ExistsQuantifiedFormula;
import net.sf.tweety.logics.qbf.syntax.ForallQuantifiedFormula;

import org.junit.Test;

/**
 * Test class for basic qbf functionalities.
 * 
 * @author Anna Gessler
 *
 */
public class QbfTest {
	public static final int DEFAULT_TIMEOUT = 2000;
	
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void TypesTest() {
		Proposition a = new Proposition("abba");
		ExistsQuantifiedFormula ef = new ExistsQuantifiedFormula(a,a);
		ForallQuantifiedFormula af = new ForallQuantifiedFormula(a,a);
		assertTrue(ef.getQuantifierVariables().contains(a));
		
		//TODO more tests for functions
		PlBeliefSet bs = new PlBeliefSet();
		bs.add(ef);
		bs.add(af);
		assertTrue(bs.getMinimalSignature().size() == 1);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ParseQuantifiedFormulaTest() throws ParserException, IOException {
		QbfParser parser = new QbfParser();
		PlFormula f1 = parser.parseFormula("forall a: (a => !b)");
		assertTrue(f1 instanceof ForallQuantifiedFormula);
		ForallQuantifiedFormula f2 = (ForallQuantifiedFormula) f1;
		assertTrue(f2.getFormula() instanceof Implication);
		Implication f3 = (Implication) f2.getFormula();
		assertTrue(f3.getFormulas().getFirst().equals(new Proposition("a")));
		assertTrue(f3.getFormulas().getSecond().equals(new Negation(new Proposition("b"))));
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ParseNestedQuantifiedFormulaTest() throws ParserException, IOException {
		QbfParser parser = new QbfParser();
		PlFormula f1 = parser.parseFormula("a && exists e: (forall a: (!a) || e) && d");
		assertTrue(f1 instanceof Conjunction);
		assertTrue(f1.getAtoms().size() == 3);
		Conjunction f2 = (Conjunction) f1;
		assertTrue(((Conjunction)f2.get(1)).get(0) instanceof ExistsQuantifiedFormula);
	}
	
}
