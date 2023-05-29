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
package org.tweetyproject.arg.dung.serialisibility.syntax;

import java.util.LinkedList;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This class represents a sequence of sets of arguments, which is typically generated during the process
 * of finding extensions in a framework by serialising sets of arguments.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class SerialisationSequence extends LinkedList<Extension<DungTheory>> {

	private static final long serialVersionUID = -109538431325318647L;

	public SerialisationSequence() {
		super();
	}

	/**
	 * Creates a sequence, containing the specified element as the first element in the list
	 * @param root First element of the sequence
	 */
	public SerialisationSequence(Extension<DungTheory> root) {
		super();
		this.add(root);
	}

	/**
	 * Creates a sequence, containing all arguments of the specified sequence, in the same order
	 * @param parentSequence Sequence specifying part of the arguments on this sequence
	 */
	public SerialisationSequence(SerialisationSequence parentSequence) {
		super(parentSequence);
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
		return;
	}

	/**
	 * @return An extension containing all arguments of the sets of this sequence
	 */
	public Extension<DungTheory> getCompleteExtension() {
		Extension<DungTheory> output = new Extension<>();
		for (Extension<DungTheory> extension : this) {
			output.addAll(extension);
		}
		return output;
	}
	
	/*
	 * @Override public boolean equals(Object o) { if(!(o instanceof
	 * SerialisationSequence)) return false;
	 * 
	 * var otherSequence = (SerialisationSequence) o;
	 * 
	 * if(!super.equals(otherSequence)) return false;
	 * 
	 * // consider order of elements var iteratorThis = this.iterator(); var
	 * iteratorOther = otherSequence.iterator(); while(iteratorThis.hasNext()) {
	 * if(!iteratorThis.next().equals(iteratorOther.next())) return false; } return
	 * true; }
	 */
}
