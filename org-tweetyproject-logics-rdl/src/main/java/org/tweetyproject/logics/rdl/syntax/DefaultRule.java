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
package org.tweetyproject.logics.rdl.syntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.commons.syntax.Functor;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.commons.syntax.interfaces.Conjunctable;
import org.tweetyproject.logics.commons.syntax.interfaces.Disjunctable;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.reasoner.FolReasoner;
import org.tweetyproject.logics.fol.syntax.Conjunction;
import org.tweetyproject.logics.fol.syntax.Disjunction;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.math.probability.Probability;

/**
 * Models a default rule in Reiter's default logic, see [R. Reiter. A logic for
 * default reasoning. Artificial Intelligence, 13:81–132, 1980].
 *
 * @author Matthias Thimm, Nils Geilen
 *
 */
public class DefaultRule extends RelationalFormula {

	/** The prerequisite of the default rule */
	private FolFormula pre;
	/** The justifications of the default rule */
	private Collection<FolFormula> jus;
	/** The conclusion of the default rule */
	private FolFormula conc;

	/**
	 * an empty Default Rule
	 */
	public DefaultRule(){

	}

	/**
	 * Creates a new DefaultRule
	 * @param pre 	the prerequsite
	 * @param jus	the justification
	 * @param conc	the conclusion
	 * @throws IllegalArgumentException if there is some issue with the arguments
	 * @throws ParserException	when a parameter is missing
	 */
	public DefaultRule(FolFormula pre, FolFormula jus, FolFormula conc) throws IllegalArgumentException {
		this(pre, Arrays.asList(jus), conc);
	}

	/**
	 * Creates a new DefaultRule
	 * @param pre 	the prerequsite
	 * @param jus	the justifications
	 * @param conc	the conclusion
	 * @throws IllegalArgumentException if there is some issue with the arguments
	 */
	public DefaultRule(FolFormula pre, Collection<FolFormula> jus, FolFormula conc) throws IllegalArgumentException {
		super();
		if (pre == null)
			throw new IllegalArgumentException("Prerequisite needed to form default rule.");
		if (conc == null)
			throw new IllegalArgumentException("Conclusion needed to form default rule.");
		if (jus == null)
			throw new IllegalArgumentException("Justification needed to form default rule.");
		this.pre = pre;
		this.jus = new LinkedList<>();
		this.jus.addAll(jus);
		this.conc = conc;
	}

	/**
	 * Tests, whether the default is normal
	 * normal ^= a:c/c
	 * @param dt a default theory
	 * @return true iff the theory is normal
	 */
	public boolean isNormal(DefaultTheory dt) {
		if(jus.size()!=1)
			return false;
		FolReasoner prover = FolReasoner.getDefaultReasoner();
		return prover.equivalent(dt.getFacts(), jus.iterator().next(), conc);
	}


	/**
	 * Return the default's prerequisite
	 * @return the default's prerequisite
	 */
	public FolFormula getPrerequisite() {
		return pre;
	}

	/**
	 * Return the default's justification
	 * @return the default's justification
	 */
	public Collection<FolFormula> getJustification() {
		return jus;
	}

	/**
	 * Return the default's conclusion
	 * @return the default's conclusion
	 */
	public FolFormula getConclusion() {
		return conc;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.syntax.interfaces.SimpleLogicalFormula#getPredicates()
	 */
	@Override
	@SuppressWarnings("all")
	public Set<? extends Predicate> getPredicates() {
		Set result = pre.getPredicates();
		result.addAll(conc.getPredicates());
		for(FolFormula f: jus)
			result.add(f.getPredicates());
		return result;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.syntax.interfaces.SimpleLogicalFormula#isLiteral()
	 */
	@Override
	public boolean isLiteral() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.syntax.interfaces.QuantifiedFormula#getQuantifierVariables()
	 */
	@Override
	public Set<Variable> getQuantifierVariables() {
		Set<Variable> vars = conc.getQuantifierVariables();
		vars.addAll(pre.getQuantifierVariables());
		for (FolFormula f : jus)
			vars.addAll(f.getQuantifierVariables());
		return vars;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.syntax.interfaces.QuantifiedFormula#getUnboundVariables()
	 */
	@Override
	public Set<Variable> getUnboundVariables() {
		Set<Variable> unbound = conc.getUnboundVariables();
		unbound.addAll(pre.getUnboundVariables());
		for (FolFormula f : jus)
			unbound.addAll(f.getUnboundVariables());
		return unbound;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.syntax.interfaces.QuantifiedFormula#containsQuantifier()
	 */
	@Override
	public boolean containsQuantifier() {
		boolean result = conc.containsQuantifier() || pre.containsQuantifier();
		for (FolFormula f : jus)
			result |= f.containsQuantifier();
		return result;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.syntax.interfaces.QuantifiedFormula#isWellBound()
	 */
	@Override
	public boolean isWellBound() {
		if (!conc.isWellBound())
			return false;
		if (!pre.isWellBound())
			return false;
		for (FolFormula f : jus)
			if (!f.isWellBound())
				return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.syntax.interfaces.QuantifiedFormula#isWellBound(java.util.Set)
	 */
	@Override
	public boolean isWellBound(Set<Variable> boundVariables) {
		if (!conc.isWellBound(boundVariables))
			return false;
		if (!pre.isWellBound(boundVariables))
			return false;
		for (FolFormula f : jus)
			if (!f.isWellBound(boundVariables))
				return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.syntax.interfaces.QuantifiedFormula#isClosed()
	 */
	@Override
	public boolean isClosed() {
		if (!conc.isClosed())
			return false;
		if (!pre.isClosed())
			return false;
		for (FolFormula f : jus)
			if (!f.isClosed())
				return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.syntax.interfaces.QuantifiedFormula#isClosed(java.util.Set)
	 */
	@Override
	public boolean isClosed(Set<Variable> boundVariables) {
		if (!conc.isClosed(boundVariables))
			return false;
		if (!pre.isClosed(boundVariables))
			return false;
		for (FolFormula f : jus)
			if (!f.isClosed(boundVariables))
				return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.syntax.interfaces.LogicStructure#getTerms()
	 */
	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> result=pre.getTerms();
		result.addAll(conc.getTerms());
		for(FolFormula f: jus)
			result.addAll(f.getTerms());
		return result;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.syntax.interfaces.LogicStructure#getTerms(java.lang.Class)
	 */
	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		Set<C> result=pre.getTerms(cls);
		result.addAll(conc.getTerms(cls));
		for(FolFormula f: jus)
			result.addAll(f.getTerms(cls));
		return result;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.fol.syntax.RelationalFormula#getAtoms()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<FolAtom> getAtoms() {
		Set<FolAtom> atoms = (Set<FolAtom>) conc.getAtoms();
		atoms.addAll((Collection<? extends FolAtom>) pre.getAtoms());
		for (FolFormula f : jus)
			atoms.addAll((Collection<? extends FolAtom>) f.getAtoms());
		return atoms;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.fol.syntax.RelationalFormula#getFunctors()
	 */
	@Override
	public Set<Functor> getFunctors() {
		Set<Functor> funs = conc.getFunctors();
		funs.addAll(pre.getFunctors());
		for (FolFormula f : jus)
			funs.addAll(f.getFunctors());
		return funs;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.fol.syntax.RelationalFormula#substitute(org.tweetyproject.logics.commons.syntax.interfaces.Term, org.tweetyproject.logics.commons.syntax.interfaces.Term)
	 */
	@Override
	public RelationalFormula substitute(Term<?> v, Term<?> t) throws IllegalArgumentException {
		List<FolFormula> fs = new ArrayList<>();
		for (FolFormula f : jus)
			fs.add(f.substitute(v, t));
		;
		return new DefaultRule((FolFormula)pre.substitute(v, t), fs, (FolFormula)conc.substitute(v, t));
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.fol.syntax.RelationalFormula#getUniformProbability()
	 */
	@Override
	public Probability getUniformProbability() {
		throw new UnsupportedOperationException("Uniform probability of default is undefined.");
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.fol.syntax.RelationalFormula#complement()
	 */
	@Override
	public RelationalFormula complement() {
		throw new IllegalArgumentException("No complement of default");
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.fol.syntax.RelationalFormula#combineWithOr(org.tweetyproject.logics.commons.syntax.interfaces.Disjunctable)
	 */
	@Override
	public Disjunction combineWithOr(Disjunctable formula) {
		throw new IllegalArgumentException("Not combinable with or");
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.fol.syntax.RelationalFormula#combineWithAnd(org.tweetyproject.logics.commons.syntax.interfaces.Conjuctable)
	 */
	@Override
	public Conjunction combineWithAnd(Conjunctable formula) {
		throw new IllegalArgumentException("Not combinable with and");
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.fol.syntax.RelationalFormula#getSignature()
	 */
	@Override
	public FolSignature getSignature() {
		FolSignature result = pre.getSignature();
		result.addSignature(conc.getSignature());
		for(FolFormula f: jus)
			result.addSignature(f.getSignature());
		return result;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.fol.syntax.RelationalFormula#toString()
	 */
	@Override
	public String toString() {
		String result = pre + " :: ";
		Iterator<FolFormula> i = jus.iterator();
		if (i.hasNext())
			result += i.next();
		while (i.hasNext())
			result += " ; " + i.next();
		return result + " / " + conc;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.fol.syntax.RelationalFormula#clone()
	 */
	@Override
	public RelationalFormula clone() {
		try {
			return new DefaultRule(pre, jus, conc);
		} catch (Exception e) {
		}
		return null;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conc == null) ? 0 : conc.hashCode());
		result = prime * result + ((jus == null) ? 0 : jus.hashCode());
		result = prime * result + ((pre == null) ? 0 : pre.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(this==o)
			return true;
		if(o instanceof DefaultRule){
			DefaultRule d = (DefaultRule)o;
			for(FolFormula f: this.jus){
				boolean b= false;
				for(FolFormula g: d.jus)
					b|=f.equals(g);
				if(!b)
					return false;
			}
			return this.pre.equals(d.pre) && this.conc.equals(d.conc);
		}
		return false;
	}



}
