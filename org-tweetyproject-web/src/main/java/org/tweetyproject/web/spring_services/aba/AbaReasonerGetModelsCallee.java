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
package org.tweetyproject.web.spring_services.aba;

import java.util.Collection;

import org.tweetyproject.arg.aba.reasoner.GeneralAbaReasoner;
import org.tweetyproject.arg.aba.semantics.AbaExtension;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.web.spring_services.Callee;

/**
 * The AbaReasonerGetModelsCallee class extends the Callee class and represents
 * a callee responsible for obtaining multiple models from an Argumentation-Based Argumentation (ABA) reasoner.
 *
 * @param <T> The type of formula used in ABA
 */
public class AbaReasonerGetModelsCallee<T extends Formula> extends Callee {

    /** The ABA reasoner instance */
    private GeneralAbaReasoner<T> reasoner;

    /** The ABA theory instance */
    private AbaTheory<T> bbase;

    /**
     * Constructor for AbaReasonerGetModelsCallee.
     *
     * @param reasoner The ABA reasoner instance
     * @param bbase    The ABA theory instance
     */
    public AbaReasonerGetModelsCallee(GeneralAbaReasoner<T> reasoner, AbaTheory<T> bbase) {
        this.reasoner = reasoner;
        this.bbase = bbase;
    }

    /**
     * Calls the ABA reasoner to obtain multiple models from the provided ABA theory.
     *
     * @return A collection of ABA extensions representing the models
     * @throws Exception If an exception occurs during the ABA reasoner operation
     */
    @Override
    public Collection<AbaExtension<T>> call() throws Exception {
        return this.reasoner.getModels(this.bbase);
    }
}

