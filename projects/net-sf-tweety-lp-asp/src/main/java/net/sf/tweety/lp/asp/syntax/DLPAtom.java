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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.tweety.logics.commons.error.LanguageException;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * this class models an atom, which is a basic structure for
 * building literals and rules for logic programs
 * 
 * @author Tim Janus
 * @author Thomas Vengels
 * @author Matthias Thimm
 */
public class DLPAtom extends DLPElementAdapter implements DLPLiteral {

	/** the predicate identifying the atom */
	protected DLPPredicate		pred;
	
	/** a list of arguments of the atom */
	protected List<Term<?>>	arguments = new LinkedList<Term<?>>();
	
	/** Default-Ctor: Used for dynamic instantiation */
	public DLPAtom() {}
	
	/**
	 * Copy-Ctor: Generates a deep copy of the given atom
	 * @param other	The atom acting as source for the deep copy
	 */
	public DLPAtom(DLPAtom other) {
		this.pred = new DLPPredicate(other.getName(), other.getArity());
		for(Term<?> t : other.getArguments()) {
			this.arguments.add((Term<?>)t.clone());
		}
	}
	
	/**
	 * Copy-Ctor: Generates a deep copy of the given FOL atom
	 * @param other	The FOL atom acting as source for the deep copy
	 */
	public DLPAtom(FOLAtom other) {
		this.pred = new DLPPredicate(other.getPredicate().getName(), other.getArguments().size());
		for(Term<?> t: other.getArguments())
			this.arguments.add((Term<?>) t.clone());
	}
	
	/**
	 * Ctor: Creates an atom with the given predicate as name and the
	 * given terms as argument
	 * @param symbol	The name of the atom
	 * @param terms		A list of Term<?> defining the arguments of the term
	 */
	public DLPAtom(String symbol, Term<?>... terms) {
		this.pred = new DLPPredicate(symbol, terms.length);
		for(int i=0; i<terms.length; ++i) {
			this.arguments.add(terms[i]);
		}
	}
	
	/**
	 * Creates a predicate for the atom from symbol name
	 * and uses a list of terms as arguments for the atom. 
	 * Size of terms determines arity of the predicate.
	 */
	public DLPAtom(String symbol, List<? extends Term<?>> terms) {
		this.pred = new DLPPredicate(symbol, terms.size());
		this.arguments.addAll(terms);
	}

	@Override
	public DLPAtom getAtom() {
		return this;
	}
	
	public int getArity() {
		return this.pred.getArity();
	}

	@Override 
	public String toString() {
		String ret = "";
		ret += this.pred.getName();
		
		if (arguments.size()>0) {
			ret += "(" + this.arguments.get(0);
			for (int i = 1; i < arguments.size(); i++)
				ret += ", " + this.arguments.get(i);
			ret += ")";
		}
		return ret;
	}
	
	public Term<?>	getTerm(int index) {
		if ( (index <0) || (this.arguments == null))
			return null;
		if ( index >= this.arguments.size())
			return null;
		
		return this.arguments.get(index);
	}
	
	@Override
	public DLPAtom cloneWithAddedTerm(Term<?> tval)  {
		DLPAtom reval = (DLPAtom)this.clone();
		reval.pred.setArity(reval.pred.getArity() + 1);
		reval.arguments.add(tval);
		return reval;
	}
	
	@Override
	public int hashCode() {
		return (pred == null ? 0 : pred.hashCode()) + 
				arguments.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof DLPAtom) {			
			DLPAtom oa = (DLPAtom) o;	
			
			// functors must be equal:
			if(oa.pred != null) {
				if (!oa.pred.equals( this.pred)) {
					return false;
				}
			} else if(this.pred != null) {
				return false;
			}
			
			if(!oa.arguments.equals(arguments))
				return false;
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public DLPAtom clone() {
		return new DLPAtom(this);
	}
	
	@Override
	public String getName() {
		return pred.getName();
	}

	@Override
	public boolean isGround() {
		if(arguments == null)
			return true;
		
		for(Term<?> t : arguments) {
			if(t instanceof Variable)
				return false;
			else if(t instanceof DLPAtom) {
				return ((DLPAtom)t).isGround();
			}
		}
		return true;
	}

	@Override
	public DLPNeg complement() {
		return new DLPNeg(this);
	}

	@Override
	public DLPAtom substitute(Term<?> v, Term<?> t)
			throws IllegalArgumentException {
		DLPAtom reval = new DLPAtom(this);
		reval.arguments.clear();
		for(int i=0; i<arguments.size(); i++) {
			if(arguments.get(i).equals(v)) {
				reval.arguments.add(t);
			} else {
				reval.arguments.add(arguments.get(i));
			}
		}
		return reval;
	}

	@Override
	public Set<DLPAtom> getAtoms() {
		Set<DLPAtom> reval = new HashSet<DLPAtom>();
		reval.add(this);
		return reval;
	}

	@Override
	public Set<DLPPredicate> getPredicates() {
		Set<DLPPredicate> reval = new HashSet<DLPPredicate>();
		reval.add(pred);
		return reval;
	}

	@Override
	public FolSignature getSignature() {
		FolSignature reval = new FolSignature();
		reval.add(this);
		return reval;
	}

	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> reval = new HashSet<Term<?>>();
		reval.addAll(arguments);
		return reval;
	}

	@Override
	public DLPPredicate getPredicate() {
		return pred;
	}

	@Override
	public RETURN_SET_PREDICATE setPredicate(Predicate predicate) {
		Predicate old = this.pred;
		this.pred = (DLPPredicate)predicate;
		return AtomImpl.implSetPredicate(old, this.pred, arguments);
	}

	@Override
	public void addArgument(Term<?> arg) throws LanguageException {
		this.arguments.add(arg);
	}

	@Override
	public List<? extends Term<?>> getArguments() {
		return Collections.unmodifiableList(arguments);
	}

	@Override
	public boolean isComplete() {
		return getTerms().size() == pred.getArity();
	}

	@Override
	public SortedSet<DLPLiteral> getLiterals() {
		SortedSet<DLPLiteral> reval = new TreeSet<DLPLiteral>();
		reval.add(this);
		return reval;
	}

	@Override
	public int compareTo(DLPLiteral o) {
		if(o instanceof DLPNeg) { 
			return -1;
		}
		return toString().compareTo(o.toString());
	}
}
