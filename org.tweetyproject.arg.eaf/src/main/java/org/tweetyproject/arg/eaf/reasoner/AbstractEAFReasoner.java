/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.eaf.reasoner;

import java.util.Collection;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.eaf.semantics.EAFSemantics;
import org.tweetyproject.arg.eaf.syntax.EpistemicArgumentationFramework;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.QualitativeReasoner;
import org.tweetyproject.commons.postulates.PostulateEvaluatable;

/**
 * 
 * @author Sandra Hoffmann
 *
 */
public abstract class AbstractEAFReasoner implements QualitativeReasoner<EpistemicArgumentationFramework,Argument>, ModelProvider<Argument,EpistemicArgumentationFramework,Extension<EpistemicArgumentationFramework>>, PostulateEvaluatable<Argument> {

	
    /* (non-Javadoc)
     * @see org.tweetyproject.arg.dung.reasoner.AbstractDungReasoner#query(org.tweetyproject.arg.dung.syntax.DungTheory, org.tweetyproject.arg.dung.syntax.Argument)
     */
    @Override
    public Boolean query(EpistemicArgumentationFramework beliefbase, Argument formula) {
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
    public Boolean query(EpistemicArgumentationFramework beliefbase, Argument formula, InferenceMode inferenceMode) {
        Collection<Extension<EpistemicArgumentationFramework>> extensions = this.getModels(beliefbase);
        if(inferenceMode.equals(InferenceMode.SKEPTICAL)){
            for(Extension<EpistemicArgumentationFramework> e: extensions)
                if(!e.contains(formula))
                    return false;
            return true;
        }
        // so its credulous semantics
        for(Extension<EpistemicArgumentationFramework> e: extensions){
            if(e.contains(formula))
                return true;
        }
        return false;
    }

    /**
     * Creates a reasoner for the given semantics.
     * @param semantics a semantics
     * @return a reasoner for the given CAF theory, inference type, and semantics
     */
    public static AbstractEAFReasoner getSimpleReasonerForSemantics(EAFSemantics semantics){
        return switch (semantics) {
            case EAF_GR -> new SimpleEAFGroundedReasoner();
            case EAF_CO -> new SimpleEAFCompleteReasoner();
            case EAF_PR -> new SimpleEAFPreferredReasoner();
            case EAF_ST -> new SimpleEAFStableReasoner();
            case EAF_ADM -> new SimpleEAFAdmissibleReasoner();
            default -> throw new IllegalArgumentException("Unknown semantics.");
        };
    }
	


	@Override
	public boolean isInstalled() {
		return true;
	}
	
}
