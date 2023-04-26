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

import org.tweetyproject.arg.dung.equivalence.IEquivalence;
import org.tweetyproject.arg.dung.serialisibility.sequence.SerialisationSequence;

/**
 * This class represents an comparator, which defines if 2 sequences are equivalent, 
 * by comparing if they consist of equivalent elements (naiv approach). 
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class SerialisationEquivalenceBySequenceNaiv implements IEquivalence<SerialisationSequence>{

	@Override
	public boolean isEquivalent(SerialisationSequence seq1, SerialisationSequence seq2) {
		return seq1.equals(seq2);
	}

	@Override
	public boolean isEquivalent(Collection<SerialisationSequence> sequences) {
		SerialisationSequence first = sequences.iterator().next();
		for (SerialisationSequence seq : sequences) {
			if(seq == first) continue;
			if(!isEquivalent(seq, first)) return false;
		}
		return true;
	}

	@Override
	public Collection<SerialisationSequence> getEquivalentTheories(SerialisationSequence sequence) {
		// not supported
		return null;
	}

	@Override
	public String getDescription() {
		return "serialSequenceNaivEQ";
	}

}
