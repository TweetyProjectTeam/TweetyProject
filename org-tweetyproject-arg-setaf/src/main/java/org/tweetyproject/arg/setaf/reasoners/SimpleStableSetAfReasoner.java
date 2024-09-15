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

import java.util.*;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.setaf.syntax.*;

/**
 * This reasoner for setaf theories performs inference on the stable extensions.
 * Computes the set of all stable extensions, i.e., all conflict-free sets that attack each other argument.
 * For that, it uses the SimpleSccCompleteReasoner to first compute all complete extensions, and
 * then filters out the non-stable ones.
 * @author Matthias Thimm, Sebastian Franke
 *
 */
public class SimpleStableSetAfReasoner extends AbstractExtensionSetAfReasoner {




		/** Default */
		public SimpleStableSetAfReasoner(){
			// Default
		}



	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.setaf.reasoner.AbstractExtensionReasoner#getModels(org.tweetyproject.arg.setaf.syntax.DungTheory)
	 */
	@Override
	public Collection<Extension<SetAf>> getModels(SetAf bbase) {
		Collection<Extension<SetAf>> completeExtensions = new SimpleCompleteSetAfReasoner().getModels(bbase);
		Set<Extension<SetAf>> result = new HashSet<Extension<SetAf>>();
		for(Extension<SetAf> e: completeExtensions)
			if(((SetAf)bbase).isAttackingAllOtherArguments(e))
				result.add(e);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.setaf.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.setaf.syntax.DungTheory)
	 */
	@Override
	public Extension<SetAf> getModel(SetAf bbase) {
		// returns the first found stable extension
		Collection<Extension<SetAf>> completeExtensions = new SimpleCompleteSetAfReasoner().getModels(bbase);
		for(Extension<SetAf> e: completeExtensions)
			if(((SetAf)bbase).isAttackingAllOtherArguments(e))
				return e;
		return null;
	}

	@Override
	public boolean isInstalled() {
		return true;
	}
}
