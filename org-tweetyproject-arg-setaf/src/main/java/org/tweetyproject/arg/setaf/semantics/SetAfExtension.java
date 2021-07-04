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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.setaf.semantics;

import java.util.*;

import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.syntax.*;


/**
 * This class models a (possible) extension of a Setaf theory, i.e. a set of arguments.
 *  
 * @author  Sebastian Franke
 *
 */
public class SetAfExtension extends SetAfAbstractArgumentationInterpretation implements Collection<Argument>, Comparable<SetAfExtension> {
	
	/**
	 * The arguments in the extension
	 */
	private Set<Argument> arguments; 
	
	/**
	 * Creates a new empty extension.
	 */
	public SetAfExtension(){
		this(new HashSet<Argument>());
	}

	/**
	 * Creates a new extension with the given set of arguments.
	 * @param arguments a set of arguments
	 */
	public SetAfExtension(Collection<? extends Argument> arguments){
		this.arguments = new HashSet<Argument>(arguments);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String s = "{";
		boolean first = true;
		for(Argument a: this)
			if(first){
				s += a;
				first = false;
			}else s += "," + a;		
		return s+="}";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((arguments == null) ? 0 : arguments.hashCode());
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
		SetAfExtension other = (SetAfExtension) obj;
		if (arguments == null) {
			if (other.arguments != null)
				return false;
		} else if (!arguments.equals(other.arguments))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.argumentation.setaf.semantics.AbstractArgumentationInterpretation#getArgumentsOfStatus(org.tweetyproject.argumentation.setaf.semantics.ArgumentStatus)
	 */
	@Override
	public SetAfExtension getArgumentsOfStatus(ArgumentStatus status) {
		if(status.equals(ArgumentStatus.IN)) return this;
		throw new IllegalArgumentException("Arguments of status different from \"IN\" cannot be determined from an extension alone");
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	@Override
	public boolean add(Argument arg0) {
		return this.arguments.add(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends Argument> arg0) {
		return this.arguments.addAll(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		this.arguments.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object arg0) {
		return this.arguments.contains(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> arg0) {
		return this.arguments.containsAll(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.arguments.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#iterator()
	 */
	@Override
	public Iterator<Argument> iterator() {
		return this.arguments.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object arg0) {
		return this.arguments.remove(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> arg0) {
		return this.arguments.removeAll(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> arg0) {
		return this.arguments.retainAll(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	@Override
	public int size() {
		return this.arguments.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public Object[] toArray() {
		return this.arguments.toArray();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(T[] arg0) {
		return this.arguments.toArray(arg0);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SetAfExtension arg0) {
		if(this.hashCode() < arg0.hashCode())
			return -1;
		if(this.hashCode() > arg0.hashCode())
			return 1;
		return 0;
	}

}
