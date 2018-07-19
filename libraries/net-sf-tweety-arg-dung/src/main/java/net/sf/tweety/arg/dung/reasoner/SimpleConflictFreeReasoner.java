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
package net.sf.tweety.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.commons.util.SetTools;

/**
 * This reasoner for Dung theories performs inference on the conflict-free extensions.
 * @author Matthias Thimm
 *
 */
public class SimpleConflictFreeReasoner extends AbstractExtensionReasoner {

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner#getModels(net.sf.tweety.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Collection<Extension> getModels(DungTheory bbase) {
		Set<Extension> extensions = new HashSet<Extension>();
		// Check all subsets
		for(Set<Argument> ext: new SetTools<Argument>().subsets(bbase))
			if(new Extension(ext).isConflictFree(bbase))
				extensions.add(new Extension(ext));
		return extensions;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner#getModel(net.sf.tweety.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Extension getModel(DungTheory bbase) {
		// as the empty set is always conflict-free we return that one.
		return new Extension();
	}
}
