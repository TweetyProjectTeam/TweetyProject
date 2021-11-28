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

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.setaf.syntax.SetAf;
import org.tweetyproject.commons.util.SetTools;

/**
 * This reasoner for SetAf theories performs inference on the conflict-free extensions.
 * @author Matthias Thimm, Sebastian Franke
 *
 */
public class SimpleConflictFreeSetAfReasoner extends AbstractExtensionSetAfReasoner {

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.setaf.reasoner.AbstractExtensionReasoner#getModels(org.tweetyproject.arg.setaf.syntax.SetAfTheory)
	 */
	@Override
	public Collection<Extension<SetAf>> getModels(SetAf bbase) {
		Set<Extension<SetAf>> extensions = new HashSet<Extension<SetAf>>();
		// Check all subsets
		for(Set<Argument> ext: new SetTools<Argument>().subsets(((SetAf)bbase)))
			if(((SetAf)bbase).isConflictFree(new Extension<SetAf>(ext)))
				extensions.add(new Extension<SetAf>(ext));
		return extensions;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.setaf.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.setaf.syntax.SetAfTheory)
	 */
	@Override
	public Extension<SetAf> getModel(SetAf bbase) {
		// as the empty set is always conflict-free we return that one.
		return new Extension<SetAf>();
	}
	
	@Override
	public boolean isInstalled() {
		return true;
	}
}
