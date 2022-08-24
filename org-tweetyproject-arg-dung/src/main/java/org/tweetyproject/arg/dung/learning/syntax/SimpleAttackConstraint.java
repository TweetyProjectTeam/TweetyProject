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

package org.tweetyproject.arg.dung.learning.syntax;

import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.logics.pl.syntax.*;

import java.util.Collection;
import java.util.HashSet;

/**
 * Implementation of the acceptance conditions used for learning argumentation frameworks from labelings
 * Notation:
 * for each acceptance condition we also store the argument a it is associated with
 * In the condition itself we then use !b and b to represent !r_(ba) and r_(ba) respectively
 * e.g. the condition C_a = !a && !c && b means a is not attacked by a and c, but must be attacked by b
 *
 * @author Lars Bengel
 */
public class SimpleAttackConstraint implements AttackConstraint<AssociativePlFormula> {

    /** the argument this acceptance condition is for */
    protected Argument argument;
    /** the actual condition */
    protected AssociativePlFormula condition;

    /**
     * initialize empty acceptance condition for the given argument
     * in tha case any incoming attack is optional
     * @param arg some argument
     */
    public SimpleAttackConstraint(Argument arg) {
        this.argument = arg;
        this.condition = new Conjunction();
    }

    /**
     * compute acceptance condition for the given argument with respect to the given input labeling
     * @param arg some argument
     * @param input some input labeling
     */
    public SimpleAttackConstraint(Argument arg, Input input) {
        this.argument = arg;
        this.condition = getConditionForArgument(arg, input);
    }

    /**
     * compute acceptance condition by combining two acceptance conditions for the same argument a
     *
     * @param condition1 some condition for the argument a
     * @param condition2 some condition for the argument a
     */
    public SimpleAttackConstraint(SimpleAttackConstraint condition1, SimpleAttackConstraint condition2) {
        this(condition1, condition2, false);
    }

    /**
     * compute acceptance condition by combining two acceptance conditions for the same argument a
     * after combining the acceptance condition is transformed into DNF
     * this leads to better computability in later steps
     * see: thesis.util.ModelComputation
     *
     * @param condition1 some condition for the argument a
     * @param condition2 some condition for the argument a
     * @param toDNF true if acceptance condition should be transformed to DNF
     */
    public SimpleAttackConstraint(SimpleAttackConstraint condition1, SimpleAttackConstraint condition2, boolean toDNF) {
        // throw exception if the conditions are for different arguments
        if (!condition1.getArgument().equals(condition2.getArgument())) {
            System.out.println("\n"+condition1);
            System.out.println(condition2);
            throw new IllegalArgumentException("Should not happen");
        }
        this.argument = condition1.getArgument();
        if (toDNF) {
            // combine conditions and transform to dnf
            this.condition = (AssociativePlFormula) new Conjunction(condition1.getCondition(), condition2.getCondition()).collapseAssociativeFormulas().toDnf().trim();
        } else {
            this.condition = (AssociativePlFormula) new Conjunction(condition1.getCondition(), condition2.getCondition()).collapseAssociativeFormulas().trim();
        }

    }

    /**
     * return the acceptance condition
     * @return the condition
     */
    public AssociativePlFormula getCondition() {
        return condition;
    }

    /**
     * return the argument the condition is for
     * @return the argument
     */
    public Argument getArgument() {
        return this.argument;
    }

    /**
     * compute the acceptance condition with respect to the labeling and its semantics for the given argument
     * @param arg some argument
     * @param input some input labeling
     * @return the acceptance condition for arg wrt. the input labeling
     */
    public AssociativePlFormula getConditionForArgument(Argument arg, Input input) {
        switch (input.getSemantics()) {
            case CF:
                return this.getConditionForArgumentCF(arg, input);
            case ADM:
                return this.getConditionForArgumentADM(arg, input);
            case CO:
                return this.getConditionForArgumentCO(arg, input);
            case ST:
                return this.getConditionForArgumentST(arg, input);
            default:
                throw new IllegalArgumentException("Unsupported Semantics");
        }
    }

    /**
     * compute the acceptance condition for arg wrt. to a conflict-free labeling
     * @param arg some argument
     * @param input some input labeling
     * @return the condition for arg considering input as a conflict-free labeling
     */
    protected AssociativePlFormula getConditionForArgumentCF(Argument arg, Input input) {
        AssociativePlFormula formula = new Conjunction();
        switch (input.get(arg)) {
            case UNDECIDED:
            case IN:
                Collection<PlFormula> cf_arguments = new HashSet<>();
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.IN)) {
                    cf_arguments.add(new Negation(new Proposition(b.getName())));
                }
                formula = new Conjunction(cf_arguments);
                break;
            case OUT:
                Collection<PlFormula> poss_attackers = new HashSet<>();
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.IN)) {
                    poss_attackers.add(new Proposition(b.getName()));
                }
                formula = new Disjunction(poss_attackers);
                break;
        }
        return formula;
    }

    /**
     * compute the acceptance condition for arg wrt. to an admissible labeling
     * @param arg some argument
     * @param input some input labeling
     * @return the condition for arg considering input as an admissible labeling
     */
    protected AssociativePlFormula getConditionForArgumentADM(Argument arg, Input input) {
        AssociativePlFormula formula = new Conjunction();
        switch (input.get(arg)) {
            case IN:
                // an IN argument can not be attacked by another IN argument or a UNDECIDED argument (i.e. then it would not be defended)
                Collection<PlFormula> cf_arguments = new HashSet<>();
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.IN)) {
                    cf_arguments.add(new Negation(new Proposition(b.getName())));
                }
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.UNDECIDED)) {
                    cf_arguments.add(new Negation(new Proposition(b.getName())));
                }
                formula = new Conjunction(cf_arguments);
                break;
            case OUT:
                Collection<PlFormula> poss_attackers = new HashSet<>();
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.IN)) {
                    poss_attackers.add(new Proposition(b.getName()));
                }
                formula = new Disjunction(poss_attackers);
                break;
            case UNDECIDED:
                // an UNDECIDED argument can not be attacked by an IN argument (i.e. then it would be OUT)
                Collection<PlFormula> in_arguments = new HashSet<>();
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.IN)) {
                    in_arguments.add(new Negation(new Proposition(b.getName())));
                }
                formula = new Conjunction(in_arguments);
                break;


        }
        return formula;
    }

    /**
     * compute the acceptance condition for arg wrt. to a complete labeling
     * @param arg some argument
     * @param input some input labeling
     * @return the condition for arg considering input as a complete labeling
     */
    protected AssociativePlFormula getConditionForArgumentCO(Argument arg, Input input) {
        AssociativePlFormula formula = new Conjunction();
        switch (input.get(arg)) {
            case IN:
                // an IN argument can not be attacked by another IN argument or a UNDECIDED argument (i.e. then it would not be defended)
                Collection<PlFormula> cf_arguments = new HashSet<>();
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.IN)) {
                    cf_arguments.add(new Negation(new Proposition(b.getName())));
                }
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.UNDECIDED)) {
                    cf_arguments.add(new Negation(new Proposition(b.getName())));
                }
                formula = new Conjunction(cf_arguments);
                break;
            case OUT:
                Collection<PlFormula> poss_attackers = new HashSet<>();
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.IN)) {
                    poss_attackers.add(new Proposition(b.getName()));
                }
                formula = new Disjunction(poss_attackers);
                break;
            case UNDECIDED:
                // an UNDECIDED argument can not be attacked by an IN argument (i.e. then it would be OUT)
                Collection<PlFormula> in_arguments = new HashSet<>();
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.IN)) {
                    in_arguments.add(new Negation(new Proposition(b.getName())));
                }
                formula = new Conjunction(in_arguments);
                Collection<PlFormula> und_arguments = new HashSet<>();
                for (Argument c: input.getArgumentsOfStatus(ArgumentStatus.UNDECIDED)) {
                    und_arguments.add(new Proposition(c.getName()));
                }
                AssociativePlFormula sub_formula = new Disjunction(und_arguments);

                formula = new Conjunction(formula, sub_formula);
                break;
        }
        return formula;
    }

    /**
     * compute the acceptance condition for arg wrt. to a stable labeling
     * @param arg some argument
     * @param input some input labeling
     * @return the condition for arg considering input as a stable labeling
     */
    protected AssociativePlFormula getConditionForArgumentST(Argument arg, Input input) {
        if (!input.getArgumentsOfStatus(ArgumentStatus.UNDECIDED).isEmpty())
            throw new IllegalArgumentException("Labeling is not stable");
        return getConditionForArgumentCF(arg, input);
    }

    /**
     * compute the optional acceptance condition for this argument based on its acceptance condition
     * @param arguments the set of all arguments
     * @return the optional acceptance condition of this argument
     */
    public AssociativePlFormula getOptionalCondition(Collection<Argument> arguments) {
        Collection<Argument> args = new HashSet<>();
        for (Proposition atom: this.condition.getAtoms()) {
            args.add(new Argument(atom.getName()));
        }
        Collection<Argument> optionalArguments = new HashSet<>(arguments);
        optionalArguments.removeAll(args);
        Collection<PlFormula> optionalAtoms = new HashSet<>();
        for (Argument a: optionalArguments) {
            optionalAtoms.add(new Proposition(a.getName()));
        }
        return new Disjunction(optionalAtoms);
    }

    @Override
    public String toString() {
        return argument + ":\t\t" + condition;
    }
}
