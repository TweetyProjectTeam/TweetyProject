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
package net.sf.tweety.arg.aba.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * This class models an inference rule from an ABA theory.
 *
 * @param <T> is the type of the language that the ABA theory's rules range over
 * @author Nils Geilen (geilenn@uni-koblenz.de)
 */
public class InferenceRule<T extends Formula> extends AbaRule<T> {

	/**
	 * The rule's conclusion
	 */
	T head;
	/**
	 * The rule's prerequisites
	 */
	Collection<T> body = new HashSet<T>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.commons.util.rules.Rule#isFact()
	 */
	@Override
	public boolean isFact() {
		return body.size() == 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.commons.util.rules.Rule#isConstraint()
	 */
	@Override
	public boolean isConstraint() {
		throw new RuntimeException("arg.aba.InferenceRule.isConstraint not implemented");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.commons.util.rules.Rule#setConclusion(net.sf.tweety.commons.
	 * Formula)
	 */
	@Override
	public void setConclusion(T conclusion) {
		head = conclusion;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.commons.util.rules.Rule#addPremise(net.sf.tweety.commons.
	 * Formula)
	 */
	@Override
	public void addPremise(T premise) {
		body.add(premise);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.commons.util.rules.Rule#addPremises(java.util.Collection)
	 */
	@Override
	public void addPremises(Collection<? extends T> premises) {
		body.addAll(premises);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.commons.util.rules.Rule#getSignature()
	 */
	@Override
	public Signature getSignature() {
		Signature sig = head.getSignature();
		for (T t : body)
			sig.addSignature(t.getSignature());
		return sig;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.commons.util.rules.Rule#getPremise()
	 */
	@Override
	public Collection<? extends T> getPremise() {
		return body;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.commons.util.rules.Rule#getConclusion()
	 */
	@Override
	public T getConclusion() {
		return head;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.aba.syntax.ABARule#isAssumption()
	 */
	@Override
	public boolean isAssumption() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = "(" + head + " <- ";
		if (body.isEmpty())
			return result + "true)";
		Iterator<T> i = body.iterator();
		result += i.next();
		while (i.hasNext())
			result += ", " + i.next();
		return result + ")";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((head == null) ? 0 : head.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InferenceRule other = (InferenceRule) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (head == null) {
			if (other.head != null)
				return false;
		} else if (!head.equals(other.head))
			return false;
		return true;
	}

	@Override
	public Set<? extends Predicate> getPredicates() {
		Set<Predicate> predicates = new HashSet<Predicate>();
		Signature sig = this.getSignature();
		if (sig instanceof FolSignature) {
			predicates.addAll(((RelationalFormula) head).getPredicates());
			for (T t : body) {
				predicates.addAll(((RelationalFormula) t).getPredicates());
			}
		}
		return predicates;
	}

	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> reval = new HashSet<Term<?>>();
		Signature sig = this.getSignature();
		if (sig instanceof FolSignature) {
			reval.addAll(((RelationalFormula) head).getTerms());
			for (T t : body) {
				reval.addAll(((RelationalFormula) t).getTerms());
			}
		}
		return reval;
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		Set<C> reval = new HashSet<C>();
		Signature sig = this.getSignature();
		if (sig instanceof FolSignature) {
			for (Term<?> arg : this.getTerms()) {
				if (arg.getClass().equals(cls)) {
					@SuppressWarnings("unchecked")
					C castArg = (C) arg;
					reval.add(castArg);
				}
				// recursively add terms for all inner functional terms
				reval.addAll(arg.getTerms(cls));
			}
		}
		return reval;
	}

	@Override
	public InferenceRule<T> clone() {
		InferenceRule<T> rule = new InferenceRule<T>();
		rule.setConclusion(head);
		rule.addPremises(body);
		return rule;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AbaElement<T> substitute(Term<?> v, Term<?> t) throws IllegalArgumentException {
		InferenceRule<T> rule = new InferenceRule<T>();
		Signature sig = this.getSignature();
		if (sig instanceof FolSignature) {
			rule.setConclusion(((T) (((RelationalFormula) head).substitute(v, t))));
			for (T b : body) {
				rule.addPremise(((T) (((RelationalFormula) b).substitute(v, t))));
			}
			return rule;
		} else
			return this.clone();

	}

}
