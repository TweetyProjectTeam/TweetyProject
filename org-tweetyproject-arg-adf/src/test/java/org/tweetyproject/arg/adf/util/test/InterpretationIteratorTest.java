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

import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.interpretation.InterpretationIterator;
import org.tweetyproject.arg.adf.syntax.Argument;

/**
 * @author Mathias Hofer
 *
 */
public class InterpretationIteratorTest {

	@Test
	public void testIterator1() {
		Iterator<Interpretation> iterator = new InterpretationIterator(List.of(new Argument("a"), new Argument("b"), new Argument("c")));
		int count = 0;
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		assertTrue(count == 27);
	}
	
}
