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
package org.tweetyproject.arg.adf.reasoner;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.QualitativeReasoner;

import java.util.Collection;
import java.util.HashSet;

/**
 * Abstract class for reasoners of ADF semantics
 *
 * @author Lars Bengel
 */
public abstract class AbstractADFReasoner implements QualitativeReasoner<AbstractDialecticalFramework, Argument>, ModelProvider<Argument,AbstractDialecticalFramework,Interpretation> {
    @Override
    public Boolean query(AbstractDialecticalFramework bbase, Argument formula) {
        return this.query(bbase, formula, InferenceMode.SKEPTICAL);
    }

    /**
     * Determine the set of acceptable arguments wrt. the given inference mode
     * @param bbase some ADF
     * @param inferenceMode the inference mode
     * @return the set of acceptable arguments
     */
    public Collection<Argument> queryAll(AbstractDialecticalFramework bbase, InferenceMode inferenceMode) {
        Collection<Argument> result = new HashSet<>();
        Collection<Interpretation> models = this.getModels(bbase);
        if(inferenceMode.equals(InferenceMode.CREDULOUS)) {
            for (Interpretation model : models)
                result.addAll(model.satisfied());
        } else {
            result.addAll(bbase);
            for(Interpretation model: models)
                result.retainAll(model.satisfied());
        }
        return result;
    }

    /**
     * Queries the given ADF for the given argument using the given
     * inference type.
     * @param beliefbase an ADF
     * @param formula a single argument
     * @param inferenceMode either InferenceMode.SKEPTICAL or InferenceMode.CREDULOUS
     * @return "true" if the argument is accepted
     */
    public Boolean query(AbstractDialecticalFramework beliefbase, Argument formula, InferenceMode inferenceMode) {
        Collection<Interpretation> models = this.getModels(beliefbase);
        if(inferenceMode.equals(InferenceMode.SKEPTICAL)){
            for(Interpretation m: models)
                if(!m.satisfies(formula))
                    return false;
            return true;
        }
        // so its credulous semantics
        for(Interpretation m: models){
            if(m.satisfies(formula))
                return true;
        }
        return false;
    }

    @Override
    public boolean isInstalled() {
        return true;
    }

    @Override
    public Interpretation getModel(AbstractDialecticalFramework bbase) {
        return getModels(bbase).iterator().next();
    }
}
