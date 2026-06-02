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
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.QualitativeReasoner;

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

    public Boolean query(AbstractDialecticalFramework bbase, Argument formula, InferenceMode mode) {
        return null; // TODO implement
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
