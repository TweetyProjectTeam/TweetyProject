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
package org.tweetyproject.arg.dung.serialisability.syntax;

import java.util.LinkedList;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This class represents a sequence of sets of initial sets.
 * More precisely, each set is an initial set of the reduct wrt. the union of the previous initial sets.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class OldSerialisationSequence extends LinkedList<Extension<DungTheory>> {

	private static final long serialVersionUID = -109538431325318647L;

	/**
	 * Creates an empty serialisation sequence
	 */
	public OldSerialisationSequence() {
		super();
	}

	/**
	 * Creates a serialisation sequence, containing the specified element as the first element in the list.
	 * @param root First element of the sequence
	 */
	public OldSerialisationSequence(Extension<DungTheory> root) {
		super();
		this.add(root);
	}

	/**
	 * Creates a serialisation sequence, by copying the given sequence
	 * @param sequence serialisation sequence to be copied
	 */
	public OldSerialisationSequence(OldSerialisationSequence sequence) {
		super(sequence);
	}
	
	@Override
	public boolean add(Extension<DungTheory> element) {
		if(this.contains(element)) {
			return false;
		}
		return super.add(element);
	}
	
	@Override
	public void add(int index, Extension<DungTheory> element) {
		if(this.contains(element)) {
			return;
		}
		super.add(index, element);
    }

	/**
	 * Computes the corresponding extension as the union of all the sets in the sequence
	 * @return An extension containing all arguments of this sequence
	 */
	public Extension<DungTheory> getExtension() {
		Extension<DungTheory> output = new Extension<>();
		for (Extension<DungTheory> extension : this) {
			output.addAll(extension);
		}
		return output;
	}
}
