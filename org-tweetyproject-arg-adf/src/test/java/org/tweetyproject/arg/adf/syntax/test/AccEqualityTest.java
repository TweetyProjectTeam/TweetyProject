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
package org.tweetyproject.arg.adf.syntax.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.EquivalenceAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ImplicationAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.NegationAcceptanceCondition;

/**
 * @author Mathias Hofer
 *
 */
public class AccEqualityTest {

	@Test
	public void testEquality1() {
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		AcceptanceCondition acc1 = new EquivalenceAcceptanceCondition(a, b);
		AcceptanceCondition acc2 = new EquivalenceAcceptanceCondition(a, b);
		assertTrue(acc1.equals(acc2));
	}
	
	@Test
	public void testEquality2() {
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		AcceptanceCondition acc1 = new EquivalenceAcceptanceCondition(new NegationAcceptanceCondition(a), b);
		AcceptanceCondition acc2 = new EquivalenceAcceptanceCondition(new NegationAcceptanceCondition(a), b);
		assertTrue(acc1.equals(acc2));
	}
	
	@Test
	public void testEquality3() {
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		AcceptanceCondition acc1 = new EquivalenceAcceptanceCondition(a, b);
		AcceptanceCondition acc2 = new EquivalenceAcceptanceCondition(b, a);
		assertTrue(acc1.equals(acc2));
	}
	
	@Test
	public void testUnequality1() {
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		AcceptanceCondition acc1 = new ImplicationAcceptanceCondition(a, b);
		AcceptanceCondition acc2 = new ImplicationAcceptanceCondition(b, a);
		assertFalse(acc1.equals(acc2));
	}
	
	@Test
	public void testUnequality2() {
		Argument a = new Argument("a");
		Argument b = new Argument("a");
		assertFalse(a.equals(b));
	}
		
}
