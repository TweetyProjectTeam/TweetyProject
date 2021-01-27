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

import java.util.List;
import java.util.Set;

import org.junit.Test;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.util.InterpretationTrieSet;

/**
 * @author Mathias Hofer
 *
 */
public class InterpretationPrefixSetTest {
	
	@Test
	public void testPrefix1() {
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Interpretation i1 = Interpretation.fromSets(Set.of(a), Set.of(), Set.of(b, c));
//		InterpretationPrefixSet set = new InterpretationPrefixSet(List.of(i1));
		InterpretationTrieSet set = new InterpretationTrieSet(List.of(i1));
		assertTrue(set.contains(i1));
		assertTrue(set.contains(Interpretation.fromSets(Set.of(a), Set.of(c), Set.of(b))));
		assertTrue(set.contains(Interpretation.fromSets(Set.of(a), Set.of(c,b), Set.of())));
		assertFalse(set.contains(Interpretation.fromSets(Set.of(), Set.of(c,b), Set.of(a))));
		assertFalse(set.contains(Interpretation.fromSets(Set.of(), Set.of(a,c,b), Set.of())));
	}

}
