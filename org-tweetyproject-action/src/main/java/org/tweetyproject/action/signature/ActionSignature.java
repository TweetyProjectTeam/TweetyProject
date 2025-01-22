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
package org.tweetyproject.action.signature;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.action.description.syntax.CLaw;
import org.tweetyproject.action.grounding.GroundingTools;
import org.tweetyproject.action.query.syntax.SActionQuery;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.commons.syntax.Sort;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This class represents an action signature, which consists of a set of fluent names
 * (representing environment states) and a set of action names (representing actions).
 * These names are represented by first-order predicates to allow for a simple implementation
 * of a grounding mechanism.
 *
 *
 * <p><b>Authors:</b> Sebastian Homann</p>
 */
public class ActionSignature extends FolSignature {

    /**
     * Creates an empty action signature.
     */
    public ActionSignature() {
        super();
    }

    /**
     * Creates an action signature with the given objects, which should be sorts, constants,
     * predicates, or formulas.
     *
     * @param c a collection of items to be added to the signature.
     * @throws IllegalArgumentException if at least one of the given objects is
     *                                  neither a constant, a sort, a predicate, nor a
     *                                  formula.
     */
    public ActionSignature(Collection<?> c) throws IllegalArgumentException {
        super(c);
    }

    /**
     * Creates a new ActionSignature for a single first-order formula.
     *
     * @param f a FOL formula.
     */
    public ActionSignature(FolFormula f) {
        super();
        if (f != null)
            add(f);
    }

/**
 * Retrieves the set of action names contained in this action signature.

 *
 * @return a `Set` of `FolActionName` objects representing the action names in this signature.
 */
public Set<FolActionName> getActionNames() {
    Set<FolActionName> result = new HashSet<FolActionName>();
    for (Predicate a : this.getPredicates()) {
        if (a instanceof FolActionName) {
            result.add((FolActionName) a);
        }
    }
    return result;
}

/**
 * Retrieves the set of fluent names contained in this action signature.
 *
 *
 * @return a `Set` of `FolFluentName` objects representing the fluent names in this signature.
 */
public Set<FolFluentName> getFluentNames() {
    Set<FolFluentName> result = new HashSet<FolFluentName>();
    for (Predicate a : this.getPredicates()) {
        if (a instanceof FolFluentName) {
            result.add((FolFluentName) a);
        }
    }
    return result;
}

    /**
     * Checks if a given formula is valid in the sense of an action description.
     * A valid formula contains only predicates that are either fluent names or action names,
     * and contains neither quantifiers nor functions.
     *
     * @param f the formula to be checked.
     * @return true if the given formula is a valid action description, false otherwise.
     */
    public boolean isValidFormula(Formula f) {
        if (!(f instanceof FolFormula))
            return false;
        FolFormula fol = (FolFormula) f;
        for (Predicate a : fol.getPredicates()) {
            if (!(a instanceof FolActionName) && !(a instanceof FolFluentName))
                return false;
        }
        return !fol.containsQuantifier() && fol.getFunctors().isEmpty();
    }

    /**
     * Returns true if the given action name is contained in this action signature.
     *
     * @param actionName an action name.
     * @return true if the action name is contained in this signature, false otherwise.
     */
    public boolean containsActionName(String actionName) {
        return getActionName(actionName) != null;
    }

    /**
     * Returns true if the given fluent name is contained in this action signature.
     *
     * @param fluentName a fluent name.
     * @return true if the fluent name is contained in this action signature, false otherwise.
     */
    public boolean containsFluentName(String fluentName) {
        return getFluentName(fluentName) != null;
    }

    /**
     * Returns the action name predicate with the given name if one exists, null otherwise.
     *
     * @param action the name of the action.
     * @return the action name predicate with the given name, or null if it does not exist.
     */
    public FolActionName getActionName(String action) {
        for (Predicate p : this.getPredicates()) {
            if (p instanceof FolActionName && ((FolActionName) p).getName().equals(action))
                return (FolActionName) p;
        }
        return null;
    }

    /**
     * Returns the fluent name predicate with the given name if one exists, null otherwise.
     *
     * @param fluentName the name of the fluent.
     * @return the fluent name predicate with the given name, or null if it does not exist.
     */
    public FolFluentName getFluentName(String fluentName) {
        for (Predicate p : this.getPredicates()) {
            if (p instanceof FolFluentName && ((FolFluentName) p).getName().equals(fluentName))
                return (FolFluentName) p;
        }
        return null;
    }

    /**
     * Returns true if the given action name is contained in this signature.
     *
     * @param actionName a FOL action name.
     * @return true if the action name is contained in this signature, false otherwise.
     */
    public boolean containsActionName(FolActionName actionName) {
        return this.getPredicates().contains(actionName);
    }

    /**
     * Returns true if the given fluent name is contained in this signature.
     *
     * @param fluentName a FOL fluent name.
     * @return true if the fluent name is contained in this signature, false otherwise.
     */
    public boolean containsFluentName(FolFluentName fluentName) {
        return this.getPredicates().contains(fluentName);
    }

    /**
     * Returns true if the given predicate is contained in this signature.
     *
     * @param predicate some predicate.
     * @return true if the predicate is contained in this signature, false otherwise.
     */
    public boolean containsPredicate(Predicate predicate) {
        return this.getPredicates().contains(predicate);
    }

    /**
     * Returns the set of all possible grounded atoms in this signature based on all fluent predicates.
     *
     * @return the set of all possible grounded fluent atoms.
     */
    public Set<FolAtom> getAllGroundedFluentAtoms() {
        Set<FolAtom> atoms = new HashSet<FolAtom>();
        Set<Variable> variables = new HashSet<Variable>();
        for (FolFluentName f : getFluentNames()) {
            FolAtom a = new FolAtom(f);
            int i = 0;
            for (Sort s : f.getArgumentTypes()) {
                Variable v = new Variable("X" + (i++), s);
                a.addArgument(v);
                variables.add(v);
            }
            atoms.add(a);
        }
        Set<Map<Variable, Constant>> substitutions = GroundingTools.getAllSubstitutions(variables);
        Set<FolAtom> result = new HashSet<FolAtom>();
        for (Map<Variable, Constant> substitution : substitutions) {
            for (FolAtom a : atoms) {
                result.add((FolAtom) a.substitute(substitution));
            }
        }
        return result;
    }

    /**
     * Returns the set of all possible grounded atoms in this signature based on all action name predicates.
     *
     * @return the set of all possible grounded action atoms.
     */
    public Set<FolAtom> getAllGroundedActionNameAtoms() {
        Set<FolAtom> atoms = new HashSet<FolAtom>();
        Set<Variable> variables = new HashSet<Variable>();
        int i = 0;
        for (FolActionName f : getActionNames()) {
            FolAtom a = new FolAtom(f);
            for (Sort s : f.getArgumentTypes()) {
                Variable v = new Variable("Y" + (i++), s);
                a.addArgument(v);
                variables.add(v);
            }
            atoms.add(a);
        }
        Set<Map<Variable, Constant>> substitutions = GroundingTools.getAllSubstitutions(variables);
        Set<FolAtom> result = new HashSet<FolAtom>();
        for (Map<Variable, Constant> substitution : substitutions) {
            for (FolAtom a : atoms) {
                result.add((FolAtom) a.substitute(substitution));
            }
        }
        return result;
    }

    /**
     * Adds an object to the action signature.
     *
     * @param obj the object to be added.
     * @throws IllegalArgumentException if the object type is not supported.
     */
    @Override
    public void add(Object obj) throws IllegalArgumentException {
        if (obj instanceof ActionSignature) {
            this.addSignature((ActionSignature) obj);
            return;
        }
        if (obj instanceof FolFluentName) {
            FolFluentName f = (FolFluentName) obj;
            secondSet.add(f);
            return;
        }
        if (obj instanceof FolActionName) {
            FolActionName f = (FolActionName) obj;
            secondSet.add(f);
            return;
        }
        if (obj instanceof CLaw) {
            CLaw f = (CLaw) obj;
            this.addAll(f.getFormulas());
            return;
        }
        if (obj instanceof SActionQuery) {
            SActionQuery f = (SActionQuery) obj;
            this.add(f.getFormula());
            return;
        }
        if (obj instanceof Constant) {
            fourthSet.add(((Constant) obj).getSort());
            firstSet.add((Constant) obj);
            return;
        }
        if (obj instanceof Sort) {
            fourthSet.add((Sort) obj);
            return;
        }
        if (obj instanceof Predicate) {
            this.addAll(((Predicate) obj).getArgumentTypes());
            secondSet.add((Predicate) obj);
            return;
        }
        if (obj instanceof PlFormula) {
            this.addAll(((PlFormula) obj).getPredicates());
            return;
        }
        if (obj instanceof RelationalFormula) {
            this.addAll(((RelationalFormula) obj).getTerms(Constant.class));
            this.addAll(((RelationalFormula) obj).getPredicates());
            return;
        }
        throw new IllegalArgumentException("Class " + obj.getClass() + " of parameter " + obj + " is unsupported.");
    }

    /**
     * Provides a string representation of the action signature.
     *
     * @return a string representation of the action signature, including all sorts, action names, and fluent names.
     */
    @Override
    public String toString() {
        String result = ":- signature\n";
        for (Sort s : this.getSorts()) {
            result += s.getName() + " = " + s.getTerms(Constant.class).toString() + "\n";
        }
        for (FolActionName p : this.getActionNames()) {
            result += p.toString() + "\n";
        }
        for (FolFluentName f : this.getFluentNames()) {
            result += f.toString() + "\n";
        }
        return result;
    }
}


