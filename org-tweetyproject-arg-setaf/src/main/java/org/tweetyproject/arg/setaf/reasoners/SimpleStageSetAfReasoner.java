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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.setaf.reasoners;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.setaf.semantics.SetAfExtension;
import org.tweetyproject.arg.setaf.semantics.SetAfLabeling;
import org.tweetyproject.arg.setaf.syntax.SetAf;

/**
 * This reasoner for setaf theories performs inference on the stage extensions.
 * @author Matthias Thimm, Sebastian Franke
 *
 */
public class SimpleStageSetAfReasoner extends AbstractExtensionSetAfReasoner {

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.setaf.reasoner.AbstractExtensionReasoner#getModels(org.tweetyproject.arg.setaf.syntax.DungTheory)
	 */
	@Override
	public Collection<SetAfExtension> getModels(SetAf bbase) {
		// A stage extension is a conflict-free set with minimal undecided arguments
		Collection<SetAfExtension> cfExt = new SimpleConflictFreeSetAfReasoner().getModels(bbase);
		Set<SetAfLabeling> cfLab = new HashSet<SetAfLabeling>();
		for(SetAfExtension e: cfExt)
			cfLab.add(new SetAfLabeling(bbase,e));
		Set<SetAfExtension> result = new HashSet<SetAfExtension>();
		boolean stage;
		for(SetAfLabeling lab: cfLab){
			stage = true;
			for(SetAfLabeling lab2: cfLab){
				if(lab != lab2){
					if(lab.getArgumentsOfStatus(ArgumentStatus.UNDECIDED).containsAll(lab2.getArgumentsOfStatus(ArgumentStatus.UNDECIDED)) &&
							!lab.getArgumentsOfStatus(ArgumentStatus.UNDECIDED).equals(lab2.getArgumentsOfStatus(ArgumentStatus.UNDECIDED)) ){
						stage = false;
						break;
					}
				}
			}
			if(stage){
				result.add(lab.getArgumentsOfStatus(ArgumentStatus.IN));
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.setaf.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.setaf.syntax.DungTheory)
	 */
	@Override
	public SetAfExtension getModel(SetAf bbase) {
		// just return the first one
		return this.getModels(bbase).iterator().next();
	}
}
