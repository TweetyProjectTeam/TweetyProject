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
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Labeling;
import org.tweetyproject.arg.setaf.syntax.SetAf;

/**
 * This reasoner for setAf theories performs inference on the ideal extension.
 * @author Matthias Thimm, Sebastian Franke
 *
 */
public class SimpleIdealSetAfReasoner extends AbstractExtensionSetAfReasoner {



		/** Default */
		public SimpleIdealSetAfReasoner(){
			// Default
		}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.setaf.reasoner.AbstractExtensionReasoner#getModels(org.tweetyproject.arg.setaf.syntax.DungTheory)
	 */
	@Override
	public Collection<Extension<SetAf>> getModels(SetAf bbase) {
		Collection<Extension<SetAf>> exts = new HashSet<Extension<SetAf>>();
		exts.add(this.getModel(bbase));
		return exts;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.setaf.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.setaf.syntax.DungTheory)
	 */
	@Override
	public Extension<SetAf> getModel(SetAf bbase) {
		Collection<Extension<SetAf>> admExt = new SimpleAdmissibleSetAfReasoner().getModels(bbase);
		Collection<Extension<SetAf>> prefExt = new SimplePreferredSetAfReasoner().getModels(bbase);
		Set<Labeling> potResult = new HashSet<Labeling>();
		boolean potIdeal;
		for(Extension<SetAf> ext: admExt){
			Labeling extLab = new Labeling(bbase,ext);
			// ext is ideal if
			// 1. for every preferred labeling L both in and out are subsets of that sets in L
			potIdeal = true;
			for(Extension<SetAf> ext2: prefExt){
				Labeling extLab2 = new  Labeling((SetAf)bbase, ext2);
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
		for( Labeling lab: potResult){
			ideal = true;
			for( Labeling lab2: potResult){
				if(lab != lab2)
					if(lab2.getArgumentsOfStatus(ArgumentStatus.IN).containsAll(lab.getArgumentsOfStatus(ArgumentStatus.IN)))
						if(lab2.getArgumentsOfStatus(ArgumentStatus.OUT).containsAll(lab.getArgumentsOfStatus(ArgumentStatus.OUT))){
							ideal = false;
							break;
						}
			}
			if(ideal)
				return (Extension<SetAf>) lab.getArgumentsOfStatus(ArgumentStatus.IN);
		}
		// this should not happen as there is always an ideal extension;
		throw new RuntimeException("Ideal extension seems to be undefined.");
	}

	@Override
	public boolean isInstalled() {
		return true;
	}
}
