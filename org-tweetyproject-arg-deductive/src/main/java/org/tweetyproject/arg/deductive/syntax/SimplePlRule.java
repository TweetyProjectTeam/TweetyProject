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
 *  Copyright 2017 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.deductive.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.commons.util.rules.Rule;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;

/**
 * Represents a simple propositional logic rule.
 * The rule consists of a set of premises (support) and a conclusion (claim).
 * The premises and conclusion are propositional logic formulas ({@link PlFormula}).
 *
 * This class implements the {@link Rule} interface and provides basic functionality
 * for manipulating and querying the premises and conclusion of the rule.
 *
 * @author Federico Cerutti (federico.cerutti@acm.org)
 */
public class SimplePlRule implements Rule<PlFormula, PlFormula> {

    /** The conclusion (claim) of the rule */
    private PlFormula claim = null;

    /** The set of premises (support) for the rule */
    private Set<PlFormula> support = null;

    /**
     * Default constructor. Initializes the rule with no conclusion and an empty set of premises.
     */
    public SimplePlRule() {
        support = new HashSet<PlFormula>();
    }

    /**
     * Constructs a rule with the specified conclusion and an empty set of premises.
     *
     * @param _claim The propositional logic formula representing the conclusion of the rule.
     */
    public SimplePlRule(PlFormula _claim) {
        claim = _claim;
        support = new HashSet<PlFormula>();
    }

    /**
     * Constructs a rule with the specified conclusion and set of premises.
     *
     * @param _claim   The propositional logic formula representing the conclusion of the rule.
     * @param _support The set of propositional logic formulas representing the premises of the rule.
     */
    public SimplePlRule(PlFormula _claim, Set<PlFormula> _support) {
        claim = _claim;
        support = _support;
    }

    /**
     * Adds a premise to the rule.
     *
     * @param arg0 The propositional logic formula to be added as a premise.
     */
    public void addPremise(PlFormula arg0) {
        this.support.add(arg0);
    }

    /**
     * Adds a collection of premises to the rule.
     *
     * @param arg0 The collection of propositional logic formulas to be added as premises.
     */
    public void addPremises(Collection<? extends PlFormula> arg0) {
        this.support.addAll(arg0);
    }

    /**
     * Retrieves the conclusion (claim) of the rule.
     *
     * @return The propositional logic formula representing the conclusion of the rule.
     */
    public PlFormula getConclusion() {
        return this.claim;
    }

    /**
     * Retrieves the premises (support) of the rule.
     *
     * @return The set of propositional logic formulas representing the premises of the rule.
     */
    public Collection<? extends PlFormula> getPremise() {
        return this.support;
    }

    /**
     * Retrieves the signature of the rule, which includes the signature of both the premises and the conclusion.
     *
     * @return The propositional logic signature of the rule.
     */
    public Signature getSignature() {
        PlSignature sig = new PlSignature();
        sig.add(this.claim.getSignature());
        for (Formula p : this.support) {
            sig.add((PlSignature) p.getSignature());
        }
        return sig;
    }

    /**
     * Determines if this rule is a constraint.
     * In this case, the rule is not considered a constraint.
     *
     * @return False, as this rule is not a constraint.
     */
    public boolean isConstraint() {
        return false;
    }

    /**
     * Determines if this rule is a fact.
     * A rule is considered a fact if it has no premises.
     *
     * @return True if the rule has no premises, false otherwise.
     */
    public boolean isFact() {
        return this.support.isEmpty();
    }

    /**
     * Sets the conclusion (claim) of the rule.
     *
     * @param arg0 The propositional logic formula to be set as the conclusion of the rule.
     */
    public void setConclusion(PlFormula arg0) {
        this.claim = arg0;
    }

    /**
     * Checks if this rule is equal to another object.
     * Two rules are considered equal if they have the same conclusion and premises.
     *
     * @param obj The object to compare with this rule.
     * @return True if the rules are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimplePlRule other = (SimplePlRule) obj;
        if (!this.claim.equals(other.claim))
            return false;
        if (this.support == null) {
            if (other.support != null) {
                return false;
            }
        } else if (!support.equals(other.support))
            return false;
        return true;
    }

    /**
     * Computes the hash code for this rule based on the premises.
     *
     * @return The hash code of the rule.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((support == null) ? 0 : support.hashCode());
        return result;
    }

    /**
     * Returns a string representation of this rule.
     * If the rule is a fact (i.e., has no premises), only the conclusion is returned.
     * Otherwise, the premises and conclusion are returned in the format "premises -> conclusion".
     *
     * @return The string representation of this rule.
     */
    @Override
    public String toString() {
        if (this.isFact()) {
            return this.claim.toString();
        }
        return this.support.toString() + " -> " + this.claim.toString();
    }
}
