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
package net.sf.tweety.logics.pl.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.commons.syntax.AssociativeFormulaSupport;
import net.sf.tweety.logics.commons.syntax.AssociativeFormulaSupport.AssociativeSupportBridge;
import net.sf.tweety.logics.commons.syntax.interfaces.AssociativeFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.SimpleLogicalFormula;


/**
 * This class captures the common functionalities of formulas with an associative
 * operation like conjunction, disjunction, etc.
 *
 * @author Matthias Thimm
 * @author Tim Janus
 */
public abstract class AssociativePropositionalFormula extends PropositionalFormula 
	implements AssociativeFormula<PropositionalFormula>,
	AssociativeSupportBridge,
	Collection<PropositionalFormula> {

	/**
	 * The inner formulas of this formula 
	 */
	protected AssociativeFormulaSupport<PropositionalFormula> support;
	
	/**
	 * Creates a new (empty) associative formula.
	 */
	public AssociativePropositionalFormula(){
		support = new AssociativeFormulaSupport<PropositionalFormula>(this);
	}
	
	/**
	 * Creates a new associative formula with the given inner formulas. 
	 * @param formulas a collection of formulas.
	 */
	public AssociativePropositionalFormula(Collection<? extends PropositionalFormula> formulas){
		this();
		this.support.addAll(formulas);
	}
	
	/**
	 * Creates a new associative formula with the two given formulae
	 * @param first a propositional formula.
	 * @param second a propositional formula.
	 */
	public AssociativePropositionalFormula(PropositionalFormula first, PropositionalFormula second){
		this();
		this.add(first);
		this.add(second);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<PropositionalPredicate> getPredicates() {
		return (Set<PropositionalPredicate>) support.getPredicates();
	}
	
	@Override
	public List<PropositionalFormula> getFormulas() {
		return support.getFormulas();
	}

	@Override
	public <C extends SimpleLogicalFormula> Set<C> getFormulas(Class<C> cls) {
		return support.getFormulas(cls);
	
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Set<Proposition> getAtoms() {
		return (Set<Proposition>) support.getAtoms();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.syntax.PropositionalFormula#getLiterals()
	 */
	@Override
	public Set<PropositionalFormula> getLiterals(){
		Set<PropositionalFormula> result = new HashSet<PropositionalFormula>();
		for(PropositionalFormula f: this.support){
			result.addAll(f.getLiterals());
		}
		return result;
	}
	
	@Override
	public String toString() {
		return support.toString();
	}
	
	@Override
	public PropositionalSignature getSignature() {
		return (PropositionalSignature)support.getSignature();
	}
	
	@Override
	public Signature createEmptySignature() {
		return new PropositionalSignature();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.syntax.PropositionalFormula#numberOfOccurrences(net.sf.tweety.logics.pl.syntax.Proposition)
	 */
	public  int numberOfOccurrences(Proposition p){
		int result = 0;
		for(PropositionalFormula f: this.support.getFormulas())
			result += f.numberOfOccurrences(p);		
		return result;
	}
		
	//-------------------------------------------------------------------------
	//	UTILITY METHODS
	//-------------------------------------------------------------------------
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((support == null) ? 0 : support.getFormulas().hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssociativePropositionalFormula other = (AssociativePropositionalFormula) obj;
		if (support == null) {
			if (other.support != null)
				return false;
		} else if (!support.getFormulas().equals(other.support.getFormulas()))
			return false;
		else if(support.size() != other.support.size())
			return false;
		return true;
	}
	
	//-------------------------------------------------------------------------
	//	COLLECTION INTERFACE
	//-------------------------------------------------------------------------
	
	@Override
	public boolean add(PropositionalFormula f){
		return this.support.add(f);
	}
	
	@Override
	public boolean addAll(Collection<? extends PropositionalFormula> c){
		return this.support.addAll(c);
	}
	
	@Override
	public void clear(){
		this.support.clear();
	}
	
	@Override
	public boolean contains(Object o){
		return this.support.contains(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c){
		return this.support.containsAll(c);
	}
	
	@Override
	public boolean isEmpty(){
		return this.support.isEmpty();
	}
	
	@Override
	public Iterator<PropositionalFormula> iterator(){
		return this.support.iterator();
	}

	@Override
	public boolean remove(Object o){
		return this.support.remove(o);
	}
	
	@Override
	public boolean removeAll(Collection<?> c){
		return this.support.removeAll(c);
	}
	
	@Override
	public boolean retainAll(Collection<?> c){
		return this.support.retainAll(c);
	}
	
	@Override
	public int size(){
		return this.support.size();
	}
	
	@Override
	public Object[] toArray(){
		return this.support.toArray();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Object[] toArray(Object[] a){
		return this.support.toArray(a);
	}
	
	@Override
	public void add(int index, PropositionalFormula element) {
		this.support.add(index, element);
	}

	@Override
	public boolean addAll(int index, Collection<? extends PropositionalFormula> c) {
		return this.support.addAll(index, c);
	}

	@Override
	public PropositionalFormula get(int index) {
		return this.support.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return this.support.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return this.support.lastIndexOf(o);
	}

	@Override
	public ListIterator<PropositionalFormula> listIterator() {
		return this.support.listIterator();
	}

	@Override
	public ListIterator<PropositionalFormula> listIterator(int index) {
		return this.support.listIterator(index);
	}

	@Override
	public PropositionalFormula remove(int index) {
		return this.support.remove(index);
	}

	@Override
	public PropositionalFormula set(int index, PropositionalFormula element) {
		return this.support.set(index, element);
	}

	@Override
	public List<PropositionalFormula> subList(int fromIndex, int toIndex) {
		return this.support.subList(fromIndex, toIndex);
	}

}
