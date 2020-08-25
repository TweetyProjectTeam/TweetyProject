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

import java.util.Objects;

import org.junit.Test;

import net.sf.tweety.arg.adf.syntax.pl.Atom;
import net.sf.tweety.arg.adf.util.Pair;

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
		Atom a = Atom.of(null);
		Atom b = Atom.of(null);
		assertTrue(Objects.equals(Pair.of(a, b), Pair.of(a, b)));
	}
	
	@Test
	public void testAtomPairEquality2() {
		Atom a = Atom.of("a");
		Atom b = Atom.of("b");
		assertTrue(Objects.equals(Pair.of(a, b), Pair.of(a, b)));
	}
	
	@Test
	public void testAtomPairUnquality1() {
		Atom a = Atom.of(null);
		Atom b = Atom.of(null);
		assertFalse(Objects.equals(Pair.of(b, a), Pair.of(a, b)));
	}
	
	@Test
	public void testAtomPairUnquality2() {
		Atom a = Atom.of("a");
		Atom b = Atom.of("a");
		assertFalse(Objects.equals(Pair.of(b, a), Pair.of(a, b)));
	}

}
