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
import java.util.HashMap;
import java.util.Map;

import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Labeling;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.arg.dung.syntax.DungTheory;


/**
 * This reasoner for Dung theories performs inference on the semi-stable extensions.
 * @author Matthias Thimm
 *
 */
public class SimpleSemiStableReasoner extends AbstractExtensionReasoner {
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModels(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Collection<Extension> getModels(ArgumentationFramework bbase) {
		// check all complete extensions and remove those sets with non-mininal set of undecided arguments
		Collection<Extension> exts = new SimpleCompleteReasoner().getModels(bbase);
		Map<Extension,Extension> extUndec = new HashMap<Extension,Extension>();
		for(Extension ext: exts)
			extUndec.put(ext, new Labeling((DungTheory)bbase,ext).getArgumentsOfStatus(ArgumentStatus.UNDECIDED));
		boolean b;
		for(Extension ext: extUndec.keySet()){
			b = false;
			for(Extension ext2: extUndec.keySet()){
				if(ext != ext2){
					if(extUndec.get(ext).containsAll(extUndec.get(ext2)) && !extUndec.get(ext2).containsAll(extUndec.get(ext))){
						exts.remove(ext);
						b = true;
					}
				}			
				if(b) break;
			}			
		}
		return exts;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Extension getModel(ArgumentationFramework bbase) {
		// just return the first one (which is always defined)
		return this.getModels(bbase).iterator().next();
	}
}
