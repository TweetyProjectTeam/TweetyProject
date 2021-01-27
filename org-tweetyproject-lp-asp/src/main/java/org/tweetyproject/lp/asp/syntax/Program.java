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
package org.tweetyproject.lp.asp.syntax;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.tweetyproject.commons.util.rules.RuleSet;
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.interfaces.LogicProgram;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 * This class models an ASP-Core-2 program, meaning a set of rules and
 * optionally a query. A program is ground if it contains no variables. The
 * rules are ordered alphabetically.
 * 
 * @author Tim Janus
 * @author Thomas Vengels
 * @author Anna Gessler
 */
public class Program extends RuleSet<ASPRule> implements LogicProgram<ClassicalHead, ASPBodyElement, ASPRule> {

	private static final long serialVersionUID = -1498770939009078101L;

	/**
	 * A single query (optional field).
	 */
	private ASPLiteral query;

	/**
	 * Optional field that can be used by some solvers. If used, all atoms will be
	 * hidden from the output of the solver except for those with whitelisted
	 * predicates. This corresponds to the '#show' statement of the clingo input
	 * language.
	 */
	private Set<Predicate> output_predicate_whitelist;
	
	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	/**
	 * Creates a new empty program.
	 */
	public Program() {
		super();
		this.query = null;
		this.output_predicate_whitelist = new HashSet<Predicate>();
	}

	/**
	 * Creates a new program with the given rules.
	 * 
	 * @param rules a set of rules
	 */
	public Program(Collection<ASPRule> rules) {
		super(rules);
		this.query = null;
		this.output_predicate_whitelist = this.getPredicates();
	}

	/**
	 * Creates a new program with the given rules.
	 * 
	 * @param rules a set of rules
	 */
	public Program(ASPRule... rules) {
		super();
		this.query = null;
		this.output_predicate_whitelist = this.getPredicates();
		this.addAll(Arrays.asList(rules));
	}

	/**
	 * Creates a new program with the given query and rules.
	 * 
	 * @param query a query
	 * @param rules a set of rules
	 */
	public Program(ASPLiteral query, Set<ASPRule> rules) {
		super(rules);
		this.query = query;
		this.output_predicate_whitelist = this.getPredicates();
	}

	/**
	 * Copy-Constructor
	 * 
	 * @param other another program
	 */
	public Program(Program other) {
		this(other.query, other);
	}
	
	// -------------------------------------------------------------------------
	// GETTERS AND SETTERS
	// -------------------------------------------------------------------------

	/**
	 * Sets the query of the program.
	 * 
	 * @param query a literal
	 */
	public void setQuery(ASPLiteral query) {
		this.query = query;
	}

	@Override
	public void addFact(ClassicalHead fact) {
		this.add(new ASPRule(fact));
	}

	@Override
	public void addFacts(ClassicalHead... fact) {
		for (ClassicalHead a : fact)
			addFact(a);
	}
	
	/**
	 * Sets the whitelist of predicates. Solvers that use this option will only show
	 * atoms over predicates in this set in their output.
	 * 
	 * @param ps set of predicates
	 */
	public void setOutputWhitelist(Collection<Predicate> ps) {
		this.output_predicate_whitelist = (Set<Predicate>) ps;
	}

	/**
	 * Returns the whitelist of predicates. Solvers that use this option will only
	 * show atoms over predicates in this set in their output.
	 * 
	 * @return set of whitelisted predicates
	 */
	public Set<Predicate> getOutputWhitelist() {
		return this.output_predicate_whitelist;
	}
	
	/**
	 * Returns true if the program contains a query.
	 * 
	 * @return true if the program has a query, false otherwise
	 */
	public boolean hasQuery() {
		return (this.query != null);
	}

	/**
	 * Returns the query of the program, if there is one.
	 * 
	 * @return a literal, or null if the program has no query
	 */
	public ASPLiteral getQuery() {
		return query;
	}
	
	/**
	 * Adds a fact to this program.
	 * @param fact a literal
	 */
	public void addFact(ASPLiteral fact) {
		this.add(new ASPRule(fact));
	}

	/**
	 * Add the given facts to this program.
	 * @param facts 
	 */
	void addFacts(ASPLiteral... facts) {
		for (ASPLiteral l : facts)
			this.addFact(l);
	}

	/**
	 * Adds another program's content to the content of this program.
	 * 
	 * @param other Reference to the other program.
	 */
	public void add(Program other) {
		for (ASPRule r : other)
			this.add(r);
		if (other.hasQuery())
			if (this.hasQuery())
				throw new IllegalArgumentException("Failed to add other program's query because this program already has a query.");
			else
				this.query = other.getQuery();
	}
	
	// -------------------------------------------------------------------------
	// INTERFACE METHODS AND UTILS
	// -------------------------------------------------------------------------
	
	/**
	 * Checks if the program is an extended program, meaning the heads of the
	 * literals do not have more than one literal.
	 * 
	 * @return True if the program is an extended program, false otherwise.
	 */
	public boolean isExtendedProgram() {
		for (ASPRule r : this) {
			if (r.getHead().getLiterals().size() > 1)
				return false;
		}
		return true;
	}

	/**
	 * Returns true if the program is safe, i.e. if all of its rules and its query
	 * are safe according to the definition of safety given in the ASP-Core-2 standard.
	 * 
	 * @return true if the program is safe, false otherwise
	 */
	public boolean isSafe() {
		for (ASPRule r : this)
			if (!r.isSafe())
				return false;
		return true;
	}
	
	/**
	 * Checks if the program is ground, meaning if it contains no rules
	 * with variables. 
	 * @return true if the program is ground, false otherwise
	 */
	public boolean isGround() {
		for (ASPRule r : this)
			if (!r.isGround())
				return false;
		if (this.hasQuery() && !query.isGround())
			return false;
		return true;
	}

	@Override
	public FolSignature getMinimalSignature() {
		FolSignature sig = new FolSignature();
		for (ASPRule r : this) {
			sig.addAll(r.getPredicates());
			sig.addAll(r.getTerms(Constant.class));
		}
		if (this.query != null) {
			sig.addAll(this.query.getPredicates());
			sig.addAll(this.query.getTerms(Constant.class));
		}
		return sig;
	}

	@Override
	public Program substitute(Term<?> v, Term<?> t) throws IllegalArgumentException {
		Program reval = new Program();
		for (ASPRule r : this)
			reval.add(r.substitute(t, v));
		if (this.hasQuery())
			reval.setQuery((ASPLiteral) this.query.substitute(t, v));
		return reval;
	}

	@Override
	public Program substitute(Map<? extends Term<?>, ? extends Term<?>> map) throws IllegalArgumentException {
		Program reval = this;
		for (Term<?> t : map.keySet()) {
			reval = reval.substitute(t, map.get(t));
		}
		return reval;
	}

	@Override
	public Program exchange(Term<?> v, Term<?> t) throws IllegalArgumentException {
		Program reval = new Program();
		for (ASPRule r : this)
			reval.add(r.exchange(v, t));
		if (this.hasQuery())
			reval.setQuery((ASPLiteral) this.query.exchange(t, v));
		return reval;
	}

	@Override
	public Program clone() {
		return new Program(this);
	}

	/**
	 * Processes the set of all predicates which appear in this program.
	 * 
	 * @return set of predicates
	 */
	private Set<Predicate> getPredicates() {
		Set<Predicate> prs = new HashSet<Predicate>();
		for (ASPRule r : this)
			prs.addAll(r.getPredicates());
		if (this.hasQuery())
			prs.add(query.getPredicate());
		return prs;
	}

	/**
	 * Returns the reduct of this program wrt. the given state, i.e. a program that
	 * contains no default negation and only those rules of this program (without
	 * all default-negated literals in the body) that do not have a default-negated
	 * version of a literal in their body.
	 * 
	 * @param state some set of literals
	 * @return the reduct of this program
	 */
	public Program getReduct(Set<ASPLiteral> state) {
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
	 * @param p The program which is not defaultificated yet
	 * @return a program p' which is the defaultificated version of p.
	 */
	public static Program getDefaultification(Program p) {
		Program reval = new Program();
		for (ASPRule origRule : p) {
			ASPRule defRule = new ASPRule();
			if (!origRule.isConstraint()) {
				if (!(origRule.getHead() instanceof ClassicalHead))
					throw new UnsupportedOperationException(
							"This function is currently only supported for classical heads (disjunctions of literals)");
				ASPLiteral head = ((ClassicalHead) origRule.getHead()).iterator().next();
				StrictNegation neg = new StrictNegation(head.getAtom());
				defRule.addPremises(origRule.getPremise());
				DefaultNegation defaultificationLit = null;
				if (head instanceof StrictNegation) {
					defRule.addToHead(neg);
					defaultificationLit = new DefaultNegation(head.getAtom());
				} else {
					defRule.addToHead(head);
					defaultificationLit = new DefaultNegation(neg);
				}

				if (defaultificationLit != null && !defRule.getPremise().contains(defaultificationLit)) {
					defRule.addPremise(defaultificationLit);
				}
			} else {
				defRule.addPremises(origRule.getPremise());
			}
			reval.add(defRule);
		}
		return reval;
	}

	@Override
	public String toString() {
		String r = "{";
		for (ASPRule a : this)
			r += a.toString() + " ";
		r = r.substring(0, r.length() - 1);
		if (this.hasQuery())
			r += " " + query.toString() + "?";
		r += "}";
		return r;
	}


}
