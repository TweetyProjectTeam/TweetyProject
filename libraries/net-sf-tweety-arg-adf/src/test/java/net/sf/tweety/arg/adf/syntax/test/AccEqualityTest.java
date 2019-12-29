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
package net.sf.tweety.arg.adf.syntax.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sf.tweety.arg.adf.syntax.AcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.ContradictionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.EquivalenceAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.NegationAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.TautologyAcceptanceCondition;

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
		assertTrue(new ContradictionAcceptanceCondition().equals(new ContradictionAcceptanceCondition()));
	}
	
	@Test
	public void testEquality4() {
		assertTrue(new TautologyAcceptanceCondition().equals(new TautologyAcceptanceCondition()));
	}
	
	@Test
	public void testEquality5() {
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		AcceptanceCondition acc1 = new EquivalenceAcceptanceCondition(a, b);
		AcceptanceCondition acc2 = a.builder().iff(b).build();
		assertTrue(acc1.equals(acc2));
	}
	
	@Test
	public void testUnequality1() {
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		AcceptanceCondition acc1 = new EquivalenceAcceptanceCondition(a, b);
		AcceptanceCondition acc2 = new EquivalenceAcceptanceCondition(b, a);
		assertTrue(!acc1.equals(acc2));
	}
	
	@Test
	public void testUnequality2() {
		Argument a = new Argument("a");
		Argument b = new Argument("a");
		assertTrue(!a.equals(b));
	}
		
}
