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
package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Labeling;
import org.tweetyproject.arg.dung.syntax.DungTheory;


/**
 * This reasoner calculates claim based semi stable extensions
 * @author Sebastian Franke
 *
 */
public class SimpleSemiStableReasoner extends AbstractExtensionReasoner {

	/**
	 *
	 * @param bbase the claim based thory
	 * @return all extensions of the semantics
	 */
	@Override
	public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
		// check all complete extensions and remove those sets with non-mininal set of undecided arguments
		Collection<Extension<DungTheory>> exts = new SimpleCompleteReasoner().getModels(bbase);
		Map<Extension<DungTheory>,Extension<DungTheory>> extUndec = new HashMap<Extension<DungTheory>,Extension<DungTheory>>();
		for(Extension<DungTheory> ext: exts)
			extUndec.put(ext, (Extension<DungTheory>) new Labeling(bbase,ext).getArgumentsOfStatus(ArgumentStatus.UNDECIDED));
		boolean b;
		for(Extension<DungTheory> ext: extUndec.keySet()){
			b = false;
			for(Extension<DungTheory> ext2: extUndec.keySet()){
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

	/**
	 *
	 * @param bbase the claim based thory
	 * @return an extensions of the semantics
	 */
	@Override
	public Extension<DungTheory> getModel(DungTheory bbase) {
		// just return the first one (which is always defined)
		return this.getModels(bbase).iterator().next();
	}
}
