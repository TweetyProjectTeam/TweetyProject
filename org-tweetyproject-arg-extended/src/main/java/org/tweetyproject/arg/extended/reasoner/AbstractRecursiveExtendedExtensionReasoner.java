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

import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungEntity;
import org.tweetyproject.arg.extended.syntax.RecursiveExtendedTheory;
import org.tweetyproject.commons.InferenceMode;

import java.util.Collection;

/**
 * Abstract extension reasoner for reasoning with recursive extended theories
 *
 * @author Lars Bengel
 */
public abstract class AbstractRecursiveExtendedExtensionReasoner {

    /** Default */
    public AbstractRecursiveExtendedExtensionReasoner() {
        super();
    }

    /**
     * Returns a simple recursive extended theory reasoner for the given semantics
     *
     * @param semantics some semantics
     * @return a simple reasoner for the given semantics
     */
    public static AbstractRecursiveExtendedExtensionReasoner getSimpleReasonerForSemantics(Semantics semantics) {
        return switch (semantics) {
            case CF -> new SimpleRecursiveExtendedConflictFreeReasoner();
            case ADM -> new SimpleRecursiveExtendedAdmissibleReasoner();
            case CO -> new SimpleRecursiveExtendedCompleteReasoner();
            default -> throw new IllegalArgumentException("Unknown semantics.");
        };
    }

    /*
     * (non-Javadoc)
     *
     * @see org.tweetyproject.arg.dung.reasoner.AbstractDungReasoner#query(org.
     * tweetyproject.arg.dung.syntax.DungTheory,
     * org.tweetyproject.arg.dung.syntax.Argument)
     */
    /**
     * Queries the given belief base with a specified argument, using the default
     * inference mode
     * of skeptical reasoning.
     * This method checks whether the provided argument (formula) is entailed by the
     * belief base
     * under skeptical inference mode.
     *
     * @param beliefbase The belief base, represented as a
     *                   {@link RecursiveExtendedTheory}, to be queried.
     * @param formula    The argument (formula) to be queried.
     * @return {@code TRUE} if the formula is entailed by the belief base under
     *         skeptical reasoning, {@code FALSE} otherwise.
     */
    public Boolean query(RecursiveExtendedTheory beliefbase, Argument formula) {
        return this.query(beliefbase, formula, InferenceMode.SKEPTICAL);
    }

    /**
     * Queries the given AAF for the given argument using the given
     * inference type.
     *
     * @param beliefbase    an AAF
     * @param formula       a single argument
     * @param inferenceMode either InferenceMode.SKEPTICAL or
     *                      InferenceMode.CREDULOUS
     * @return "true" if the argument is accepted
     */
    public Boolean query(RecursiveExtendedTheory beliefbase, Argument formula, InferenceMode inferenceMode) {
        Collection<Collection<DungEntity>> extensions = this.getModels(beliefbase);
        if (inferenceMode.equals(InferenceMode.SKEPTICAL)) {
            for (Collection<DungEntity> e : extensions)
                if (!e.contains(formula))
                    return false;
            return true;
        }
        // so its credulous semantics
        for (Collection<DungEntity> e : extensions) {
            if (e.contains(formula))
                return true;
        }
        return false;
    }

    /**
     * Computes all models for the given theory
     *
     * @param bbase some recursive extended theory
     * @return the models of this theory wrt. some semantics
     */
    public abstract Collection<Collection<DungEntity>> getModels(RecursiveExtendedTheory bbase);

    /**
     * The reasoner is native and thus always installed
     *
     * @return "true"
     */
    public boolean isInstalled() {
        return true;
    }
}
