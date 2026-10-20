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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * A serialisation sequence (S1, ..., Sn), i.e. the initial models selected
 * along a path of a serialisation system. The sequence is a read-only list of
 * its steps; the induced model S1 ⊔ ... ⊔ Sn is not stored.
 *
 * @see "Lars Bengel, Matthias Thimm. 'Serialisability in Formal Argumentation' (2026)"
 * @see "Lydia Blümel, Matthias Thimm. 'A Ranking Semantics for Abstract Argumentation based on Serialisability' COMMA (2022)"
 *
 * @param <S> the type of models
 *
 * @author Lars Bengel
 */
public final class SerialisationSequence<S> extends AbstractList<S> {
	/** the selected initial models */
	private final List<S> steps;

	/**
	 * Creates a sequence
	 * @param steps the selected initial models
	 */
	public SerialisationSequence(List<S> steps) {
		this.steps = new ArrayList<>(steps);
	}

	/**
	 * @param initialModel some initial model
	 * @return a new sequence with the given initial model appended
	 */
	public SerialisationSequence<S> extend(S initialModel) {
		List<S> steps = new ArrayList<>(this.steps);
		steps.add(initialModel);
		return new SerialisationSequence<>(steps);
	}

	@Override
	public S get(int index) {
		return this.steps.get(index);
	}

	@Override
	public int size() {
		return this.steps.size();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("(");
		for (int i = 0; i < this.steps.size(); i++) {
			if (i > 0)
				s.append(",");
			s.append(this.steps.get(i));
		}
		s.append(")");
		return s.toString();
	}
}
