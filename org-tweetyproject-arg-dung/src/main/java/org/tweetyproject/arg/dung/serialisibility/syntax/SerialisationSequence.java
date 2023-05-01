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

import java.util.LinkedHashSet;

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
public class SerialisationSequence extends LinkedHashSet<Extension<DungTheory>> {

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
}
