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
package org.tweetyproject.arg.adf.util.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.junit.Test;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.util.Pair;

/**
 * @author Mathias Hofer
 *
 */
public class PairTest {
	
	@Test
	public void testIntegerPairEquality() {
		assertTrue(Objects.equals(Pair.of(1, 2), Pair.of(1, 2)));
	}
	
	@Test
	public void testAtomPairEquality1() {
		Literal a = Literal.create();
		Literal b = Literal.create();
		assertTrue(Objects.equals(Pair.of(a, b), Pair.of(a, b)));
	}
	
	@Test
	public void testAtomPairEquality2() {
		Literal a = Literal.create("a");
		Literal b = Literal.create("b");
		assertTrue(Objects.equals(Pair.of(a, b), Pair.of(a, b)));
	}
	
	@Test
	public void testAtomPairUnquality1() {
		Literal a = Literal.create(null);
		Literal b = Literal.create(null);
		assertFalse(Objects.equals(Pair.of(b, a), Pair.of(a, b)));
	}
	
	@Test
	public void testAtomPairUnquality2() {
		Literal a = Literal.create("a");
		Literal b = Literal.create("a");
		assertFalse(Objects.equals(Pair.of(b, a), Pair.of(a, b)));
	}

}
