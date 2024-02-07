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
package org.tweetyproject.arg.dung.semantics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;




/**
 * This class models a labeling of an abstract argumentation framework, i.e.
 * a function mapping arguments to values "in", "out", "undec".
 *
 * @author Matthias Thimm
 */
public class Labeling extends AbstractArgumentationInterpretation implements Map<Argument,ArgumentStatus> {

	/** The actual labeling. */
	private Map<Argument,ArgumentStatus> labeling;

	/**
	 * Creates a new labeling.
	 */
	public Labeling(){
		this.labeling = new HashMap<Argument,ArgumentStatus>();
	}

	/**
	 * Creates a new labeling from the given extension wrt. the given theory (this only gives
	 * a valid labeling wrt. some semantics if the semantics is admissibility-based).
	 * @param theory some Dung theory.
	 * @param ext an extension
	 */
	public Labeling(ArgumentationFramework<Argument> theory, Extension<? extends ArgumentationFramework<Argument>> ext){
		this();
		for(Argument a: ext)
			this.labeling.put(a, ArgumentStatus.IN);
		if(!theory.containsAll(ext))
			throw new IllegalArgumentException("The arguments of the given extension are not all in the given theory.");
		Extension<ArgumentationFramework<Argument>> ext2 = new Extension<ArgumentationFramework<Argument>>();
		for(Argument a: theory.getNodes()){
			if(!ext.contains(a))
				if(theory.isAttacked(a, ext))
					ext2.add(a);
		}
		for(Argument a: ext2)
			this.labeling.put(a, ArgumentStatus.OUT);
		for(Argument a: theory.getNodes())
			if(!this.labeling.containsKey(a))
				this.labeling.put(a, ArgumentStatus.UNDECIDED);
	}






	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object arg0) {
		return this.labeling.containsKey(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object arg0) {
		return this.labeling.containsValue(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public Set<java.util.Map.Entry<Argument, ArgumentStatus>> entrySet() {
		return this.labeling.entrySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public ArgumentStatus get(Object arg0) {
		return this.labeling.get(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	@Override
	public Set<Argument> keySet() {
		return this.labeling.keySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public ArgumentStatus put(Argument arg0, ArgumentStatus arg1) {
		return this.labeling.put(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends Argument, ? extends ArgumentStatus> arg0) {
		this.labeling.putAll(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	@Override
	public Collection<ArgumentStatus> values() {
		return this.labeling.values();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear() {
		this.labeling.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.labeling.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public ArgumentStatus remove(Object arg0) {
		return this.labeling.remove(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	@Override
	public int size() {
		return this.labeling.size();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.argumentation.dung.semantics.AbstractArgumentationInterpretation#getArgumentsOfStatus(org.tweetyproject.argumentation.dung.semantics.ArgumentStatus)
	 */
	@Override
	public Extension<? extends ArgumentationFramework<Argument>> getArgumentsOfStatus(ArgumentStatus status) {
		Extension<ArgumentationFramework<Argument>> ext = new Extension<ArgumentationFramework<Argument>>();
		for(Argument a: this.labeling.keySet())
			if(this.labeling.get(a).equals(status))
				ext.add(a);
		return ext;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.argumentation.dung.semantics.AbstractArgumentationInterpretation#toString()
	 */
	@Override
	public String toString() {
		return this.labeling.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((labeling == null) ? 0 : labeling.hashCode());
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
		Labeling other = (Labeling) obj;
		if (labeling == null) {
			if (other.labeling != null)
				return false;
		} else if (!labeling.equals(other.labeling))
			return false;
		return true;
	}

}
