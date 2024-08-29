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
 package org.tweetyproject.logics.commons.syntax;

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.logics.commons.syntax.interfaces.Atom;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;

/**
 * This class provides common functionalities for quantified formulas, i.e.
 * forall and exists quantified formulas.
 *
 *
 * @author Matthias Thimm
 * @author Anna Gessler
 *
 * @param <T>	The type of the formulas which are quantified.
 */

 public class QuantifiedFormulaSupport<T extends RelationalFormula> {

    /**
     * The inner formula that this quantified formula ranges over.
     */
    private T innerFormula;

    /**
     * The set of variables that are quantified within this formula.
     */
    private Set<Variable> quantifier_variables;

    /**
     * Constructs a new {@code QuantifiedFormulaSupport} instance with the specified formula and
     * quantifier variables.
     *
     * @param formula   The relational formula that this quantified formula ranges over.
     * @param variables The set of variables that are quantified within the formula.
     */
    public QuantifiedFormulaSupport(T formula, Set<Variable> variables) {
        this.innerFormula = formula;
        this.quantifier_variables = variables;
    }

    /**
     * Returns the formula that this quantified formula ranges over.
     *
     * @return The relational formula that this quantified formula ranges over.
     */
    public T getFormula() {
        return this.innerFormula;
    }

    /**
     * Returns the set of variables that are quantified within this formula.
     *
     * @return The set of quantified variables.
     */
    public Set<Variable> getQuantifierVariables() {
        return new HashSet<Variable>(this.quantifier_variables);
    }

    /**
     * Sets the formula that this quantified formula ranges over.
     *
     * @param formula The relational formula to set.
     */
    public void setFormula(T formula) {
        this.innerFormula = formula;
    }

    /**
     * Sets the set of variables that are quantified within this formula.
     *
     * @param variables The set of variables to be quantified.
     */
    public void setQuantifierVariables(Set<Variable> variables) {
        this.quantifier_variables = variables;
    }

    /**
     * Checks whether the formula is closed, meaning all variables are bound by a quantifier.
     *
     * @return {@code true} if the formula is closed, {@code false} otherwise.
     */
    public boolean isClosed() {
        return this.innerFormula.isClosed(this.quantifier_variables);
    }

    /**
     * Checks whether the formula is closed when considering an additional set of bound variables.
     *
     * @param boundVariables The set of additional variables considered to be bound.
     * @return {@code true} if the formula is closed with the additional variables, {@code false} otherwise.
     */
    public boolean isClosed(Set<Variable> boundVariables) {
        Set<Variable> variables = new HashSet<Variable>(this.quantifier_variables);
        variables.addAll(boundVariables);
        return this.innerFormula.isClosed(variables);
    }

    /**
     * Checks whether the formula contains a quantifier.
     *
     * @return {@code true} since this support class is specifically for quantified formulas.
     */
    public boolean containsQuantifier() {
        return true;
    }

    /**
     * Checks whether the formula is well-bound, meaning no variable is bound more than once.
     *
     * @return {@code true} if the formula is well-bound, {@code false} otherwise.
     */
    public boolean isWellBound() {
        return this.innerFormula.isWellBound(this.quantifier_variables);
    }

    /**
     * Checks whether the formula is well-bound when considering an additional set of bound variables.
     *
     * @param boundVariables The set of additional variables considered to be bound.
     * @return {@code true} if the formula is well-bound with the additional variables, {@code false} otherwise.
     */
    public boolean isWellBound(Set<Variable> boundVariables) {
        Set<Variable> intersection = new HashSet<Variable>(this.quantifier_variables);
        intersection.retainAll(boundVariables);
        if (!intersection.isEmpty()) return false;
        Set<Variable> variables = new HashSet<Variable>(this.quantifier_variables);
        variables.addAll(boundVariables);
        return this.innerFormula.isWellBound(variables);
    }

    /**
     * Returns the set of predicates contained in the inner formula.
     *
     * @return A set of predicates used in the inner formula.
     */
    public Set<? extends Predicate> getPredicates() {
        return this.innerFormula.getPredicates();
    }

    /**
     * Returns the set of functors contained in the inner formula.
     *
     * @return A set of functors used in the inner formula.
     */
    public Set<Functor> getFunctors() {
        return this.innerFormula.getFunctors();
    }

    /**
     * Returns the set of atoms contained in the inner formula.
     *
     * @return A set of atoms used in the inner formula.
     */
    public Set<? extends Atom> getAtoms() {
        return innerFormula.getAtoms();
    }

    /**
     * Returns the set of unbound variables in the inner formula, excluding the quantified variables.
     *
     * @return A set of unbound variables in the inner formula.
     */
    public Set<Variable> getUnboundVariables() {
        Set<Variable> variables = innerFormula.getUnboundVariables();
        variables.removeAll(this.quantifier_variables);
        return variables;
    }

    /**
     * Checks whether the formula is in Disjunctive Normal Form (DNF).
     *
     * @return {@code false}, as this class does not specifically check for DNF.
     */
    public boolean isDnf() {
        return false;
    }

    /**
     * Checks whether the formula is a literal.
     *
     * @return {@code false}, as this class is intended for quantified formulas, not literals.
     */
    public boolean isLiteral() {
        return false;
    }

    /**
     * Returns the set of terms used in the inner formula.
     *
     * @return A set of terms used in the inner formula.
     */
    public Set<Term<?>> getTerms() {
        return innerFormula.getTerms();
    }

    /**
     * Returns the set of terms of a specified type used in the inner formula.
     *
     * @param <C> The type of terms to return.
     * @param cls The class object representing the type of terms to return.
     * @return A set of terms of the specified type used in the inner formula.
     */
    public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
        return innerFormula.getTerms(cls);
    }

    /**
     * Computes the hash code for this {@code QuantifiedFormulaSupport} object.
     *
     * @return The hash code value for this object.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((innerFormula == null) ? 0 : innerFormula.hashCode());
        result = prime * result
                + ((quantifier_variables == null) ? 0 : quantifier_variables.hashCode());
        return result;
    }


}
