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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.commons.syntax.AssociativeFormulaSupport;
import net.sf.tweety.logics.commons.syntax.AssociativeFormulaSupport.AssociativeSupportBridge;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.interfaces.AssociativeFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Atom;
import net.sf.tweety.logics.commons.syntax.interfaces.ComplexLogicalFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Disjunctable;
import net.sf.tweety.logics.commons.syntax.interfaces.SimpleLogicalFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * This formula represents the head of an disjunctive rule which is a
 * disjunction of ELP literals. An ELP itself only allows one literal in
 * the head.
 * 
 * @author Tim Janus
 */
public class DLPHead extends DLPElementAdapter 
	implements AssociativeFormula<DLPLiteral>, 
				Disjunctable, 
				ComplexLogicalFormula,
				AssociativeSupportBridge{

	private AssociativeFormulaSupport<DLPLiteral> assocSupport =
			new AssociativeFormulaSupport<DLPLiteral>(this);
	
	/** Default-Ctor: Generates empty head */
	public DLPHead() {}
	
	/**
	 * Ctor: Generates a head with one literal
	 * @param literal	The literal of the head
	 */
	public DLPHead(DLPLiteral literal) {
		this.add(literal);
	}
	
	/**
	 * Copy-Ctor: Generates a deep copy from the given head
	 * @param other		The DLPHead object which acts as source for the copy operation.
	 */
	public DLPHead(DLPHead other) {
		this();
		this.assocSupport.addAll(other.assocSupport);
	}
	
	@Override
	public Set<DLPAtom> getAtoms() {
		Set<DLPAtom> reval = new HashSet<DLPAtom>();
		for(Atom at : assocSupport.getAtoms()) {
			if(at instanceof DLPAtom) {
				reval.add((DLPAtom)at);
			}
		}
		return reval;
	}

	@Override
	public Set<DLPPredicate> getPredicates() {
		Set<DLPPredicate> reval = new HashSet<DLPPredicate>();
		for(Predicate pred : assocSupport.getPredicates()) {
			if(pred instanceof DLPPredicate) {
				reval.add((DLPPredicate)pred);
			}
		}
		return reval;
	}

	@Override
	public Class<? extends Predicate> getPredicateCls() {
		return DLPPredicate.class;
	}

	@Override
	public FolSignature getSignature() {
		return (FolSignature)assocSupport.getSignature();
	}

	@Override
	public List<DLPLiteral> getFormulas() {
		return assocSupport.getFormulas();
	}

	@Override
	public <C extends SimpleLogicalFormula> Set<C> getFormulas(Class<C> cls) {
		return assocSupport.getFormulas(cls);
	}
	
	@Override
	public DLPHead substitute(Term<?> v, Term<?> t)
			throws IllegalArgumentException {
		return (DLPHead)assocSupport.substitute(v,t);
	}
	
	@Override
	public SimpleLogicalFormula combineWithOr(Disjunctable f) {
		if(! (f instanceof DLPLiteral))
			throw new IllegalArgumentException();
		
		DLPLiteral arg = (DLPLiteral)f;
		DLPHead reval = new DLPHead(this);
		reval.add(arg);
		return reval;
	}

	@Override
	public <T extends SimpleLogicalFormula> AssociativeFormula<T> createEmptyFormula() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Signature createEmptySignature() {
		return new FolSignature();
	}

	@Override
	public String getOperatorSymbol() {
		return ";";
	}

	@Override
	public String getEmptySymbol() {
		return "";
	}


	@Override
	public DLPHead clone() {
		return new DLPHead(this);
	}
	
	@Override
	public SortedSet<DLPLiteral> getLiterals() {
		SortedSet<DLPLiteral> reval = new TreeSet<DLPLiteral>();
		for(DLPElement element : this) {
			reval.addAll(element.getLiterals());
		}
		return reval;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof AssociativeFormula<?>) {
			AssociativeFormula<?> cast = (AssociativeFormula<?>)other;
			return assocSupport.equals(cast.getFormulas()) && this.getClass().equals(other.getClass());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return assocSupport.hashCode() + this.getClass().hashCode();
	}
	
	@Override
	public String toString() {
		return assocSupport.toString();
	}
	
	//-------------------------------------------------------------------------
	//	COLLECTION METHODS
	//-------------------------------------------------------------------------
	
	@Override
	public boolean add(DLPLiteral arg0) {
		return assocSupport.add(arg0);
	}

	@Override
	public boolean addAll(Collection<? extends DLPLiteral> arg0) {
		return assocSupport.addAll(arg0);
	}

	@Override
	public void clear() {
		assocSupport.clear();
	}

	@Override
	public boolean contains(Object arg0) {
		return assocSupport.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return assocSupport.containsAll(arg0);
	}

	@Override
	public boolean isEmpty() {
		return assocSupport.isEmpty();
	}

	@Override
	public Iterator<DLPLiteral> iterator() {
		return assocSupport.iterator();
	}

	@Override
	public boolean remove(Object arg0) {
		return assocSupport.remove(arg0);
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		return assocSupport.removeAll(arg0);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		return assocSupport.retainAll(arg0);
	}

	@Override
	public int size() {
		return assocSupport.size();
	}

	@Override
	public Object[] toArray() {
		return assocSupport.toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		return assocSupport.toArray(arg0);
	}

	@Override
	public Set<Term<?>> getTerms() {
		return assocSupport.getTerms();
	}

	@Override
	public void add(int index, DLPLiteral element) {
		assocSupport.add(index, element);
	}

	@Override
	public boolean addAll(int index, Collection<? extends DLPLiteral> c) {
		return assocSupport.addAll(index, c);
	}

	@Override
	public DLPLiteral get(int index) {
		return assocSupport.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return assocSupport.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return assocSupport.lastIndexOf(o);
	}

	@Override
	public ListIterator<DLPLiteral> listIterator() {
		return assocSupport.listIterator();
	}

	@Override
	public ListIterator<DLPLiteral> listIterator(int index) {
		return assocSupport.listIterator(index);
	}

	@Override
	public DLPLiteral remove(int index) {
		return assocSupport.remove(index);
	}

	@Override
	public DLPLiteral set(int index, DLPLiteral element) {
		return assocSupport.set(index, element);
	}

	@Override
	public List<DLPLiteral> subList(int fromIndex, int toIndex) {
		return assocSupport.subList(fromIndex, toIndex);
	}
}
