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

import net.sf.tweety.arg.dung.semantics.*;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.commons.ModelProvider;

/**
 * This class implements a stratified labeling reasoner.
 * @author Matthias Thimm
 */
public class StratifiedLabelingReasoner extends AbstractDungReasoner implements ModelProvider<Argument,DungTheory,StratifiedLabeling> {

	/** The semantics used for this reasoner. */
	private Semantics semantics;
	
	/**
	 * Creates a new reasoner for the given Dung theory, semantics, and inference type.
	 * @param beliefBase a Dung theory
	 * @param semantics a semantics
	 * @param inferenceType and inference type
	 */
	public StratifiedLabelingReasoner(Semantics semantics) {		
		this.semantics = semantics;
	}

	/**
	 * Creates a new reasoner using sceptical inference and grounded semantics.
	 * @param beliefBase The knowledge base for this reasoner.
	 */
	public StratifiedLabelingReasoner(){
		this(Semantics.GROUNDED_SEMANTICS);		
	}	
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.ModelProvider#getModels(net.sf.tweety.commons.BeliefBase)
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
	 * @see net.sf.tweety.commons.ModelProvider#getModel(net.sf.tweety.commons.BeliefBase)
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
	 * @see net.sf.tweety.arg.dung.reasoner.AbstractDungReasoner#query(net.sf.tweety.arg.dung.syntax.DungTheory, net.sf.tweety.arg.dung.syntax.Argument)
	 */
	@Override
	public Boolean query(DungTheory beliefbase, Argument formula) {		
		return this.query(beliefbase, formula, Semantics.SCEPTICAL_INFERENCE);
	}	
	
	/** Queries the given AAF for the given argument using the given 
	 * inference type.
	 * @param beliefbase an AAF
	 * @param formula a single argument
	 * @param inferenceType either Semantics.SCEPTICAL_INFERENCE or Semantics.CREDULOUS_INFERENCE
	 * @return "true" if the argument is accepted
	 */
	public Boolean query(DungTheory beliefbase, Argument formula, int inferenceType) {
		Collection<StratifiedLabeling> labelings = this.getModels(beliefbase);
		if(inferenceType == Semantics.SCEPTICAL_INFERENCE){
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
