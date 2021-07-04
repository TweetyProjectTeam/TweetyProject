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
import java.util.HashMap;
import java.util.Map;

import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.setaf.semantics.SetAfExtension;
import org.tweetyproject.arg.setaf.semantics.SetAfLabeling;
import org.tweetyproject.arg.setaf.syntax.SetAf;


/**
 * This reasoner for setaf theories performs inference on the semi-stable extensions.
 * @author Matthias Thimm, Sebastian Franke
 *
 */
public class SimpleSemiStableSetAfReasoner extends AbstractExtensionSetAfReasoner {
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.setaf.reasoner.AbstractExtensionReasoner#getModels(org.tweetyproject.arg.setaf.syntax.DungTheory)
	 */
	@Override
	public Collection<SetAfExtension> getModels(SetAf bbase) {
		// check all complete extensions and remove those sets with non-mininal set of undecided arguments
		Collection<SetAfExtension> exts = new SimpleCompleteSetAfReasoner().getModels(bbase);
		Map<SetAfExtension,SetAfExtension> extUndec = new HashMap<SetAfExtension,SetAfExtension>();
		for(SetAfExtension ext: exts)
			extUndec.put(ext, new SetAfLabeling(bbase,ext).getArgumentsOfStatus(ArgumentStatus.UNDECIDED));
		boolean b;
		for(SetAfExtension ext: extUndec.keySet()){
			b = false;
			for(SetAfExtension ext2: extUndec.keySet()){
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
	 * @see org.tweetyproject.arg.setaf.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.setaf.syntax.DungTheory)
	 */
	@Override
	public SetAfExtension getModel(SetAf bbase) {
		// just return the first one (which is always defined)
		return this.getModels(bbase).iterator().next();
	}
}
