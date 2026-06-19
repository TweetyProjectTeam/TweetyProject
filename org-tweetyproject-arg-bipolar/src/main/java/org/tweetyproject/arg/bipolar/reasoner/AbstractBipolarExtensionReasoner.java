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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.bipolar.reasoner;

import org.tweetyproject.arg.bipolar.semantics.Semantics;
import org.tweetyproject.arg.bipolar.syntax.BipolarArgumentationFramework;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.QualitativeReasoner;
import org.tweetyproject.commons.postulates.PostulateEvaluatable;

import java.util.Collection;
import java.util.HashSet;

/**
 * Abstract class for reasoners for bipolar argumentation
 *
 * @author Lars Bengel
 */
public abstract class AbstractBipolarExtensionReasoner implements ModelProvider<Argument,BipolarArgumentationFramework, Extension<BipolarArgumentationFramework>>, PostulateEvaluatable<Argument>, QualitativeReasoner<BipolarArgumentationFramework, Argument> {

    /**
     * Default constructor
     */
    public AbstractBipolarExtensionReasoner() {
        super();
    }


    /**
     * Return a reasoner for the given semantics
     * @param semantics some semantics
     * @return reasoner for the semantics
     */
    public static AbstractBipolarExtensionReasoner getSimpleReasonerForSemantics(Semantics semantics) {
        return switch (semantics) {
            case BCF -> new SimpleStronglyConflictFreeReasoner();
            case BCOH -> new SimpleCoherentReasoner();
            case BAD -> new SimpleCoherentAdmissibleReasoner();
            case CAD -> new SimpleCoalitionReasoner(org.tweetyproject.arg.dung.semantics.Semantics.ADM);
            case CCO -> new SimpleCoalitionReasoner(org.tweetyproject.arg.dung.semantics.Semantics.CO);
            case CGR -> new SimpleCoalitionReasoner(org.tweetyproject.arg.dung.semantics.Semantics.GR);
            case CPR -> new SimpleCoalitionReasoner(org.tweetyproject.arg.dung.semantics.Semantics.PR);
            case CST -> new SimpleCoalitionReasoner(org.tweetyproject.arg.dung.semantics.Semantics.ST);
            default -> throw new IllegalArgumentException("Unknown semantics.");
        };
    }

    /**
     * Determine the set of acceptable arguments wrt. the given inference mode
     * @param bbase some bipolar argumentation framework
     * @param inferenceMode the inference mode
     * @return the set of acceptable arguments
     */
    public Collection<Argument> queryAll(BipolarArgumentationFramework bbase, InferenceMode inferenceMode) {
        Collection<Argument> result = new HashSet<>();
        if(inferenceMode.equals(InferenceMode.CREDULOUS))
            for(Collection<Argument> extension: this.getModels(bbase))
                result.addAll(extension);
        else {
            result.addAll(bbase);
            for(Collection<Argument> extension: this.getModels(bbase))
                result.retainAll(extension);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.arg.dung.reasoner.AbstractDungReasoner#query(org.tweetyproject.arg.dung.syntax.DungTheory, org.tweetyproject.arg.dung.syntax.Argument)
     */
    @Override
    public Boolean query(BipolarArgumentationFramework beliefbase, Argument formula) {
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
    public Boolean query(BipolarArgumentationFramework beliefbase, Argument formula, InferenceMode inferenceMode) {
        Collection<Extension<BipolarArgumentationFramework>> extensions = this.getModels(beliefbase);
        if(inferenceMode.equals(InferenceMode.SKEPTICAL)){
            for(Extension<BipolarArgumentationFramework> e: extensions)
                if(!e.contains(formula))
                    return false;
            return true;
        }
        // so its credulous semantics
        for(Extension<BipolarArgumentationFramework> e: extensions){
            if(e.contains(formula))
                return true;
        }
        return false;
    }

    /**
     * the solver is natively installed and is therefore always installed
     */
    @Override
    public boolean isInstalled() {
        return true;
    }
}
