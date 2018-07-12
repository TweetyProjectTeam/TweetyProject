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
package net.sf.tweety.math;

import java.util.Collection;
import java.util.Iterator;

/**
 * This class contains a set of closed set (interval) of possible numbers
 * 
 * @author Bastian Wolf
 *
 * @param <S> the (number-)type of the elements in this interval 
 */

public class Interval<S extends Number> implements NumberSet<S> {
	
	/**
	 * 
	 */
	private NumberSet<S> intervalset;
	
	/**
	 * 
	 */
	private S lowerBound;
	
	/**
	 * 
	 */
	private S upperBound;
	
	
	
	public Interval(NumberSet<S> interval){
		this.intervalset = interval;
	}
	
	public Interval(S lower, S upper) {
		this.lowerBound = lower;
		this.upperBound = upper;
	}

	
	/**
	 * 
	 */
	public S getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(S lowerBound) {
		this.lowerBound = lowerBound;
		updateIntervallSetOnChange();
	}

	public S getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(S upperBound) {
		this.upperBound = upperBound;
		updateIntervallSetOnChange();
	}

	public NumberSet<S> getIntervalset() {
		return intervalset;
	}

	public void setIntervalset(NumberSet<S> intervalset) {
		this.intervalset = intervalset;
	}
	
	
	/**
	 * updates interval on changes of its lower or upper bounds 
	 */
	private void updateIntervallSetOnChange(){
		
	}
	
	/**
	 * checks, whether an element is within this interval
	 * @param a
	 */
	public boolean isElementOf(S a){
		
			
		return false;
		
	}

	/**
	 * 
	 */
	@Override
	public boolean add(S e) {
		return intervalset.add(e);
	}

	/**
	 * 
	 */
	@Override
	public boolean addAll(Collection<? extends S> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		intervalset.clear();
	}

	@Override
	public boolean contains(Object o) {
		return intervalset.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<S> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}


}
