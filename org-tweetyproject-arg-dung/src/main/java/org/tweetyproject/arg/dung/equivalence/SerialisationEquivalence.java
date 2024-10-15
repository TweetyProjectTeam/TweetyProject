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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.equivalence;

import org.tweetyproject.arg.dung.reasoner.SerialisedExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;

/**
 * This class defines 'Serialisation' equivalence for {@link DungTheory Argumentation Frameworks} wrt. some {@link Semantics},
 * i.e., two AFs are serialisation equivalent iff they possess the same set of
 * {@link org.tweetyproject.arg.dung.serialisability.semantics.SerialisationSequence Serialisation Sequences} wrt. some {@link Semantics}.
 *
 * @author Julian Sander
 * @author Lars Bengel
 */
public class SerialisationEquivalence implements Equivalence<DungTheory> {

	/** the semantics by which the serialisation sequences of the AFs are compared */
	private final Semantics semantics;

	/**
	 * Initializes a new instance of this equivalence notion for the given semantics
	 * @param semantics some semantics
	 */
	public SerialisationEquivalence(Semantics semantics) {
		this.semantics = semantics;
	}

	@Override
	public boolean isEquivalent(DungTheory obj1, DungTheory obj2) {
		SerialisedExtensionReasoner reasoner = new SerialisedExtensionReasoner(this.semantics);
		return reasoner.getSequences(obj1).equals(reasoner.getSequences(obj2));
	}

	@Override
	public boolean isEquivalent(Collection<DungTheory> objects) {
		DungTheory first = objects.iterator().next();
		for (DungTheory theory : objects) {
			if(theory == first) {
				continue;
			}
			if(!this.isEquivalent(theory, first)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getName() {
		return "Serialisation Equivalence";
	}
}
