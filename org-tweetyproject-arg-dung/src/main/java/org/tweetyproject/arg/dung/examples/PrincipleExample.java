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
package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.principles.Principle;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleAdmissibleReasoner;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DungTheoryGenerator;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;
import org.tweetyproject.commons.postulates.PostulateEvaluator;

import java.util.Collection;
import java.util.HashSet;

/**
 * Example code for checking (non-)satisfaction of {@link Principle} for
 * argumentation semantics
 *
 * @author Lars Bengel
 */
public class PrincipleExample {
    /**
     * Execute the example
     *
     * @param args cmdline arguments
     */
    public static void main(String[] args) {
        // Initialize a list of all principles to be checked
        Collection<Principle> all_principles = new HashSet<>();
        all_principles.add(Principle.CONFLICT_FREE);
        all_principles.add(Principle.ADMISSIBILITY);
        all_principles.add(Principle.NAIVETY);
        all_principles.add(Principle.STRONG_ADMISSIBILITY);
        all_principles.add(Principle.I_MAXIMALITY);
        all_principles.add(Principle.REINSTATEMENT);
        all_principles.add(Principle.WEAK_REINSTATEMENT);
        all_principles.add(Principle.CF_REINSTATEMENT);
        all_principles.add(Principle.DIRECTIONALITY);
        all_principles.add(Principle.INRA);
        all_principles.add(Principle.MODULARIZATION);
        all_principles.add(Principle.REDUCT_ADM);
        all_principles.add(Principle.SEMIQUAL_ADM);
        all_principles.add(Principle.SCC_DECOMPOSABILITY);
        all_principles.add(Principle.ALLOWING_ABSTENTION);
        all_principles.add(Principle.DEFENCE);
        all_principles.add(Principle.WEAK_DIRECTIONALITY);
        all_principles.add(Principle.NON_INTERFERENCE);
        all_principles.add(Principle.SEMI_DIRECTIONALITY);

        // Evaluate the given reasoner wrt to specified principles
        evaluateReasoner(new SimpleAdmissibleReasoner(), all_principles);
    }

    /**
     * Evaluates a given abstract extension reasoner against a collection of
     * principles (postulates).
     * <p>
     * This method uses a {@link DungTheoryGenerator} to generate Dung argumentation
     * frameworks
     * and evaluates how well the provided {@link AbstractExtensionReasoner}
     * satisfies the given principles.
     * The evaluation is conducted using a {@link PostulateEvaluator}, which applies
     * the principles to
     * generated frameworks and assesses the reasoner's performance.
     * </p>
     *
     * @param reasoner   The {@link AbstractExtensionReasoner} to be evaluated. This
     *                   reasoner
     *                   provides extensions for argumentation frameworks.
     * @param principles A collection of {@link Principle} objects representing the
     *                   postulates
     *                   against which the reasoner will be evaluated.
     */
    public static void evaluateReasoner(AbstractExtensionReasoner reasoner, Collection<Principle> principles) {
        DungTheoryGenerator generator = new EnumeratingDungTheoryGenerator();
        PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(generator, reasoner);
        evaluator.addAllPostulates(principles);
        System.out.println(evaluator.evaluate(4000, false).prettyPrint());
    }

}
