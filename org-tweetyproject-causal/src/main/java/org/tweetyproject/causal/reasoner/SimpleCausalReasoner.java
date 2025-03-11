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
 * Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.causal.reasoner;

import org.tweetyproject.causal.semantics.CausalInterpretation;
import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.causal.syntax.StructuralCausalModel;
import org.tweetyproject.commons.InterpretationSet;
import org.tweetyproject.logics.commons.analysis.NaiveMusEnumerator;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.sat.SimpleModelEnumerator;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Naive reasoner for causal reasoning
 *
 * @author Lars Bengel
 */
public class SimpleCausalReasoner extends AbstractCausalReasoner {
    @Override
    public Collection<CausalInterpretation> getModels(CausalKnowledgeBase cbase, Collection<PlFormula> observations, Map<Proposition, Boolean> interventions) {
        // Perform interventions
        StructuralCausalModel model = cbase.getCausalModel();
        for (Proposition atom : interventions.keySet()) {
            model = model.intervene(atom, interventions.get(atom));
        }
        PlBeliefSet knowledge = new PlBeliefSet(model.getStructuralEquations());
        knowledge.addAll(observations);

        // Enumerate through all maximally consistent subsets of the assumptions and compute the models for each set + knowledge
        SatSolver solver = new Sat4jSolver();
        NaiveMusEnumerator<PlFormula> enumerator = new NaiveMusEnumerator<>(solver);

        SimpleModelEnumerator modelEnumerator = new SimpleModelEnumerator();

        Collection<Collection<PlFormula>> maximalConsistentSubsets = enumerator.maximalConsistentSubsets(cbase.getAssumptions());

        Collection<CausalInterpretation> result = new HashSet<>();
        for (Collection<PlFormula> set : maximalConsistentSubsets) {
            PlBeliefSet set2 = new PlBeliefSet(knowledge);
            set2.addAll(set);
            for (InterpretationSet<Proposition, PlBeliefSet, PlFormula> itp : modelEnumerator.getModels(set2)) {
                CausalInterpretation interpretation = new CausalInterpretation();
                interpretation.addAll(itp);
                result.add(interpretation);
            }
        }

        return result;
    }
}
