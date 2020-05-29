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
package net.sf.tweety.arg.adf.util.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.util.ExcludeSubinterpretationsIterator;

/**
 * @author Mathias Hofer
 *
 */
public class ExcludeSubinterpretationsIteratorTest {

	@Test
	public void testExcludeSubsets1() {
		Argument a = new Argument("a");
		Interpretation i1 = Interpretation.fromSets(Set.of(a), Set.of(), Set.of(new Argument("b"),new Argument("c")));
		Iterator<Interpretation> iterator = new ExcludeSubinterpretationsIterator(List.of(i1));
		while(iterator.hasNext()) {
			Interpretation next = iterator.next();
			assertFalse(next.satisfied(a));
		}
	}
	
	@Test
	public void testExcludeSubsets2() {
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Interpretation i1 = Interpretation.fromSets(Set.of(a), Set.of(b), Set.of(c));
		Iterator<Interpretation> iterator = new ExcludeSubinterpretationsIterator(List.of(i1));
		int count = 0;
		while(iterator.hasNext()) {
			Interpretation next = iterator.next();
			assertFalse(next.satisfied(a) && next.unsatisfied(b));
			count++;
		}
		assertTrue(count == 24);
	}
	
	@Test
	public void testExcludeSubsets3() {
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Interpretation i1 = Interpretation.fromSets(Set.of(a), Set.of(b), Set.of(c));
		Interpretation i2 = Interpretation.fromSets(Set.of(a), Set.of(), Set.of(b, c));
		Iterator<Interpretation> iterator = new ExcludeSubinterpretationsIterator(List.of(i1, i2));
		int count = 0;
		while(iterator.hasNext()) {
			Interpretation next = iterator.next();
			assertFalse(next.satisfied(a));
			count++;
		}
		assertTrue(count == 18);
	}
	
	@Test
	public void testExcludeSubsets4() {
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Interpretation i1 = Interpretation.fromSets(Set.of(a), Set.of(b), Set.of(c));
		Interpretation i2 = Interpretation.fromSets(Set.of(a), Set.of(), Set.of(b, c));
		Interpretation i3 = Interpretation.fromSets(Set.of(b), Set.of(a, c), Set.of());
		Iterator<Interpretation> iterator = new ExcludeSubinterpretationsIterator(List.of(i1, i2, i3));
		int count = 0;
		while(iterator.hasNext()) {
			Interpretation next = iterator.next();
			assertFalse(next.satisfied(a));
			assertFalse(next.satisfied(b) && next.unsatisfied(a) && next.unsatisfied(c));
			count++;
		}
		assertTrue(count == 17);
	}
	
	static void printArgs(Interpretation interpretation, Argument...args) {
		for (Argument arg : args) {
			printArg(arg, interpretation);
		}
		System.out.println();
	}
	
	static void printArg(Argument arg, Interpretation interpretation) {
		if (interpretation.satisfied(arg)) {
			System.out.print("t(" + arg + ") ");
		} else if (interpretation.unsatisfied(arg)) {
			System.out.print("f(" + arg + ") ");
		} else {
			System.out.print("u(" + arg + ") ");
		}
	}
	
}
