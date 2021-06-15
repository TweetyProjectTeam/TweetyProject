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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.dung.syntax.*;
import org.tweetyproject.commons.AbstractInterpretation;

/**
 * This class implements stratified labelings as in [Thimm, Kern-Isberner, 2013].
 * @author  Sebastian Franke
 */
public class StratifiedLabeling extends AbstractInterpretation<DungTheory,Argument> implements Map<Argument,Integer>{

	/** The actual mapping of arguments to integers. */
	private HashMap<Argument,Integer> map;
	
	/**
	 * Creates a new empty stratified labeling.
	 */
	public StratifiedLabeling(){
		this.map = new HashMap<Argument,Integer>();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear() {
		this.map.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object arg0) {
		return this.map.containsKey(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object arg0) {
		return this.map.containsValue(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public Set<java.util.Map.Entry<Argument, Integer>> entrySet() {
		return this.map.entrySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public Integer get(Object arg0) {
		return this.map.get(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	@Override
	public Set<Argument> keySet() {
		return this.map.keySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Integer put(Argument arg0, Integer arg1) {
		return this.map.put(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends Argument, ? extends Integer> arg0) {
		this.map.putAll(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public Integer remove(Object arg0) {
		return this.map.remove(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	@Override
	public int size() {
		return this.map.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	@Override
	public Collection<Integer> values() {
		return this.map.values();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.Interpretation#satisfies(org.tweetyproject.commons.Formula)
	 */
	@Override
	public boolean satisfies(Argument formula) throws IllegalArgumentException {
		if(this.map.containsKey(formula))
			return this.map.get(formula) == 0;
		throw new IllegalArgumentException("No stratum defined for the given argument.");
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.Interpretation#satisfies(org.tweetyproject.BeliefBase)
	 */
	@Override
	public boolean satisfies(DungTheory beliefBase) throws IllegalArgumentException {
		throw new IllegalArgumentException("Satisfaction of belief bases by extensions is undefined.");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.map.toString();
	}
	
}
