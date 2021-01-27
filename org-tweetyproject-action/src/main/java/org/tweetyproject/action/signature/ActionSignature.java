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
 * This class represents an action signature consisting of a set of fluent names
 * (representing environment states) and a set of action names 
 * (representing actions). They are represented by first order predicates to
 * allow for the simple implementation of a grounding mechanism.
 * 
 * @author Sebastian Homann
 */
public class ActionSignature extends FolSignature {
	/**
	 * Creates an empty signature
	 */
	public ActionSignature() {
		super();
	}

	/**
	 * Creates a signature with the given objects (should be sorts, constants,
	 * predicates or formulas).
	 * 
	 * @param c a collection of items to be added.
	 * @throws IllegalArgumentException if at least one of the given objects is
	 *                                  neither a constant, a sort, a predicate or a
	 *                                  formula.
	 */
	public ActionSignature(Collection<?> c) throws IllegalArgumentException {
		super(c);
	}

	/**
	 * Creates a new Action Signature for a single first order formula.
	 * 
	 * @param f a FOL formula
	 */
	public ActionSignature(FolFormula f) {
		super();
		if (f != null)
			add(f);
	}

	/**
	 * @return the set of action names contained in this action signature.
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
	 * @return the set of fluent names contained in this action signature.
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
	 * Checks if a given formula is valid in the sense of an action description,
	 * containing only predicates that are either fluentNames or actionNames and
	 * containing neither quantifiers nor functions.
	 * 
	 * @param f the formula in question.
	 * @return true iff the given formula is a FolFormula, contains only atoms with
	 *         fluent or action-predicates and contains no quantifiers or functors.
	 */
	public boolean isValidFormula(Formula f) {
		if (!(f instanceof FolFormula))
			return false;
		FolFormula fol = (FolFormula) f;
		for (Predicate a : fol.getPredicates()) {
			if ((!(a instanceof FolActionName)) && (!(a instanceof FolFluentName)))
				return false;
		}
		return !fol.containsQuantifier() && fol.getFunctors().isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
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

	/**
	 * Returns true iff the given actionName is contained in this action signature.
	 * 
	 * @param actionName an action name
	 * @return true iff actionName is contained in this signature.
	 */
	public boolean containsActionName(String actionName) {
		return getActionName(actionName) != null;
	}

	/**
	 * Returns true iff the given fluentName is contained in this action signature.
	 * 
	 * @param fluentName a fluent name
	 * @return true iff fluentName is contained in this action signature.
	 */
	public boolean containsFluentName(String fluentName) {
		return getFluentName(fluentName) != null;
	}

	/**
	 * Returns the action name predicate with the given name if one exists, null
	 * otherwise.
	 * 
	 * @param action an action
	 * @return the actionName predicate with the given name or null
	 */
	public FolActionName getActionName(String action) {
		for (Predicate p : this.getPredicates()) {
			if (p instanceof FolActionName)
				if (((FolActionName) p).getName().equals(action))
					return (FolActionName) p;
		}
		return null;
	}

	/**
	 * Returns the fluent name predicate with the given name if one exists, null
	 * otherwise.
	 * 
	 * @param fluentName a string
	 * @return the fluent predicate with the given name or null
	 */
	public FolFluentName getFluentName(String fluentName) {
		for (Predicate p : this.getPredicates()) {
			if (p instanceof FolFluentName)
				if (((FolFluentName) p).getName().equals(fluentName))
					return (FolFluentName) p;
		}
		return null;
	}

	/**
	 * Returns true iff the given actionName is contained in this signature.
	 * 
	 * @param actionName a FOL action name
	 * @return true iff the given actionName is contained in this signature.
	 */
	public boolean containsActionName(FolActionName actionName) {
		return this.getPredicates().contains(actionName);
	}

	/**
	 * Returns true iff the given fluentName is contained in this signature.
	 * 
	 * @param fluentName a string
	 * @return true iff the given fluentName is contained in this signature.
	 */
	public boolean containsFluentName(FolFluentName fluentName) {
		return this.getPredicates().contains(fluentName);
	}

	/**
	 * Returns true iff the given predicate is contained in this signature.
	 * 
	 * @param predicate some predicate
	 * @return true iff the given predicate is contained in this signature.
	 */
	public boolean containsPredicate(Predicate predicate) {
		return this.getPredicates().contains(predicate);
	}

	/**
	 * Returns the set of all possible grounded atoms in this signature on the basis
	 * of all fluent predicates contained.
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
				Variable v = new Variable("X"+(i++), s);
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
	 * Returns the set of all possible grounded atoms in this signature on the basis
	 * of all action name predicates contained.
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
				Variable v = new Variable("Y"+(i++), s);
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

	@Override
	public void add(Object obj) throws IllegalArgumentException {
		if (obj instanceof ActionSignature) {
			this.addSignature(((ActionSignature)obj));
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
		if(obj instanceof RelationalFormula){
			this.addAll(((RelationalFormula)obj).getTerms(Constant.class));
			this.addAll(((RelationalFormula)obj).getPredicates());
			return;
		}
		throw new IllegalArgumentException("Class " + obj.getClass() + " of parameter " + obj + " is unsupported.");
	}
}
