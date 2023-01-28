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
 * This class models a clausal attack constraint
 * @author Matthias Thimm
 *
 */
public class ClausalAttackConstraint implements AttackConstraint<Collection<PlFormula>> {
    /**argument*/
	private Argument argument;
	/**clauses*/
    private Collection<PlFormula> clauses;
    /**
     * simple constructor
     * 
     * @param arg Argument
     */
    public ClausalAttackConstraint(Argument arg) {
        this.argument = arg;
        this.clauses = new HashSet<>();
    }

    /**
     * 
     * @param arg argument
     * @param input input
     */
    public ClausalAttackConstraint(Argument arg, Input input) {
        this.argument = arg;
        this.clauses = this.getConditionForArgument(arg, input);
    }

    /**
     * 
     * @param condition1 condition 1 
     * @param condition2 condition 2
     */
    public ClausalAttackConstraint(ClausalAttackConstraint condition1, ClausalAttackConstraint condition2) {
        if (condition1.getArgument() != condition2.getArgument()) {
            throw new IllegalArgumentException("Should not happen");
        }
        this.argument = condition1.getArgument();
        this.clauses = new HashSet<>();
        this.clauses.addAll(condition1.getCondition());
        this.clauses.addAll(condition2.getCondition());
    }

    /**
     * returns condition
     */
    public Collection<PlFormula> getCondition() {
        return this.clauses;
    }
    /**
     * returns the argument
     */
    public Argument getArgument() {
        return this.argument;
    }

    /**
     * 
     * @param arg argument
     * @param input input
     * @return a collection of formulas 
     */
    private Collection<PlFormula> getConditionForArgument(Argument arg, Input input) {
        return switch (input.getSemantics()) {
            case CF -> this.getConditionForArgumentCF(arg, input);
            case ADM -> this.getConditionForArgumentADM(arg, input);
            case CO -> this.getConditionForArgumentCO(arg, input);
            case ST -> this.getConditionForArgumentST(arg, input);
            default -> throw new IllegalArgumentException("Unsupported Semantics");
        };
    }

    /**
     * 
     * @param arg argument
     * @param input input
     * @return the condition for the argument
     */
    private Collection<PlFormula> getConditionForArgumentCF(Argument arg, Input input) {
        Collection<PlFormula> formula = new HashSet<>();
        switch (input.get(arg)) {
            case UNDECIDED:
            case IN:
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.IN)) {
                    formula.add(new Negation(new Proposition(b.getName())));
                }
                break;
            case OUT:
                Collection<PlFormula> poss_attackers = new HashSet<>();
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.IN)) {
                    poss_attackers.add(new Proposition(b.getName()));
                }
                formula.add(new Disjunction(poss_attackers));
                break;
        }
        return formula;
    }

    /**
     * 
     * @param arg argument
     * @param input an input
     * @return the condition for the argument admissible
     */
    private Collection<PlFormula> getConditionForArgumentADM(Argument arg, Input input) {
        Collection<PlFormula> formula = new HashSet<>();
        switch (input.get(arg)) {
            case IN:
                // an IN argument can not be attacked by another IN argument or a UNDECIDED argument (i.e. then it would not be defended)
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.IN)) {
                    formula.add(new Negation(new Proposition(b.getName())));
                }
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.UNDECIDED)) {
                    formula.add(new Negation(new Proposition(b.getName())));
                }
                break;
            case OUT:
                Collection<PlFormula> poss_attackers = new HashSet<>();
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.IN)) {
                    poss_attackers.add(new Proposition(b.getName()));
                }
                formula.add(new Disjunction(poss_attackers));
                break;
            case UNDECIDED:
                // an UNDECIDED argument can not be attacked by an IN argument (i.e. then it would be OUT)
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.IN)) {
                    formula.add(new Negation(new Proposition(b.getName())));
                }
                break;
        }
        return formula;
    }

    /**
     * 
     * @param arg argument
     * @param input input
     * @return the condition for the argument completness
     */
    private Collection<PlFormula> getConditionForArgumentCO(Argument arg, Input input) {
        Collection<PlFormula> formula = new HashSet<>();
        switch (input.get(arg)) {
            case IN:
                // an IN argument can not be attacked by another IN argument or a UNDECIDED argument (i.e. then it would not be defended)
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.IN)) {
                    formula.add(new Negation(new Proposition(b.getName())));
                }
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.UNDECIDED)) {
                    formula.add(new Negation(new Proposition(b.getName())));
                }
                break;
            case OUT:
                Collection<PlFormula> poss_attackers = new HashSet<>();
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.IN)) {
                    poss_attackers.add(new Proposition(b.getName()));
                }
                formula.add(new Disjunction(poss_attackers));
                break;
            case UNDECIDED:
                // an UNDECIDED argument can not be attacked by an IN argument (i.e. then it would be OUT)
                for (Argument b: input.getArgumentsOfStatus(ArgumentStatus.IN)) {
                    formula.add(new Negation(new Proposition(b.getName())));
                }
                Collection<PlFormula> und_arguments = new HashSet<>();
                for (Argument c: input.getArgumentsOfStatus(ArgumentStatus.UNDECIDED)) {
                    und_arguments.add(new Proposition(c.getName()));
                }
                AssociativePlFormula sub_formula = new Disjunction(und_arguments);
                formula.add(sub_formula);
                break;
        }
        return formula;
    }

    /**
     * 
     * @param arg argument
     * @param input input
     * @return conditon for argument (stable)
     */
    private Collection<PlFormula> getConditionForArgumentST(Argument arg, Input input) {
        if (!input.getArgumentsOfStatus(ArgumentStatus.UNDECIDED).isEmpty())
            throw new IllegalArgumentException("Labeling is not stable");
        return getConditionForArgumentCF(arg, input);
    }

    @Override
    public String toString() {
        return argument + ":\t\t" + clauses;
    }

}
