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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.serialisability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tweetyproject.serialisability.ToyScheme.set;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SerialisationSequence}.
 *
 * @author Lars Bengel
 */
public class SerialisationSequenceTest {

	@Test
	void empty() {
		SerialisationSequence<Set<Integer>> empty = new SerialisationSequence<>(List.of());
		assertTrue(empty.isEmpty());
		assertEquals("()", empty.toString());
	}

	@Test
	void extend() {
		SerialisationSequence<Set<Integer>> empty = new SerialisationSequence<>(List.of());
		SerialisationSequence<Set<Integer>> sequence = empty.extend(set(1)).extend(set(2, 3));
		assertEquals(List.of(set(1), set(2, 3)), sequence);
		assertEquals(set(2, 3), sequence.get(1));
		assertEquals("([1],[2, 3])", sequence.toString());
		assertTrue(empty.isEmpty());
	}

	@Test
	void readOnly() {
		List<Set<Integer>> steps = new ArrayList<>(List.of(set(1)));
		SerialisationSequence<Set<Integer>> sequence = new SerialisationSequence<>(steps);
		steps.add(set(2));
		assertEquals(1, sequence.size());
		assertThrows(UnsupportedOperationException.class, () -> sequence.add(set(3)));
		assertThrows(UnsupportedOperationException.class, () -> sequence.remove(0));
		assertThrows(UnsupportedOperationException.class, () -> sequence.set(0, set(3)));
	}

	@Test
	void equality() {
		SerialisationSequence<Set<Integer>> ab = new SerialisationSequence<>(List.of(set(1), set(2)));
		SerialisationSequence<Set<Integer>> ba = new SerialisationSequence<>(List.of(set(2), set(1)));
		SerialisationSequence<Set<Integer>> other = new SerialisationSequence<Set<Integer>>(List.of()).extend(set(1))
				.extend(set(2));
		assertNotEquals(ab, ba);
		assertEquals(ab, other);
		assertEquals(ab.hashCode(), other.hashCode());
	}
}
