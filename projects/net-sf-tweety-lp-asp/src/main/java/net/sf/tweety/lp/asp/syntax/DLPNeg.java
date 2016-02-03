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
package net.sf.tweety.lp.asp.syntax;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.tweety.logics.commons.error.LanguageException;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * This class models strict negation for atoms.
 * 
 * @author Tim Janus
 * @author Thomas Vengels
 */
public class DLPNeg extends DLPElementAdapter implements DLPLiteral {

	DLPAtom	atom;
	
	public DLPNeg(DLPAtom inner) {
		this.atom = inner;
	}
	
	public DLPNeg(DLPNeg other) {
		this.atom = (DLPAtom)other.getAtom().clone();
	}
	
	public DLPNeg(String symbol, Term<?>... terms) {
		atom = new DLPAtom(symbol, terms);
	}
	
	/**
	 * default constructor, create an atom from a functor name
	 * and a list of terms. size of terms determines arity of
	 * functor.
	 * 
	 * @param symbol the functor name
	 * @param terms the terms (arguments) of the atom
	 */
	public DLPNeg(String symbol, List<Term<?>> terms) {
		atom = new DLPAtom(symbol, terms);
	}
	
	public DLPNeg(String expr) {
		atom = new DLPAtom(expr);
	}

	@Override
	public DLPAtom getAtom() {
		return this.atom;
	}
	
	@Override
	public String toString() {
		return "-" + this.atom;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof DLPNeg) {
			DLPNeg on = (DLPNeg) o;
			
			// compare atom
			return on.getAtom().equals( this.getAtom() );
		} else {
			return false;
		}
	}

	@Override
	public boolean isGround() {
		return atom.isGround();
	}
	
	@Override
	public DLPNeg clone() {
		return new DLPNeg(this);
	}

	@Override
	public DLPAtom complement() {
		return atom;
	}

	@Override
	public DLPLiteral cloneWithAddedTerm(Term<?> term) {
		DLPNeg reval = (DLPNeg)this.clone();
		reval.atom = reval.atom.cloneWithAddedTerm(term);
		return reval;
	}
	
	@Override 
	public int hashCode() {
		return 7 + atom.hashCode();
	}

	@Override
	public DLPNeg substitute(Term<?> v, Term<?> t)
			throws IllegalArgumentException {
		return new DLPNeg(this.atom.substitute(v,t));
	}

	@Override
	public Set<DLPAtom> getAtoms() {
		return atom.getAtoms();
	}

	@Override
	public Set<DLPPredicate> getPredicates() {
		return atom.getPredicates();
	}

	@Override
	public DLPSignature getSignature() {
		return atom.getSignature();
	}

	@Override
	public Set<Term<?>> getTerms() {
		return atom.getTerms();
	}

	@Override
	public String getName() {
		return atom.getName();
	}

	@Override
	public DLPPredicate getPredicate() {
		return atom.getPredicate();
	}

	@Override
	public RETURN_SET_PREDICATE setPredicate(Predicate predicate) {
		return atom.setPredicate(predicate);
	}

	@Override
	public void addArgument(Term<?> arg) throws LanguageException {
		atom.addArgument(arg);
	}

	@Override
	public List<? extends Term<?>> getArguments() {
		return atom.getArguments();
	}

	@Override
	public boolean isComplete() {
		return atom.isComplete();
	}

	@Override
	public SortedSet<DLPLiteral> getLiterals() {
		SortedSet<DLPLiteral> reval = new TreeSet<DLPLiteral>();
		reval.add(this);
		return reval;
	}

	@Override
	public int compareTo(DLPLiteral o) {
		if(o instanceof DLPAtom) {
			return 1;
		}
		return this.toString().compareTo(o.toString());
	}
}
