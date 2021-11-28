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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.postulates.PostulateEvaluatable;

/**
 * Ancestor class for all extension-based reasoners.
 * 
 * @author Matthias Thimm
 */
public abstract class AbstractExtensionReasoner extends AbstractDungReasoner implements ModelProvider<Argument,DungTheory,Extension<DungTheory>>, PostulateEvaluatable<Argument> {

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractDungReasoner#query(org.tweetyproject.arg.dung.syntax.DungTheory, org.tweetyproject.arg.dung.syntax.Argument)
	 */
	@Override
	public Boolean query(DungTheory beliefbase, Argument formula) {		
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
	public Boolean query(DungTheory beliefbase, Argument formula, InferenceMode inferenceMode) {
		Collection<Extension<DungTheory>> extensions = this.getModels(beliefbase);
		if(inferenceMode.equals(InferenceMode.SKEPTICAL)){
			for(Extension<DungTheory> e: extensions)
				if(!e.contains(formula))
					return false;
			return true;
		}
		// so its credulous semantics
		for(Extension<DungTheory> e: extensions){
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
	public static AbstractExtensionReasoner getSimpleReasonerForSemantics(Semantics semantics){
		switch(semantics){
			case CO: return new SimpleCompleteReasoner();
			case GR: return new SimpleGroundedReasoner();
			case PR: return new SimplePreferredReasoner();
			case ST: return new SimpleStableReasoner();
			case ADM: return new SimpleAdmissibleReasoner();
			case CF: return new SimpleConflictFreeReasoner();
			case SST: return new SimpleSemiStableReasoner();
			case ID: return new SimpleIdealReasoner();
			case EA: return new SimpleEagerReasoner();
			case STG: return new SimpleStageReasoner();
			case STG2: return new Stage2Reasoner();
			case CF2: return new SccCF2Reasoner();
			case SCF2: return new SCF2Reasoner();
			case WAD: return new WeaklyAdmissibleReasoner();
			case N: return new SimpleNaiveReasoner();
		default:
			throw new IllegalArgumentException("Unknown semantics.");			
		}		
	}
	
	/**
	 * the solver is natively installed and is therefore always installed
	 */
	@Override
	public boolean isInstalled() {
		return true;
	}
	

}
