/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.dung;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.arg.dung.semantics.ArgumentStatus;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.Labeling;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * This reasoner for Dung theories performs inference on the semi-stable extensions.
 * @author Matthias Thimm
 *
 */
public class SemiStableReasoner extends AbstractExtensionReasoner {

	/**
	 * Creates a new semi-stable reasoner for the given knowledge base.
	 * @param beliefBase a knowledge base.
	 * @param inferenceType The inference type for this reasoner.
	 */
	public SemiStableReasoner(BeliefBase beliefBase, int inferenceType){
		super(beliefBase, inferenceType);		
	}
	
	/**
	 * Creates a new semi-stable reasoner for the given knowledge base using sceptical inference.
	 * @param beliefBase The knowledge base for this reasoner.
	 */
	public SemiStableReasoner(BeliefBase beliefBase){
		super(beliefBase);		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.dung.AbstractExtensionReasoner#computeExtensions()
	 */
	public Set<Extension> computeExtensions(){
		// check all complete extensions and remove those sets with non-mininal set of undecided arguments
		Set<Extension> exts = new CompleteReasoner(this.getKnowledgBase(),this.getInferenceType()).getExtensions();
		Map<Extension,Extension> extUndec = new HashMap<Extension,Extension>();
		for(Extension ext: exts)
			extUndec.put(ext, new Labeling((DungTheory) this.getKnowledgBase(),ext).getArgumentsOfStatus(ArgumentStatus.UNDECIDED));
		boolean b;
		for(Extension ext: extUndec.keySet()){
			b = false;
			for(Extension ext2: extUndec.keySet()){
				if(ext != ext2){
					if(extUndec.get(ext).containsAll(extUndec.get(ext2))){
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
	 * @see net.sf.tweety.arg.dung.AbstractExtensionReasoner#getPropositionalCharacterisationBySemantics(java.util.Map, java.util.Map, java.util.Map)
	 */
	@Override
	protected PlBeliefSet getPropositionalCharacterisationBySemantics(Map<Argument, Proposition> in, Map<Argument, Proposition> out, Map<Argument, Proposition> undec) {
		throw new UnsupportedOperationException("Implement me!");
	}
}
