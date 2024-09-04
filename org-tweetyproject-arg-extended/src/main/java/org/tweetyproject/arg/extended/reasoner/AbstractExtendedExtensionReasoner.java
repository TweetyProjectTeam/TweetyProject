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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.extended.reasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.extended.syntax.ExtendedTheory;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.QualitativeReasoner;
import org.tweetyproject.commons.postulates.PostulateEvaluatable;

import java.util.Collection;

/**
 * Abstract extension reasoner for reasoning with extended theories
 *
 * @author Lars Bengel
 */
public abstract class AbstractExtendedExtensionReasoner implements ModelProvider<Argument, ExtendedTheory, Extension<ExtendedTheory>>, PostulateEvaluatable<Argument>, QualitativeReasoner<ExtendedTheory, Argument> {


	/** Default */
	public AbstractExtendedExtensionReasoner(){
		super();
	}
    /**
     * Returns a simple extended theory reasoner for the given semantics
     * @param semantics some semantics
     * @return a simple reasoner for the given semantics
     */
    public static AbstractExtendedExtensionReasoner getSimpleReasonerForSemantics(Semantics semantics) {
        return switch (semantics) {
            case CF -> new SimpleExtendedConflictFreeReasoner();
            case ADM -> new SimpleExtendedAdmissibleReasoner();
            case CO -> new SimpleExtendedCompleteReasoner();
            default -> throw new IllegalArgumentException("Unknown semantics.");
        };
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.arg.dung.reasoner.AbstractDungReasoner#query(org.tweetyproject.arg.dung.syntax.DungTheory, org.tweetyproject.arg.dung.syntax.Argument)
     */
    @Override
    public Boolean query(ExtendedTheory beliefbase, Argument formula) {
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
    public Boolean query(ExtendedTheory beliefbase, Argument formula, InferenceMode inferenceMode) {
        Collection<Extension<ExtendedTheory>> extensions = this.getModels(beliefbase);
        if(inferenceMode.equals(InferenceMode.SKEPTICAL)){
            for(Extension<ExtendedTheory> e: extensions)
                if(!e.contains(formula))
                    return false;
            return true;
        }
        // so its credulous semantics
        for(Extension<ExtendedTheory> e: extensions){
            if(e.contains(formula))
                return true;
        }
        return false;
    }

    @Override
    public boolean isInstalled() {
        return true;
    }
}
