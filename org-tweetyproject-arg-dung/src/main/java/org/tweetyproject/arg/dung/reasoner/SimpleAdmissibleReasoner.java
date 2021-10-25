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

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.SetTools;

/**
 * This reasoner for Dung theories performs inference on the admissible extensions.
 * Extensions are determined by checking all possible sets for admissibility.
 * @author Matthias Thimm
 *
 */


public class SimpleAdmissibleReasoner extends AbstractExtensionReasoner {

	@Override
	public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
		Set<Extension<DungTheory>> extensions = new HashSet<Extension<DungTheory>>();
		// Check all subsets
		for(Set<Argument> ext: new SetTools<Argument>().subsets(((DungTheory) bbase)))
			if(((DungTheory) bbase).isAdmissable(new Extension<DungTheory>(ext)))
				extensions.add(new Extension<DungTheory>(ext));
		return extensions;
	}

	@Override
	public Extension<DungTheory> getModel(DungTheory bbase) {
		// As the empty set is always admissible, we just return that one
		return new Extension<DungTheory>();
	}




}
