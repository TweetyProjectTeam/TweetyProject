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
package net.sf.tweety.logics.commons.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.commons.LogicalSymbols;
import net.sf.tweety.logics.commons.syntax.interfaces.AssociativeFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Atom;
import net.sf.tweety.logics.commons.syntax.interfaces.ComplexLogicalFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.SimpleLogicalFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * This class provides common implementation for associative formulas that are
 * formulas which consists of several other formulas. It implements common
 * methods like getTerms(), getFormulas() or getAtoms(). Although
 * SimpleLogicalFormula can be used as content for an associative Formula this
 * support class also implements the functionality for ComplexLogicalFormula and
 * assumes that methods of ComplexLogicalFormula like substitute() are not
 * called by the parent of the AssociativeFormulaSupport, otherwise a
 * ClassCastException will occur.
 * 
 * @author Tim Janus
 *
 * @param <T> The type of the formulas which are saved by the associative
 *            formula support.
 */
public class AssociativeFormulaSupport<T extends SimpleLogicalFormula> extends ComplexLogicalFormulaAdapter
		implements AssociativeFormula<T> {

	/**
	 * This interface defines a bridge between the AssociativeFormula implementation
	 * like a concrete Disjunction and the AssociativeFormulaSupport object which
	 * adds the implementation of the common functionality. To provide as much
	 * common functionality as possible the AssociativeFormulaSupport needs the
	 * bridge to create empty associative formulas, empty signatures and the string
	 * representation for the operator symbol and the symbol that means that the
	 * associative formula is empty.
	 * 
	 * @author Tim Janus
	 */
	public static interface AssociativeSupportBridge {
		/**
		 * @return an empty version of the AssociativeFormula
		 * @param <T> the type of formulas
		 */
		<T extends SimpleLogicalFormula> AssociativeFormula<T> createEmptyFormula();

		/**
		 * @return An empty signature of the language of the AssociativeFormula
		 *         implementation
		 */
		Signature createEmptySignature();

		/**
		 * @return A String representing the operator which connects two items of the
		 *         associative formula.
		 */
		String getOperatorSymbol();

		/**
		 * @return A String representing an empty version of the Associative Formula
		 *         implementation
		 */
		String getEmptySymbol();
	}

	// -------------------------------------------------------------------------
	// ATTRIBUTES
	// -------------------------------------------------------------------------

	/** the bridge to the real implementation */
	private AssociativeSupportBridge bridge;

	/** the set of formulas */
	private List<T> formulas = new LinkedList<T>();

	// -------------------------------------------------------------------------
	// CTOR and COPY HELPER
	// -------------------------------------------------------------------------

	/**
	 * Ctor: Creates a AssociativeFormulaSupport object that uses the given bridge
	 * 
	 * @param bridge an associative support bridge
	 */
	public AssociativeFormulaSupport(AssociativeSupportBridge bridge) {
		this.bridge = bridge;
	}

	/**
	 * Copy-Ctor creates a deep copy of the associative formula support.
	 * 
	 * @param other another associative formula suppport
	 */
	public AssociativeFormulaSupport(AssociativeFormulaSupport<T> other) {
		this.bridge = other.bridge;
		for (T formula : other.formulas) {
			@SuppressWarnings("unchecked")
			T castformula = (T) formula.clone();
			formulas.add(castformula);
		}
	}

	/**
	 * This method generates a deep copy of the given collection of
	 * RelationalFormula
	 * 
	 * @param collection The collection of RelationalFormula to copy
	 * @return A collection containing the deep copy of the given collection
	 */
	public Collection<T> copyHelper(Collection<T> collection) {
		List<T> reval = new ArrayList<T>();
		for (T formula : collection) {
			@SuppressWarnings("unchecked")
			T cloned = (T) formula.clone();
			reval.add(cloned);
		}
		return reval;
	}

	// -------------------------------------------------------------------------
	// SIMPLE AND ASSOCIATIVE METHODS
	// -------------------------------------------------------------------------

	@Override
	public List<T> getFormulas() {
		return new LinkedList<T>(formulas);
	}

	@Override
	public <C extends SimpleLogicalFormula> Set<C> getFormulas(Class<C> cls) {
		Set<C> reval = new HashSet<C>();
		for (T rf : formulas) {
			if (rf.getClass().equals(cls)) {
				@SuppressWarnings("unchecked")
				C cast = (C) rf;
				reval.add(cast);
			}
		}
		return reval;
	}

	@Override
	public Set<? extends Atom> getAtoms() {
		Set<Atom> reval = new HashSet<Atom>();
		for (T formula : formulas) {
			reval.addAll(formula.getAtoms());
		}
		return reval;
	}

	@Override
	public Set<? extends Predicate> getPredicates() {
		Set<Predicate> predicates = new HashSet<Predicate>();
		for (T f : this)
			predicates.addAll(f.getPredicates());
		return predicates;
	}

	@Override
	public Signature getSignature() {
		Signature signature = bridge.createEmptySignature();
		for (T formula : this)
			signature.addSignature(formula.getSignature());
		return signature;
	}

	@Override
	public String toString() {
		if (this.isEmpty())
			return bridge.getEmptySymbol();
		String s = "";
		boolean isFirst = true;
		for (T f : this) {
			if (isFirst)
				isFirst = false;
			else
				s += bridge.getOperatorSymbol();
			// check if parentheses are needed
			if (f instanceof AssociativeFormula && ((AssociativeFormula<?>) f).size() > 1)
				s += LogicalSymbols.PARENTHESES_LEFT() + f.toString() + LogicalSymbols.PARENTHESES_RIGHT();
			else
				s += f.toString();
		}
		return s;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof AssociativeFormulaSupport<?>) {
			AssociativeFormulaSupport<?> cast = (AssociativeFormulaSupport<?>) other;
			return cast.formulas.equals(this.formulas);
		} else if (other instanceof List<?>) {
			return this.formulas.equals(other);
		} else if (other instanceof Set<?>) {
			List<?> temp = new LinkedList<Object>((Set<?>) other);
			return this.formulas.equals(temp);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return formulas.hashCode();
	}

	@Override
	public AssociativeFormulaSupport<T> clone() {
		return new AssociativeFormulaSupport<T>(this);
	}

	// -------------------------------------------------------------------------
	// COMPLEX FORMULA METHODS
	// -------------------------------------------------------------------------

	private ComplexLogicalFormula checkedFormulaType(T formula) {
		if (!(formula instanceof ComplexLogicalFormula)) {
			throw new IllegalArgumentException("The formula '" + formula.toString() + "' is no complex formula.");
		}
		return (ComplexLogicalFormula) formula;
	}

	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> reval = new HashSet<Term<?>>();
		for (T f : this) {
			reval.addAll(checkedFormulaType(f).getTerms());
		}
		return reval;
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		Set<C> reval = new HashSet<C>();
		for (T f : this) {
			reval.addAll(checkedFormulaType(f).getTerms(cls));
		}
		return reval;
	}

	@Override
	public ComplexLogicalFormula substitute(Term<?> v, Term<?> t) throws IllegalArgumentException {
		Set<ComplexLogicalFormula> substitutedFormulas = new HashSet<ComplexLogicalFormula>();
		for (T formula : this) {
			ComplexLogicalFormula substitudedFormula = checkedFormulaType(formula).substitute(v, t);
			substitutedFormulas.add(substitudedFormula);
		}
		AssociativeFormula<ComplexLogicalFormula> formula = bridge.createEmptyFormula();
		formula.addAll(substitutedFormulas);
		return (ComplexLogicalFormula) formula;
	}

	@Override
	public boolean isWellFormed() {
		if (this.size() < 2)
			return false;

		for (T formula : this) {
			if (checkedFormulaType(formula).isWellFormed())
				return false;
		}
		return true;
	}

	// -------------------------------------------------------------------------
	// COLLECTION<T> MEMBERS
	// -------------------------------------------------------------------------

	@Override
	public boolean add(T e) {
		return this.formulas.add(e);
	}

	/**
	 * Appends the specified elements to the end of this collection (optional operation).
	 * @param formulas to be appended to collection
	 * @return true if all elements were added, false otherwise
	 */
	@SuppressWarnings("unchecked")
	public boolean add(T... formulas) {
		boolean result = true;
		for (T f : formulas) {
			boolean sub = this.add(f);
			result = result && sub;
		}
		return result;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return this.formulas.addAll(c);
	}

	@Override
	public void clear() {
		this.formulas.clear();
	}

	@Override
	public boolean contains(Object o) {
		return this.formulas.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.formulas.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return this.formulas.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return this.formulas.iterator();
	}

	@Override
	public boolean remove(Object o) {
		return this.formulas.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.formulas.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.formulas.retainAll(c);
	}

	@Override
	public int size() {
		return this.formulas.size();
	}

	@Override
	public Object[] toArray() {
		return this.formulas.toArray();
	}

	@Override
	public <C> C[] toArray(C[] a) {
		return this.formulas.toArray(a);
	}

	@Override
	public Class<? extends Predicate> getPredicateCls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(int index, T element) {
		this.formulas.add(index, element);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return this.formulas.addAll(index, c);
	}

	@Override
	public T get(int index) {
		return this.formulas.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return this.formulas.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return this.formulas.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return this.formulas.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return this.formulas.listIterator(index);
	}

	@Override
	public T remove(int index) {
		return this.formulas.remove(index);
	}

	@Override
	public T set(int index, T element) {
		return this.formulas.set(index, element);
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return this.formulas.subList(fromIndex, toIndex);
	}

	// -------------------------------------------------------------------------
}
