/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.arg.saf.syntax;

import java.util.*;

import net.sf.tweety.arg.dung.semantics.*;
import net.sf.tweety.arg.dung.syntax.*;
import net.sf.tweety.arg.saf.*;
import net.sf.tweety.commons.util.rules.*;
import net.sf.tweety.logics.pl.syntax.*;

/**
 * This class models an argument structure, i.e. a minimal (with respect to set
 * inclusion) conflict-free sequence of basic arguments AS = [A1, ... ,An] such that for any
 * Ai and for any p in supp(Ai) there is an Aj with j > i and claim(Aj) = p.
 * 
 * @author Matthias Thimm
 */
public class ArgumentStructure extends Argument implements Collection<BasicArgument> {
	
	/**
	 * The derivation of this argument structure
	 */
	private Derivation<BasicArgument> derivation;
	
	/**
	 * Deprecated for argument structures.
	 * @param name a string
	 */
	@Deprecated
	public ArgumentStructure(String name){
		super(name);
		throw new IllegalArgumentException("Illegal constructor call for an argument structure");
	}
	
	/**
	 * Creates a new empty argument structure. 
	 */
	public ArgumentStructure(){
		this(new ArrayList<BasicArgument>());		
	}
	
	/**
	 * Creates a new argument structure with the given list of arguments.
	 * @param arguments a list of basic arguments.
	 */
	public ArgumentStructure(List<BasicArgument> arguments){
		super("ARG_STRUCTURE");
		this.derivation = new Derivation<BasicArgument>(arguments);		
	}
	
	/**
	 * Checks whether this argument structure is valid wrt. to the given
	 * structured argumentation framework, i.e. whether
	 * it is a minimal (with respect to set inclusion) conflict-free
	 * sequence of basic arguments AS = [A1, ... ,An] such that for any
	 * Ai and for any p in supp(Ai) there is an Aj with j > i and claim(Aj) = p.
	 * @param saf a structured argumentation framework.
	 * @return "true" iff this argument structure is valid.
	 */
	public boolean isValid(StructuredArgumentationFramework saf){		
		boolean isConflictFree = new Extension(this).isConflictFree(saf);
		boolean isFounded = this.derivation.isFounded();
		boolean isMinimal = this.derivation.isMinimal();
		return  isConflictFree && isFounded && isMinimal;
	}
	
	/**
	 * Checks whether this argument structure attacks
	 * the other argument structure, i.e. whether getTop(this)
	 * attacks some basic argument "a" in "other" wrt. "saf".
	 * @param other an argument structure
	 * @param saf a structured argumentation framework
	 * @return "true" if this attacks "other".
	 */
	public boolean attacks(ArgumentStructure other, StructuredArgumentationFramework saf){
		BasicArgument top = this.getTop();
		for(BasicArgument a: other)
			if(saf.isAttackedBy(a, top))
				return true;
		return false;
	}
	
	/**
	 * Returns the claim of this argument structure, i.e.
	 * the claim of its first basic argument.
	 * @return the claim of this argument structure or "null" if
	 * 	it is empty.
	 */
	public Proposition getClaim(){
		if(this.isEmpty())
			return null;
		return this.getTop().getConclusion();
	}
	
	/**
	 * Returns the first basic arguments of
	 * this argument structure
	 * @return the first basic argument or "null" if
	 * 	this is empty.
	 */
	public BasicArgument getTop(){
		if(this.isEmpty())
			return null;
		return this.derivation.get(0);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	@Override
	public boolean add(BasicArgument e) {
		return this.derivation.add(e);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends BasicArgument> c) {
		return this.addAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		this.derivation.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		return this.derivation.contains(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return this.derivation.containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.derivation.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#iterator()
	 */
	@Override
	public Iterator<BasicArgument> iterator() {
		return this.derivation.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		return this.derivation.remove(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		return this.derivation.removeAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		return this.derivation.retainAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	@Override
	public int size() {
		return this.derivation.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public Object[] toArray() {
		return this.derivation.toArray();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		return this.derivation.toArray(a);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.dung.syntax.Argument#toString()
	 */
	public String toString(){
		return this.derivation.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((derivation == null) ? 0 : derivation.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArgumentStructure other = (ArgumentStructure) obj;
		if (derivation == null) {
			if (other.derivation != null)
				return false;
		} else if (!derivation.equals(other.derivation))
			return false;
		return true;
	}
	
	
}
