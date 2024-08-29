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

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.util.rules.Derivation;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * Represents a simple propositional logic argument in deductive argumentation.
 * The argument consists of a set of supporting rules and a claim, which is a
 * propositional logic formula (PlFormula). This class follows the structure
 * defined in the paper "A Comparison of Formal Argumentation Systems" by Hunter and
 * Caminada (2013).
 *
 * This class extends {@link Argument} and stores a set of supporting rules
 * and a single claim derived from the support.
 *
 * @author Federico Cerutti (federico.cerutti(at)acm.org)
 */
public class SimplePlLogicArgument extends Argument {

    /** The support set of rules for this argument */
    private Collection<SimplePlRule> support = null;

    /** The claim of this argument, represented as a propositional logic formula */
    private PlFormula claim = null;

    /**
     * Constructs a simple propositional logic argument with the given support
     * and claim.
     *
     * @param _support The collection of supporting rules for this argument.
     * @param _claim The propositional logic formula representing the claim.
     */
    public SimplePlLogicArgument(Collection<SimplePlRule> _support, PlFormula _claim) {
        super(null);
        this.support = _support;
        this.claim = _claim;
    }

    /**
     * Constructs a simple propositional logic argument from a derivation.
     * The derivation consists of a set of rules (support) and a conclusion
     * (claim).
     *
     * @param derivation The derivation from which the argument is created.
     */
    public SimplePlLogicArgument(Derivation<SimplePlRule> derivation) {
        super(null);
        this.support = new HashSet<SimplePlRule>(derivation);
        this.claim = (PlFormula) derivation.getConclusion();
    }

    /**
     * Returns the support of this argument, i.e., the set of rules that
     * provide the basis for the claim.
     *
     * @return The collection of supporting rules.
     */
    public Collection<? extends SimplePlRule> getSupport() {
        return this.support;
    }

    /**
     * Returns the claim of this argument, which is a propositional logic formula.
     *
     * @return The propositional logic formula representing the claim.
     */
    public PlFormula getClaim() {
        return this.claim;
    }

    /**
     * Returns a string representation
     *
     * @return The string representation of this argument.
     */
    @Override
    public String toString() {
        return "<" + this.support.toString() + "," + this.claim.toString() + ">";
    }

    /**
     * Computes the hash code for this argument based on the support.
     *
     * @return The hash code of this argument.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((support == null) ? 0 : support.hashCode());
        return result;
    }

    /**
     * Checks for equality between this argument and another object.
     * Two arguments are considered equal if their claims are equal and their
     * support sets are equal.
     *
     * @param obj The object to compare this argument to.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimplePlLogicArgument other = (SimplePlLogicArgument) obj;
        if (!this.claim.equals(other.claim))
            return false;
        if (support == null) {
            if (other.support != null)
                return false;
        } else if (!support.equals(other.support))
            return false;
        return true;
    }
}
