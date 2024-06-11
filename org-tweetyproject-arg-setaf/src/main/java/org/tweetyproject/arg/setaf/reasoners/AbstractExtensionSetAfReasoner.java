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

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.setaf.syntax.SetAf;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.postulates.PostulateEvaluatable;

/**
 * Ancestor class for all SetAf-extension-based reasoners.
 * 
 * @author Sebastian Franke
 */
public abstract class AbstractExtensionSetAfReasoner extends AbstractSetAfReasoner implements ModelProvider<Argument, SetAf, Extension<SetAf>>, PostulateEvaluatable<Argument> {

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractDungReasoner#query(org.tweetyproject.arg.dung.syntax.DungTheory, org.tweetyproject.arg.dung.syntax.Argument)
	 */
	@Override
	public Boolean query(SetAf beliefbase, Argument formula) {		
		return this.query(beliefbase, formula, InferenceMode.SKEPTICAL);
	}

	/**
	 * Queries the given AAF for the given argument using the given 
	 * inference type.
	 * @param beliefbase an AAF
	 * @param formula a single argument
	 * @param inferenceMode either InferenceMode.SKEPTICAL or InferenceMode.CREDULOUS
	 * @return "true" if the argument is accepted
	 */
	public Boolean query(SetAf beliefbase, Argument formula, InferenceMode inferenceMode) {
		Collection<Extension<SetAf>> extensions = this.getModels(beliefbase);
		if(inferenceMode.equals(InferenceMode.SKEPTICAL)){
			for(Extension e: extensions)
				if(!e.contains(formula))
					return false;
			return true;
		}
		// so its credulous semantics
		for(Extension e: extensions){
			if(e.contains(formula))
				return true;			
		}			
		return false;
	}
	
	/**
	 * Creates a reasoner for the given semantics.
	 * @param semantics a semantics
	 * @return a reasoner for the given Dung theory, inference type, and semantics
	 */
	public static AbstractExtensionSetAfReasoner getSimpleReasonerForSemantics(Semantics semantics){
		switch(semantics){
			case CO: return new SimpleCompleteSetAfReasoner();
			case GR: return new SimpleGroundedSetAfReasoner();
			case PR: return new SimplePreferredSetAfReasoner();
			case ST: return new SimpleStableSetAfReasoner();
			case ADM: return new SimpleAdmissibleSetAfReasoner();
			case CF: return new SimpleConflictFreeSetAfReasoner();
			case SST: return new SimpleSemiStableSetAfReasoner();
			case ID: return new SimpleIdealSetAfReasoner();
			case EA: return new SimpleEagerSetAfReasoner();
			case STG: return new SimpleStageSetAfReasoner();
		default:
			throw new IllegalArgumentException("Unknown semantics.");			
		}		
	}
	

}
