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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.dung.learning.util;

import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.*;

import java.util.Collection;
import java.util.HashSet;

/**
 * Helper class providing methods for more efficient computation of models for specific formulae
 *
 * @author Lars Bengel
 */
public class ModelComputation {
    /**
     * compute the model of a formula in atomic CNF e.g. a and b and !c
     * @param condition some formula which is a conjunction of literals
     * @return the model of this formula
     */
    public static PossibleWorld getModelOfConjunction(Conjunction condition) {
        Collection<Proposition> accepted = new HashSet<>();
        for (PlFormula literal: condition.getLiterals()) {
            // iterate over literals p, if p is negative we skip, otherwise p must be true and part of the model
            if (literal instanceof Negation) {
            } else {
                accepted.addAll(literal.getAtoms());
            }
        }
        return new PossibleWorld(accepted);
    }

    /**
     * compute all models of a formula in disjunctive normal form
     * @param condition some formula in DNF
     * @return all models of the given formula
     */
    public static Collection<PossibleWorld> getModelsOfDNF(AssociativePlFormula condition) {
        Collection<PossibleWorld> models = new HashSet<>();
        // formula is a DNF, compute all models normally
        // TODO make this more efficient possibly
        if (condition instanceof Disjunction) {
            models.addAll(condition.getModels());
            return models;

        } else if (condition instanceof Conjunction) {
            // formula is a DNF with only one subFormula,i.e. its actually just a conjunction of literals
            models.add(ModelComputation.getModelOfConjunction((Conjunction) condition));
        }
        return models;
    }

    /**
     * compute whether the given formula in DNF has more than one model
     * @param condition some formula
     * @return true if there is more than one model for the given formula
     */
    public static boolean existsMoreThanOneModelOfDNF(AssociativePlFormula condition) {
        // if formula is an actual DNF then there is more than one model
        // otherwise if the formula is a conjunction of literals then there is only one model
        if (condition instanceof Disjunction) {
            return true;
        } else {
            return !(condition instanceof Conjunction);
        }
    }

    /**
     * compute a minimal model of the given formula in DNF
     * @param condition some formula in DNF
     * @return a model of the given condition
     */
    public static PossibleWorld getMinModelOfDNF(AssociativePlFormula condition) {
        // the model just needs to fulfill one of the subformulas of condition
        if (condition instanceof Disjunction) {
            // multiple subformulae, get model of first one
            return ModelComputation.getModelOfConjunction((Conjunction) condition.getFormulas().iterator().next());
        } else if (condition instanceof Conjunction) {
            // only one subformula exists
            return ModelComputation.getModelOfConjunction((Conjunction) condition);
        } else {
            throw new IllegalArgumentException("Not possible");
        }
    }
}
