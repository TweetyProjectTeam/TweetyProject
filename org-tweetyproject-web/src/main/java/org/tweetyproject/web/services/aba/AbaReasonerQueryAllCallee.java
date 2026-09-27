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
package org.tweetyproject.web.services.aba;

import org.tweetyproject.arg.aba.reasoner.GeneralAbaReasoner;
import org.tweetyproject.arg.aba.semantics.AbaExtension;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.web.services.Callee;

/**
 * Callee returning all credulously or skeptically accepted assumptions of an ABA theory.
 *
 * @param <T> The type of formula used in ABA
 */
public class AbaReasonerQueryAllCallee<T extends Formula> extends Callee {

    /** The ABA reasoner instance */
    private final GeneralAbaReasoner<T> reasoner;

    /** The ABA theory instance */
    private final AbaTheory<T> bbase;

    /** The inference mode */
    private final InferenceMode mode;

    /**
     * Constructs a new AbaReasonerQueryAllCallee.
     *
     * @param reasoner The ABA reasoner
     * @param bbase    The ABA theory
     * @param mode     The inference mode
     */
    public AbaReasonerQueryAllCallee(GeneralAbaReasoner<T> reasoner, AbaTheory<T> bbase, InferenceMode mode) {
        this.reasoner = reasoner;
        this.bbase = bbase;
        this.mode = mode;
    }

    /**
     * Returns the accepted assumptions.
     *
     * @return the assumptions accepted under the inference mode
     * @throws Exception If an error occurs during reasoning
     */
    @Override
    public AbaExtension<T> call() throws Exception {
        return new AbaExtension<>(this.reasoner.queryAll(this.bbase, this.mode));
    }
}
