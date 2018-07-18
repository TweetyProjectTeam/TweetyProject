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
package net.sf.tweety.arg.dung;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.arg.dung.semantics.ArgumentStatus;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.Labeling;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * This reasoner for Dung theories performs inference on the ideal extension.
 * @author Matthias Thimm
 *
 */
public class IdealReasoner extends AbstractExtensionReasoner {

	/**
	 * Creates a new ideal reasoner.
	 * @param inferenceType The inference type for this reasoner.
	 */
	public IdealReasoner(int inferenceType){
		super(inferenceType);		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.AbstractExtensionReasoner#getExtensions(net.sf.tweety.arg.dung.DungTheory)
	 */
	public Set<Extension> getExtensions(DungTheory theory){
		Set<Extension> admExt = new AdmissibleReasoner(this.getInferenceType()).getExtensions(theory);
		Set<Extension> prefExt = new PreferredReasoner(this.getInferenceType()).getExtensions(theory);
		Set<Labeling> potResult = new HashSet<Labeling>();
		boolean potIdeal; 
		for(Extension ext: admExt){
			Labeling extLab = new Labeling(theory, ext);
			// ext is ideal if
			// 1. for every preferred labeling L both in and out are subsets of that sets in L
			potIdeal = true;
			for(Extension ext2: prefExt){
				Labeling extLab2 = new Labeling(theory, ext2);
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
		Set<Extension> result = new HashSet<Extension>();
		boolean ideal;
		for(Labeling lab: potResult){
			ideal = true;
			for(Labeling lab2: potResult){
				if(lab2.getArgumentsOfStatus(ArgumentStatus.IN).containsAll(lab.getArgumentsOfStatus(ArgumentStatus.IN)))
					if(lab2.getArgumentsOfStatus(ArgumentStatus.OUT).containsAll(lab.getArgumentsOfStatus(ArgumentStatus.OUT))){
						ideal = false;
						break;
					}
			}
			if(ideal){
				result.add(lab.getArgumentsOfStatus(ArgumentStatus.IN));
				return result;
			}
			
		}		
		return new HashSet<Extension>();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.AbstractExtensionReasoner#getPropositionalCharacterisationBySemantics(java.util.Map, java.util.Map, java.util.Map)
	 */
	@Override
	protected PlBeliefSet getPropositionalCharacterisationBySemantics(DungTheory theory, Map<Argument, Proposition> in, Map<Argument, Proposition> out, Map<Argument, Proposition> undec) {
		throw new UnsupportedOperationException("Implement me!");
	}
}
