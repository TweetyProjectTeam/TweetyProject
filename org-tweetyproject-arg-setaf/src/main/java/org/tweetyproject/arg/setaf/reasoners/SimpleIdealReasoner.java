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
package org.tweetyproject.arg.setaf.reasoners;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.setaf.semantics.SetafExtension;
import org.tweetyproject.arg.setaf.semantics.Labeling;
import org.tweetyproject.arg.setaf.syntax.SetafTheory;

/**
 * This reasoner for Dung theories performs inference on the ideal extension.
 * @author Matthias Thimm
 *
 */
public class SimpleIdealReasoner extends AbstractExtensionReasoner {


	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModels(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Collection<SetafExtension> getModels(SetafTheory bbase) {
		Collection<SetafExtension> exts = new HashSet<SetafExtension>();
		exts.add(this.getModel(bbase));
		return exts;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	@Override
	public SetafExtension getModel(SetafTheory bbase) {
		Collection<SetafExtension> admExt = new SimpleAdmissibleReasoner().getModels(bbase);
		Collection<SetafExtension> prefExt = new SimplePreferredReasoner().getModels(bbase);
		Set<Labeling> potResult = new HashSet<Labeling>();
		boolean potIdeal; 
		for(SetafExtension ext: admExt){
			Labeling extLab = new Labeling(bbase,ext);
			// ext is ideal if
			// 1. for every preferred labeling L both in and out are subsets of that sets in L
			potIdeal = true;
			for(SetafExtension ext2: prefExt){
				Labeling extLab2 = new Labeling(bbase, ext2);
				if(!extLab2.getArgumentsOfStatus(ArgumentStatus.IN).containsAll(extLab.getArgumentsOfStatus(ArgumentStatus.IN))){
					potIdeal = false;
					break;
				}
				if(!extLab2.getArgumentsOfStatus(ArgumentStatus.OUT).containsAll(extLab.getArgumentsOfStatus(ArgumentStatus.OUT))){
					potIdeal = false;
					break;
				}
			}
			if(potIdeal)				
				potResult.add(extLab);			
		}		
		// get the one which maximizes in and out
		// Note that there is only one ideal extension
		boolean ideal;
		for(Labeling lab: potResult){
			ideal = true;
			for(Labeling lab2: potResult){
				if(lab != lab2)
					if(lab2.getArgumentsOfStatus(ArgumentStatus.IN).containsAll(lab.getArgumentsOfStatus(ArgumentStatus.IN)))
						if(lab2.getArgumentsOfStatus(ArgumentStatus.OUT).containsAll(lab.getArgumentsOfStatus(ArgumentStatus.OUT))){
							ideal = false;
							break;
						}
			}
			if(ideal)
				return lab.getArgumentsOfStatus(ArgumentStatus.IN);			
		}		
		// this should not happen as there is always an ideal extension;
		throw new RuntimeException("Ideal extension seems to be undefined.");
	}
}
