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
package org.tweetyproject.logics.pl.semantics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.commons.AbstractInterpretation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * A fuzzy interpretation for propositional logic.
 * @author Matthias Thimm
 *
 */
public class FuzzyInterpretation extends AbstractInterpretation<PlBeliefSet,PlFormula> implements Map<Proposition,Double>{

	/** Maps the propositions to their values. */
	private Map<Proposition,Double> values;
	
	/**
	 * Creates a new fuzzy interpretation
	 */
	public FuzzyInterpretation(){
		this.values = new HashMap<Proposition,Double>();
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.Interpretation#satisfies(org.tweetyproject.commons.Formula)
	 */
	@Override
	public boolean satisfies(PlFormula formula) throws IllegalArgumentException {
		throw new UnsupportedOperationException("This operation is not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.Interpretation#satisfies(org.tweetyproject.commons.BeliefBase)
	 */
	@Override
	public boolean satisfies(PlBeliefSet beliefBase) throws IllegalArgumentException {
		throw new UnsupportedOperationException("This operation is not implemented yet");
	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear() {
		this.values.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object arg0) {
		return this.values.containsKey(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object arg0) {
		return this.values.containsValue(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public Set<java.util.Map.Entry<Proposition, Double>> entrySet() {
		return this.values.entrySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public Double get(Object arg0) {
		return this.values.get(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.values.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	@Override
	public Set<Proposition> keySet() {
		return this.values.keySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Double put(Proposition arg0, Double arg1) {
		return this.values.put(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends Proposition, ? extends Double> arg0) {
		this.values.putAll(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public Double remove(Object arg0) {
		return this.values.remove(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	@Override
	public int size() {
		return this.values.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	@Override
	public Collection<Double> values() {
		return this.values.values();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */	
	public String toString(){
		return this.values.toString();
	}
}
