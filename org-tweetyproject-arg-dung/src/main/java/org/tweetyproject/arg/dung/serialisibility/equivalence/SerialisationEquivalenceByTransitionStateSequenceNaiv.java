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
package org.tweetyproject.arg.dung.serialisibility.equivalence;

import java.util.Collection;

import org.tweetyproject.arg.dung.equivalence.Equivalence;
import org.tweetyproject.arg.dung.serialisibility.syntax.TransitionState;
import org.tweetyproject.arg.dung.serialisibility.syntax.TransitionStateSequence;

/**
 * This class represents an comparator, which defines if 2 sets of {@link TransitionStateSequence TransitionStateSequences} are equivalent.
 * Those sets are deemed equivalent, if for each {@link TransitionStateSequence sequence} in one set there is an equivalent {@link TransitionStateSequence sequence} 
 * in the other set.Two single {@link TransitionStateSequence sequences} are expected to be equivalent if they consist of equivalent {@link TransitionState}
 * in a similar order (naive approach).  
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class SerialisationEquivalenceByTransitionStateSequenceNaiv implements Equivalence<Collection<TransitionStateSequence>>{

	@Override
	public boolean isEquivalent(Collection<TransitionStateSequence> obj1,
			Collection<TransitionStateSequence> obj2) {
		return obj1.equals(obj2);
	}

	@Override
	public boolean isEquivalent(Collection<Collection<TransitionStateSequence>> objects) {
		var first = objects.iterator().next();
		for (var collectionOfReductionSeqs : objects) {
			if(collectionOfReductionSeqs == first) continue;
			if(!isEquivalent(collectionOfReductionSeqs, first)) 
				return false;
		}
		return true;
	}

	@Override
	public String getDescription() {
		return "transitionStateSequenceNaivEQ";
	}

}
