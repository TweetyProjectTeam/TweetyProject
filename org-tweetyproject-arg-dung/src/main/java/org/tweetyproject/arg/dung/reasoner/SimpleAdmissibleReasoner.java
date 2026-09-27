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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This reasoner for Dung theories performs inference on the admissible extensions.
 * Extensions are determined by checking all conflict-free sets for defence.
 *
 * @author Matthias Thimm
 * @author Lars Bengel
 */


public class SimpleAdmissibleReasoner extends AbstractExtensionReasoner {

	/** Creates a simple admissible reasoner. */
	public SimpleAdmissibleReasoner() {
	}

	@Override
	public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
		Collection<Extension<DungTheory>> extensions = new HashSet<>();
		// conflict-free sets only need to be checked for defence
		for (Extension<DungTheory> ext: new SimpleConflictFreeReasoner().getModels(bbase)) {
			boolean defended = true;
			for (Argument argument: ext) {
				if (!bbase.isAcceptable(argument, ext)) {
					defended = false;
					break;
				}
			}
			if (defended) {
				extensions.add(ext);
			}
		}
		return extensions;
	}

	@Override
	public Extension<DungTheory> getModel(DungTheory bbase) {
		// As the empty set is always admissible, we just return that one
		return new Extension<DungTheory>();
	}




}
