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
package org.tweetyproject.comparator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.Formula;


/**
 * This class provides an acceptability interpretation of arguments by assigning
 * them real values. Often larger values indicate more acceptability, but some
 * semantics use other ways of ranking the values.
 * 
 * @author Matthias Thimm
 * @author Anna Gessler
 */
public class NumericalPartialOrder<T extends Formula, R extends BeliefBase> extends GeneralComparator<T, R> implements Map<T, Double> {

	/**
	 * Precision for comparing values.
	 */
	public static double PRECISION = 0.0001;

	/**
	 * The method used for ordering the numerical
	 * values according to acceptability.
	 */
	public SortingType sortingType;

	/**
	 * Possible sorting types for the numerical values.
	 * "Ascending" is the default.
	 * <ul>
	 * <li>{@link #DESCENDING}</li>
	 * <li>{@link #ASCENDING}</li>
	 * <li>{@link #LEXICOGRAPHIC}</li>
	 * </ul>
	 */
	public enum SortingType {
		/**
		 * the largest ranking value is ranked first
		 */
		DESCENDING,
		/**
		 * the smallest ranking value is ranked first
		 */
		ASCENDING,
		/**
		 * the first ranking value when sorted lexicographically is ranked first
		 */
		LEXICOGRAPHIC;
	}

	/** The map used for storing acceptability values */
	private Map<T, Double> objectToValue;

	/**
	 * Creates a new empty numerical argument ranking.
	 */
	public NumericalPartialOrder() {
		this.objectToValue = new HashMap<>();
		this.sortingType = SortingType.ASCENDING;
	}

	/**
	 * Creates a new ranking with the given set of
	 * arguments and the given initial ranking value. 
	 * 
	 * @param args         some set of comparable elements
	 * @param initialvalue an initial value that will be 
	 * assigned to all comparable elements
	 */
	public NumericalPartialOrder(Collection<T> args, double initialvalue) {
		this();
		for (T arg : args)
			this.objectToValue.put(arg, initialvalue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.arg.dung.semantics.ArgumentRanking#
	 * isStrictlyLessOrEquallyAcceptableThan(org.tweetyproject.arg.dung.syntax.Argument,
	 * org.tweetyproject.arg.dung.syntax.Argument)
	 */
	@Override
	public boolean isStrictlyLessOrEquallyAcceptableThan(T a, T b) {
		if (sortingType == SortingType.LEXICOGRAPHIC) {
			BigDecimal bda = new BigDecimal((Double) (this.objectToValue.get(a)));
			BigDecimal bdb = new BigDecimal((Double) (this.objectToValue.get(b)));
			bda = bda.setScale(5, RoundingMode.HALF_UP);
			bdb = bdb.setScale(5, RoundingMode.HALF_UP);
			return (bda.toString().compareTo(bdb.toString()) >= 0.0);
		} else if (sortingType == SortingType.ASCENDING)
			return this.objectToValue.get(b) <= this.objectToValue.get(a) + NumericalPartialOrder.PRECISION;
		else if (sortingType == SortingType.DESCENDING)
			return this.objectToValue.get(a) <= this.objectToValue.get(b) + NumericalPartialOrder.PRECISION;
		else
			throw new IllegalArgumentException("Unknown sorting type " + sortingType);

	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.arg.dung.semantics.AbstractArgumentationInterpretation#toString
	 * ()
	 */
	@Override
	public String toString() {
		return this.objectToValue.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear() {
		this.objectToValue.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object arg0) {
		return this.objectToValue.containsKey(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object arg0) {
		return this.objectToValue.containsValue(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public Set<java.util.Map.Entry<T, Double>> entrySet() {
		return this.objectToValue.entrySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public Double get(Object arg0) {
		return this.objectToValue.get(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.objectToValue.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#keySet()
	 */
	@Override
	public Set<T> keySet() {
		return this.objectToValue.keySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Double put(T arg0, Double arg1) {
		return this.objectToValue.put(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends T, ? extends Double> arg0) {
		this.objectToValue.putAll(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public Double remove(Object arg0) {
		return this.objectToValue.remove(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#size()
	 */
	@Override
	public int size() {
		return this.objectToValue.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#values()
	 */
	@Override
	public Collection<Double> values() {
		return this.objectToValue.values();
	}

	/**
	 * @return the sorting type that is used for ranking values (descending, ascending or sorted
	 *         lexicographically)
	 */
	public SortingType getSortingType() {
		return sortingType;
	}

	/**
	 * Set the sorting type for ranking values. For example, the "ascending" type
	 * means that smaller values signify a higher ranking than bigger values.
	 * 
	 * @param sortingType see {@link org.tweetyproject.arg.rankings.semantics.NumericalPartialOrder#sortingType} for a description of
	 * the available sorting methods
	 */
	public void setSortingType(SortingType sortingType) {
		this.sortingType = sortingType;
	}

	@Override
	public boolean isIncomparable(T a, T b) {
		return (!(isStrictlyLessOrEquallyAcceptableThan(a, b) || isStrictlyLessOrEquallyAcceptableThan(b, a)));
	}

	@Override
	public boolean containsIncomparableArguments() {
		for (T a : this.objectToValue.keySet()) 
			for (T b : this.objectToValue.keySet()) 
				if (this.isIncomparable(a, b)) 
					return true;
		return false;
	}

	@Override
	public boolean satisfies(T formula) throws IllegalArgumentException {
		return false;
	}

	@Override
	public boolean satisfies(R beliefBase) throws IllegalArgumentException {
		return false;
	}
}
