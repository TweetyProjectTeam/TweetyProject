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
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Labeling;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This reasoner for Dung theories performs inference on the stage extensions.
 * @author Matthias Thimm
 *
 */
public class SimpleStageReasoner extends AbstractExtensionReasoner {

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModels(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
		// A stage extension is a conflict-free set with minimal undecided arguments
		Collection<Extension<DungTheory>> cfExt = new SimpleConflictFreeReasoner().getModels(bbase);
		Set<Labeling> cfLab = new HashSet<Labeling>();
		for(Extension<DungTheory> e: cfExt)
			cfLab.add(new Labeling((DungTheory) bbase,e));
		Set<Extension<DungTheory>> result = new HashSet<Extension<DungTheory>>();
		boolean stage;
		for(Labeling lab: cfLab){
			stage = true;
			for(Labeling lab2: cfLab){
				if(lab != lab2){
					if(lab.getArgumentsOfStatus(ArgumentStatus.UNDECIDED).containsAll(lab2.getArgumentsOfStatus(ArgumentStatus.UNDECIDED)) &&
							!lab.getArgumentsOfStatus(ArgumentStatus.UNDECIDED).equals(lab2.getArgumentsOfStatus(ArgumentStatus.UNDECIDED)) ){
						stage = false;
						break;
					}
				}
			}
			if(stage){
				result.add((Extension<DungTheory>) lab.getArgumentsOfStatus(ArgumentStatus.IN));
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Extension<DungTheory> getModel(DungTheory bbase) {
		// just return the first one
		return this.getModels(bbase).iterator().next();
	}
}
