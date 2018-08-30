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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.lp.asp.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.commons.util.rules.RuleSet;
import net.sf.tweety.logics.commons.syntax.interfaces.LogicProgram;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * This class models an ASP-Core-2 program, meaning a set of rules and
 * optionally a query. A program is ground if it contains no variables. The
 * rules are ordered alphabetically.
 * 
 * @author Tim Janus
 * @author Thomas Vengels
 * @author Anna Gessler
 */
public class Program extends RuleSet<ASPRule> implements LogicProgram<ASPHead, ASPBodyElement, ASPRule> {

	private static final long serialVersionUID = -1498770939009078101L;

	/**
	 * The rules of the programs, including facts (rules with no body) and weak
	 * constraint (rules with no head).
	 */
	private Set<ASPRule> rules;

	/**
	 * A single query.
	 */
	private ASPLiteral query;

	/**
	 * Creates a new empty program.
	 */
	public Program() {
		this.rules = new HashSet<ASPRule>();
		this.query = null;
	}

	/**
	 * Creates a new program with the given rules.
	 * 
	 * @param query
	 * @param rules
	 */
	public Program(Collection<ASPRule> rules) {
		this.rules = (Set<ASPRule>) rules;
	}

	/**
	 * Creates a new program with the given query and rules.
	 * 
	 * @param query
	 * @param rules
	 */
	public Program(ASPLiteral query, Set<ASPRule> rules) {
		this.rules = rules;
		this.query = query;
	}

	/**
	 * Copy-Constructor
	 * 
	 * @param other
	 */
	public Program(Program other) {
		this(other.query, other.rules);
	}

	/**
	 * Sets the query of the program.
	 * 
	 * @param query
	 *            a literal
	 */
	public void setQuery(ASPLiteral query) {
		this.query = query;
	}

	@Override
	public void addFact(ASPHead fact) {
		this.rules.add(new ASPRule(fact));
	}

	@Override
	public FolSignature getSignature() {
		FolSignature sig = new FolSignature();
		for (ASPRule r : rules)
			sig.add(r);
		if (this.query != null)
			sig.add(this.query);
		return sig;
	}

	@Override
	public LogicProgram<?, ?, ?> substitute(Term<?> v, Term<?> t) throws IllegalArgumentException {
		// TODO
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public LogicProgram<?, ?, ?> substitute(Map<? extends Term<?>, ? extends Term<?>> map)
			throws IllegalArgumentException {
		// TODO
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public LogicProgram<?, ?, ?> exchange(Term<?> v, Term<?> t) throws IllegalArgumentException {
		// TODO
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public String toString() {
		String r = "";
		for (ASPRule a : this.rules) {
			r += a.toString() + "\n"; 
		}
		if (this.hasQuery())
			r += "\nQuery: " + query.toString();
		return r;
	}

	public boolean isGround() {
		for (ASPRule r : this.rules)
			if (!r.isGround())
				return false;
		if (this.hasQuery() && !query.isGround())
			return false;
		return true;
	}

	@Override
	public Program clone() {
		return new Program(this);
	}

	/**
	 * Returns true if the program contains a query.
	 * 
	 * @return true if the program has a query, false otherwise
	 */
	public boolean hasQuery() {
		return (this.query != null);
	}

	public ASPLiteral getQuery() {
		return query;
	}

	public Set<ASPRule> getRules() {
		return rules;
	}

	/**
	 * Returns the reduct of this program wrt. the given state, i.e. a program that
	 * contains no default negation and only those rules of this program (without
	 * all default-negated literals in the body) that do not have a default-negated
	 * version of a literal in their body.
	 * 
	 * @param state
	 *            some set of literals
	 * @return the reduct of this program
	 */
	public Program reduct(Set<ASPLiteral> state) {
		Program p = new Program();
		for (ASPRule r : this) {
			ASPRule r2 = new ASPRule();
			r2.setConclusion(r.getConclusion());
			boolean vio = false;
			for (ASPBodyElement e : r.getPremise()) {
				if (e instanceof DefaultNegation) {
					if (state.contains(((DefaultNegation) e).getLiteral())) {
						vio = true;
						break;
					}
				} else
					r2.addPremise(e);
			}
			if (vio)
				continue;
			p.add(r2);
		}
		return p;
	}

	/**
	 * Creates the defaultification p_d of a given program p. A defaultificated
	 * program p' of p adds for every Rule r in p a modified rule r_d of the form:
	 * 'H(r) :- B(r), not -H(r).' to the program p'.
	 * 
	 * @param p
	 *            The program which is not defaultificated yet
	 * @return a program p' which is the defaultificated version of p.
	 */
	public static Program defaultification(Program p) {
		Program reval = new Program();
		for (ASPRule origRule : p) {
			ASPRule defRule = new ASPRule();
			if (!origRule.isConstraint()) {
				ASPLiteral head = origRule.getHead().iterator().next();
				StrictNegation neg = new StrictNegation(head.getAtom());
				defRule.addPremises(origRule.getBody());
				DefaultNegation defaultificationLit = null;
				if (head instanceof StrictNegation) {
					defRule.addToHead(neg);
					defaultificationLit = new DefaultNegation(head.getAtom());
				} else {
					defRule.addToHead(head);
					defaultificationLit = new DefaultNegation(neg);
				}

				if (defaultificationLit != null && !defRule.getBody().contains(defaultificationLit)) {
					defRule.addPremise(defaultificationLit);
				}
			} else {
				defRule.addPremises(origRule.getBody());
			}
			reval.add(defRule);
		}
		return reval;
	}

	public void addFact(ASPLiteral fact) {
		this.add(new ASPRule(fact));
	}

	/**
	 * Checks if the program is an extended program, meaning the heads of the
	 * literals do not have more than one literal.
	 * 
	 * @return True if the program is an extended program, false otherwise.
	 */
	public boolean isExtendedProgram() {
		for (ASPRule r : this.rules) {
			if (r.getHead().size() > 1)
				return false;
		}
		return true;
	}

	/**
	 * Adds another program's content to the content of this program.
	 * @param other	Reference to the other program.
	 */
	public void add(Program other) {
		for (ASPRule r : other.rules)
			this.add(r);
		
		if (other.hasQuery())
			if (this.hasQuery())
				throw new IllegalArgumentException("Program already has a query.");
			else
				this.query = other.getQuery();
	}
	
	public void addAll(List<ASPRule> rules) {
		this.rules.addAll(rules);
	}

}
