/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.rdl.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Conjuctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Disjunctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.ClassicalInference;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Disjunction;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;
import net.sf.tweety.logics.rdl.DefaultTheory;
import net.sf.tweety.math.probability.Probability;

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

	public DefaultRule(FolFormula pre, Collection<FolFormula> jus, FolFormula conc) throws ParserException {
		super();
		if (pre == null)
			throw new ParserException("Prerequisite needed to form default rule.");
		if (conc == null)
			throw new ParserException("Conclusion needed to form default rule.");
		if (jus.isEmpty())
			throw new ParserException("Justification needed to form default rule.");
		this.pre = pre;
		this.jus = new LinkedList<>();
		this.jus.addAll(jus);
		this.conc = conc;
	}

	/**
	 * Tests, wether the defautl is normal
	 * normal ^= a:c/c
	 */
	public boolean isNormal(DefaultTheory dt) {
		for (FolFormula f : jus) {
			if (!ClassicalInference.equivalent(f, conc))
				return false;
		}
		return true;
	}

	public FolFormula getPre() {
		return pre;
	}

	public Collection<FolFormula> getJus() {
		return jus;
	}

	public FolFormula getConc() {
		return conc;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.syntax.interfaces.SimpleLogicalFormula#getPredicates()
	 */
	@Override
	public Set<? extends Predicate> getPredicates() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.syntax.interfaces.SimpleLogicalFormula#isLiteral()
	 */
	@Override
	public boolean isLiteral() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.syntax.interfaces.QuantifiedFormula#getQuantifierVariables()
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
	 * @see net.sf.tweety.logics.commons.syntax.interfaces.QuantifiedFormula#getUnboundVariables()
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
	 * @see net.sf.tweety.logics.commons.syntax.interfaces.QuantifiedFormula#containsQuantifier()
	 */
	@Override
	public boolean containsQuantifier() {
		boolean result = conc.containsQuantifier() || pre.containsQuantifier();
		for (FolFormula f : jus)
			result |= f.containsQuantifier();
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.syntax.interfaces.QuantifiedFormula#isWellBound()
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
	 * @see net.sf.tweety.logics.commons.syntax.interfaces.QuantifiedFormula#isWellBound(java.util.Set)
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
	 * @see net.sf.tweety.logics.commons.syntax.interfaces.QuantifiedFormula#isClosed()
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
	 * @see net.sf.tweety.logics.commons.syntax.interfaces.QuantifiedFormula#isClosed(java.util.Set)
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
	 * @see net.sf.tweety.logics.commons.syntax.interfaces.LogicStructure#getTerms()
	 */
	@Override
	public Set<Term<?>> getTerms() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.syntax.interfaces.LogicStructure#getTerms(java.lang.Class)
	 */
	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.syntax.RelationalFormula#getAtoms()
	 */
	@Override
	public Set<FOLAtom> getAtoms() {
		Set<FOLAtom> atoms = conc.getAtoms();
		atoms.addAll(pre.getAtoms());
		for (FolFormula f : jus)
			atoms.addAll(f.getAtoms());
		return atoms;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.syntax.RelationalFormula#getFunctors()
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
	 * @see net.sf.tweety.logics.fol.syntax.RelationalFormula#substitute(net.sf.tweety.logics.commons.syntax.interfaces.Term, net.sf.tweety.logics.commons.syntax.interfaces.Term)
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
	 * @see net.sf.tweety.logics.fol.syntax.RelationalFormula#getUniformProbability()
	 */
	@Override
	public Probability getUniformProbability() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.syntax.RelationalFormula#complement()
	 */
	@Override
	public RelationalFormula complement() {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException("");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.syntax.RelationalFormula#combineWithOr(net.sf.tweety.logics.commons.syntax.interfaces.Disjunctable)
	 */
	@Override
	public Disjunction combineWithOr(Disjunctable formula) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException("");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.syntax.RelationalFormula#combineWithAnd(net.sf.tweety.logics.commons.syntax.interfaces.Conjuctable)
	 */
	@Override
	public Conjunction combineWithAnd(Conjuctable formula) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException("");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.syntax.RelationalFormula#toString()
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
	 * @see net.sf.tweety.logics.fol.syntax.RelationalFormula#clone()
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
