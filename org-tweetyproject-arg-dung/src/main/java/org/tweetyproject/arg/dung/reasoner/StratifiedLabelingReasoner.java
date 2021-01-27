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

import org.tweetyproject.arg.dung.semantics.*;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.ModelProvider;

/**
 * This class implements a stratified labeling reasoner.
 * @author Matthias Thimm
 */
public class StratifiedLabelingReasoner extends AbstractDungReasoner implements ModelProvider<Argument,DungTheory,StratifiedLabeling> {

	/** The semantics used for this reasoner. */
	private Semantics semantics;
	
	/**
	 * Creates a new reasoner for the given semantics.
	 * @param semantics a semantics
	 */
	public StratifiedLabelingReasoner(Semantics semantics) {		
		this.semantics = semantics;
	}

	/**
	 * Creates a new reasoner using sceptical inference and grounded semantics.
	 */
	public StratifiedLabelingReasoner(){
		this(Semantics.GROUNDED_SEMANTICS);		
	}	
		
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.ModelProvider#getModels(org.tweetyproject.commons.BeliefBase)
	 */
	@Override
	public Collection<StratifiedLabeling> getModels(DungTheory bbase) {
		Set<StratifiedLabeling> labelings = new HashSet<StratifiedLabeling>();
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(this.semantics);
		Collection<Extension> extensions = reasoner.getModels(bbase);
		for(Extension extension: extensions){
			StratifiedLabeling labeling = new StratifiedLabeling();
			if(extension.isEmpty()){
				for(Argument arg: bbase)
					labeling.put(arg, Integer.MAX_VALUE);
				labelings.add(labeling);
			}else{
				for(Argument arg: extension)
					labeling.put(arg, 0);
				Extension remainingArguments = new Extension(bbase);
				remainingArguments.removeAll(extension);
				DungTheory remainingTheory = new DungTheory(bbase.getRestriction(remainingArguments));
				StratifiedLabelingReasoner sReasoner = new StratifiedLabelingReasoner(this.semantics);
				for(StratifiedLabeling labeling2: sReasoner.getModels(remainingTheory)){
					for(Argument arg: labeling2.keySet())
						labeling2.put(arg, labeling2.get(arg) + 1);
					labeling2.putAll(labeling);
					labelings.add(labeling2);
				}
			}
		}		
		return labelings;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.ModelProvider#getModel(org.tweetyproject.commons.BeliefBase)
	 */
	@Override
	public StratifiedLabeling getModel(DungTheory bbase) {
		// just take the first one.
		Collection<StratifiedLabeling> labes = this.getModels(bbase);
		if(labes.isEmpty())
			return null;
		return labes.iterator().next();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractDungReasoner#query(org.tweetyproject.arg.dung.syntax.DungTheory, org.tweetyproject.arg.dung.syntax.Argument)
	 */
	@Override
	public Boolean query(DungTheory beliefbase, Argument formula) {		
		return this.query(beliefbase, formula, InferenceMode.SKEPTICAL);
	}	
	
	/** Queries the given AAF for the given argument using the given 
	 * inference type.
	 * @param beliefbase an AAF
	 * @param formula a single argument
	 * @param inferenceMode either InferenceMode.SKEPTICAL or InferenceMode.CREDULOUS
	 * @return "true" if the argument is accepted
	 */
	public Boolean query(DungTheory beliefbase, Argument formula, InferenceMode inferenceMode) {
		Collection<StratifiedLabeling> labelings = this.getModels(beliefbase);
		if(inferenceMode.equals(InferenceMode.SKEPTICAL)){
			for(StratifiedLabeling e: labelings)
				if(!e.satisfies(formula))
					return false;
			return true;
		}
		// so its credulous semantics
		for(StratifiedLabeling e: labelings){
			if(e.satisfies(formula))
				return true;			
		}			
		return false;
	}
}
