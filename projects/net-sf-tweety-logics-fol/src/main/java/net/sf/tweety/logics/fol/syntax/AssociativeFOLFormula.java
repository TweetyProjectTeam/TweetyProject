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
package net.sf.tweety.logics.fol.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.commons.syntax.AssociativeFormulaSupport;
import net.sf.tweety.logics.commons.syntax.AssociativeFormulaSupport.AssociativeSupportBridge;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.AssociativeFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.SimpleLogicalFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;


/**
 * This class captures the common functionalities first order associative formulas like conjunction,
 * disjunction, etc.
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 */
public abstract class AssociativeFOLFormula extends FolFormula implements 
	AssociativeFormula<RelationalFormula>,
	AssociativeSupportBridge {
	
	/**
	 * This helper class implements most of the common functionality of an associative
	 * formula, so the implementation can delegate the method calls to the support
	 * class. 
	 */
	protected AssociativeFormulaSupport<RelationalFormula> support;

	//-------------------------------------------------------------------------
	//	CONSTRUCTORS:
	//-------------------------------------------------------------------------
	
	/**
	 * Creates a new (empty) associative formula.
	 */
	public AssociativeFOLFormula(){
		this.support = new AssociativeFormulaSupport<RelationalFormula>(this);
	}
	
	/**
	 * Creates a new associative formula with the two given formulae
	 * @param first a relational formula.
	 * @param second a relational formula.
	 */
	public AssociativeFOLFormula(RelationalFormula first, RelationalFormula second){
		this();
		this.add(first);
		this.add(second);
	}
	
	/**
	 * Creates a new associative formula with the given inner formulas. 
	 * @param formulas a collection of formulas.
	 */
	public AssociativeFOLFormula(Collection<? extends RelationalFormula> formulas) {
		this();
		this.addAll(formulas);
	}
	
	@Override
	public Signature createEmptySignature() {
		return new FolSignature();
	}
	
	//-------------------------------------------------------------------------
	//	METHODS OF RELATIONAL OR FOL FORMULA
	//-------------------------------------------------------------------------

	@Override
	public Set<Functor> getFunctors(){
		Set<Functor> functors = new HashSet<Functor>();
		for(RelationalFormula f: this)
			functors.addAll(f.getFunctors());
		return functors;
	}
	
	@Override
	public boolean containsQuantifier(){
		for(RelationalFormula f: this)
			if(f.containsQuantifier()) return true;
		return false;
	}
	
	@Override
	public Set<Variable> getUnboundVariables(){
		Set<Variable> variables = new HashSet<Variable>();
		for(RelationalFormula f: this)
			variables.addAll(f.getUnboundVariables());
		return variables;
	}
	
	@Override
	public boolean isClosed(){
		for(RelationalFormula f: this)
			if(!f.isClosed()) return false;
		return true;
	}
	
	@Override
	public boolean isClosed(Set<Variable> boundVariables){
		for(RelationalFormula f: this)
			if(!f.isClosed(boundVariables)) return false;
		return true;
	}
	
	@Override
	public boolean isWellBound(){
		for(RelationalFormula f: this)
			if(!f.isWellBound()) return false;
		return true;
	}
	
	@Override
	public boolean isWellBound(Set<Variable> boundVariables){
		for(RelationalFormula f: this)
			if(!f.isWellBound(boundVariables)) return false;
		return true;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}
	
	@Override
	public Set<Variable> getQuantifierVariables() {
		Set<Variable> reval = new HashSet<Variable>();
		for(RelationalFormula f : this) {
			reval.addAll(f.getQuantifierVariables());
		}
		return reval;
	}
	
	//-------------------------------------------------------------------------
	//	METHODS IMPLEMENTED IN AssociativeFormulaSupport:
	//-------------------------------------------------------------------------
	
	@Override
	public List<RelationalFormula> getFormulas() {
		return support.getFormulas();
	}

	@Override
	public <C extends SimpleLogicalFormula> Set<C> getFormulas(Class<C> cls) {
		Set<C> reval = new HashSet<C>();
		for(RelationalFormula rf : support) {
			if(rf.getClass().equals(cls)) {
				@SuppressWarnings("unchecked")
				C cast = (C)rf;
				reval.add(cast);
			}
		}
		return reval;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<FOLAtom> getAtoms(){
		return (Set<FOLAtom>) support.getAtoms();
	}
	
	@Override
	public Set<Term<?>> getTerms() {
		return support.getTerms();
	}
	
	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		return support.getTerms(cls);
	}

	@Override
	public Set<? extends Predicate> getPredicates(){
		return support.getPredicates();
	}
	
	@Override
	public AssociativeFOLFormula substitute(Term<?> v, Term<?> t) {
		return (AssociativeFOLFormula)support.substitute(v,  t);
	}

	@Override
	public AssociativeFOLFormula substitute(Map<? extends Term<?>, ? extends Term<?>> termMap) {
		return (AssociativeFOLFormula)support.substitute(termMap);
	}
	
	@Override
	public String toString() {
		return support.toString();
	}
	
	//-------------------------------------------------------------------------
	//	Utility Methods
	//-------------------------------------------------------------------------
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((support == null) ? 0 : support.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssociativeFOLFormula other = (AssociativeFOLFormula) obj;
		if (support == null) {
			if (other.support != null)
				return false;
		} else if (!support.equals(other.support))
			return false;
		return true;
	}	
	
	//-------------------------------------------------------------------------
	//	COLLECTION Interface:
	//-------------------------------------------------------------------------
	
	@Override
	public boolean add(RelationalFormula e) {
		return this.support.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends RelationalFormula> c) {
		return this.support.addAll(c);
	}

	@Override
	public void clear() {
		this.support.clear();
	}

	@Override
	public boolean contains(Object o) {
		return this.support.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.support.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return this.support.isEmpty();
	}

	@Override
	public Iterator<RelationalFormula> iterator() {
		return this.support.iterator();
	}

	@Override
	public boolean remove(Object o) {
		return this.support.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.support.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.support.retainAll(c);
	}

	@Override
	public int size() {
		return this.support.size();
	}

	@Override
	public Object[] toArray() {
		return this.support.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.support.toArray(a);
	}
	
	@Override
	public void add(int index, RelationalFormula element) {
		this.support.add(index, element);
	}

	@Override
	public boolean addAll(int index, Collection<? extends RelationalFormula> c) {
		return this.support.addAll(index, c);
	}

	@Override
	public RelationalFormula get(int index) {
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
	public ListIterator<RelationalFormula> listIterator() {
		return this.support.listIterator();
	}

	@Override
	public ListIterator<RelationalFormula> listIterator(int index) {
		return this.support.listIterator(index);
	}

	@Override
	public RelationalFormula remove(int index) {
		return this.support.remove(index);
	}

	@Override
	public RelationalFormula set(int index, RelationalFormula element) {
		return this.support.set(index, element);
	}

	@Override
	public List<RelationalFormula> subList(int fromIndex, int toIndex) {
		return this.support.subList(fromIndex, toIndex);
	}
}
