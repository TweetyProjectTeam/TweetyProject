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

    /**
     * Default constructor
     */
    public AbstractExtensionReasoner() {
        super();
    }

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
        return switch (semantics) {
            case CO -> new SimpleCompleteReasoner();
            case GR -> new SimpleGroundedReasoner();
            case PR -> new SimplePreferredReasoner();
            case ST -> new SimpleStableReasoner();
            case ADM -> new SimpleAdmissibleReasoner();
            case CF -> new SimpleConflictFreeReasoner();
            case SST -> new SimpleSemiStableReasoner();
            case ID -> new SimpleIdealReasoner();
            case EA -> new SimpleEagerReasoner();
            case STG -> new SimpleStageReasoner();
            case STG2 -> new Stage2Reasoner();
            case CF2 -> new SccCF2Reasoner();
            case SCF2 -> new SCF2Reasoner();
            case WAD -> new WeaklyAdmissibleReasoner();
            case WCO -> new WeaklyCompleteReasoner();
            case WPR -> new WeaklyPreferredReasoner();
            case WGR -> new WeaklyGroundedReasoner();
            case NA -> new SimpleNaiveReasoner();
            case SAD -> new StronglyAdmissibleReasoner();
            case UD -> new UndisputedReasoner();
            case SUD -> new StronglyUndisputedReasoner();
            case IS -> new SimpleInitialReasoner();
            case UC -> new SerialisedExtensionReasoner(Semantics.UC);
            default -> throw new IllegalArgumentException("Unknown semantics.");
        };
    }

    /**
     * the solver is natively installed and is therefore always installed
     */
    @Override
    public boolean isInstalled() {
        return true;
    }


}
