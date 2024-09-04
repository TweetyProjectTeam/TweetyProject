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
package org.tweetyproject.lp.nlp.syntax;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.commons.util.rules.RuleSet;
import org.tweetyproject.logics.commons.syntax.interfaces.LogicProgram;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 * Represents a nested logic program (NLP) in the form of a set of rules.
 *
 * <p>
 * The `NLPProgram` class extends `RuleSet` with rules of type `NLPRule` and implements
 * the `LogicProgram` interface for first-order logic (FOL) formulas. It provides methods
 * to manage facts, retrieve the minimal signature, and perform substitutions and exchanges
 * on terms within the program.
 * </p>
 *
 * @author Tim Janus
 */
public class NLPProgram
    extends RuleSet<NLPRule>
    implements LogicProgram<FolFormula, FolFormula, NLPRule> {

    /** Serial version UID for this class. */
    private static final long serialVersionUID = 1050122194243070233L;

    /**
     * Retrieves a set of facts from the nested logic program.
     *
     * <p>
     * This method iterates through all the rules in the program and collects
     * those that are identified as facts. A fact is a rule with an empty premise,
     * and its conclusion is considered a fact in first-order logic.
     * </p>
     *
     * @return a `Set` of `FolFormula` objects representing the facts in the program.
     */
    public Set<FolFormula> getFacts() {
        Set<FolFormula> reval = new HashSet<FolFormula>();
        for (NLPRule rule : this) {
            if (rule.isFact()) {
                reval.add(rule.getConclusion());
            }
        }
        return reval;
    }

    /**
     * Adds a fact to the nested logic program.
     *
     * <p>
     * This method creates a new `NLPRule` with the given fact.
     * </p>
     *
     * @param fact a `FolFormula` representing the fact to be added to the program.
     */
    @Override
    public void addFact(FolFormula fact) {
        this.add(new NLPRule(fact));
    }

    /**
     * Adds multiple facts to the nested logic program.
     *
     * <p>
     * This method accepts multiple facts as an array of `FolFormula` objects and
     * adds each one to the program by calling the `addFact` method for each fact.
     * </p>
     *
     * @param facts an array of `FolFormula` objects representing the facts to be added.
     */
    @Override
    public void addFacts(FolFormula... facts) {
        for (FolFormula f : facts)
            this.addFact(f);
    }

    /**
     * Retrieves the minimal signature of the nested logic program.
     *
     * @return a `FolSignature` object representing the minimal signature of the program.
     */
    @Override
    public FolSignature getMinimalSignature() {
        FolSignature reval = new FolSignature();
        for (NLPRule rule : this) {
            reval.addSignature(rule.getSignature());
        }
        return reval;
    }

    /**
     * Substitutes a term in all rules of the nested logic program.
     *
     * <p>
     * This method creates a new `NLPProgram` in which all occurrences of a specific
     * term (`t`) in the program are replaced with another term (`v`). Each rule in
     * the program is processed, and a substituted version of the rule is added to the new program.
     * </p>
     *
     * @param t the term to be replaced.
     * @param v the term to replace with.
     * @return a new `NLPProgram` with the substitution applied.
     */
    @Override
    public NLPProgram substitute(Term<?> t, Term<?> v) {
        NLPProgram reval = new NLPProgram();
        for (NLPRule rule : this) {
            reval.add(rule.substitute(t, v));
        }
        return reval;
    }

    /**
     * Substitutes multiple terms in all rules of the nested logic program according to the provided map.
     *
     *
     * @param map a map where the keys are the terms to be replaced and the values are the replacement terms.
     * @return a new `NLPProgram` with the substitutions applied.
     * @throws IllegalArgumentException if an invalid substitution is encountered.
     */
    @Override
    public NLPProgram substitute(Map<? extends Term<?>, ? extends Term<?>> map)
            throws IllegalArgumentException {
        NLPProgram reval = this;
        for (Term<?> t : map.keySet()) {
            reval = reval.substitute(t, map.get(t));
        }
        return reval;
    }

    /**
     * Exchanges two terms in all rules of the nested logic program.
     *
     * <p>
     * This method creates a new `NLPProgram` in which two terms (`v` and `t`) are exchanged
     * in all rules of the program.
     * </p>
     *
     * @param v the first term involved in the exchange.
     * @param t the second term involved in the exchange.
     * @return a new `NLPProgram` with the terms exchanged.
     * @throws IllegalArgumentException if an invalid exchange is attempted.
     */
    @Override
    public NLPProgram exchange(Term<?> v, Term<?> t)
            throws IllegalArgumentException {
        NLPProgram reval = new NLPProgram();
        for (NLPRule rule : this) {
            reval.add((NLPRule) rule.exchange(t, v));
        }
        return reval;
    }
}
