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
package org.tweetyproject.arg.dung.serialisability.equivalence;

import java.util.Collection;

import org.tweetyproject.arg.dung.equivalence.Equivalence;
import org.tweetyproject.arg.dung.serialisability.syntax.SerialisationSequence;

/**
 * This class represents an comparator, which defines if 2 sets of sequences are equivalent. 
 * Those sets are deemed equivalent, if for each sequence in one set there is an equivalent sequence 
 * in the other set.Two single sequences are expected to be equivalent if they consist of equivalent elements 
 * in a similar order (naive approach). 
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class SerialisationEquivalenceBySequenceNaiv implements Equivalence<Collection<SerialisationSequence>>{

	@Override
	public boolean isEquivalent(Collection<SerialisationSequence> seq1, Collection<SerialisationSequence> seq2) {
		return seq1.equals(seq2);
	}

	@Override
	public boolean isEquivalent(Collection<Collection<SerialisationSequence>> sequences) {
		Collection<SerialisationSequence> first = sequences.iterator().next();
		for (Collection<SerialisationSequence> seq : sequences) {
			if(seq == first) continue;
			if(!isEquivalent(seq, first)) 
				return false;
		}
		return true;
	}

	@Override
	public String getDescription() {
		return "serialSequenceNaivEQ";
	}

}
