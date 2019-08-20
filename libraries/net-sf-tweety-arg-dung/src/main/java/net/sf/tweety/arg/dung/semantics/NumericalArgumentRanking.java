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
package net.sf.tweety.arg.dung.semantics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.arg.dung.syntax.Argument;

/**
 * This class provides a acceptability interpretation of arguments by assigning
 * them real values where larger values indicate more acceptability.
 * 
 * @author Matthias Thimm
 */
public class NumericalArgumentRanking extends ArgumentRanking implements Map<Argument, Double> {

	/**
	 * Precision for comparing values.
	 */
	public static double PRECISION = 0.00001;

	public SortingType sortingType;

	/**
	 * Possible sorting types for the numerical values.
	 * <li>{@link #DESCENDING}</li>
	 * <li>{@link #ASCENDING}</li>
	 * <li>{@link #LEXICOGRAPHIC}</li>
	 */
	public enum SortingType {
		/**
		 * largest ranking value is ranked first
		 */
		DESCENDING,
		/**
		 * smallest ranking value is ranked first
		 */
		ASCENDING,
		/**
		 * first ranking value when sorted lexicographically is ranked first
		 */
		LEXICOGRAPHIC;
	}

	/** The actual map used for storing acceptability values */
	private Map<Argument, Double> theMap;

	/** Creates a new empty argument ranking */
	public NumericalArgumentRanking() {
		this.theMap = new HashMap<>();
		this.sortingType = SortingType.ASCENDING;
	}

	/**
	 * Creates a new argument ranking. All arguments given are assigned the given
	 * initial value.
	 * 
	 * @param args         some set of arguments
	 * @param initialvalue an initial value for all arguments
	 */
	public NumericalArgumentRanking(Collection<Argument> args, double initialvalue) {
		this();
		for (Argument arg : args)
			this.theMap.put(arg, initialvalue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.dung.semantics.ArgumentRanking#
	 * isStrictlyLessOrEquallyAcceptableThan(net.sf.tweety.arg.dung.syntax.Argument,
	 * net.sf.tweety.arg.dung.syntax.Argument)
	 */
	@Override
	public boolean isStrictlyLessOrEquallyAcceptableThan(Argument a, Argument b) {
		if (sortingType == SortingType.LEXICOGRAPHIC) {
			BigDecimal bda = new BigDecimal((Double) (this.theMap.get(a)));
			BigDecimal bdb = new BigDecimal((Double) (this.theMap.get(b)));
			bda = bda.setScale(5, RoundingMode.HALF_UP);
			bdb = bdb.setScale(5, RoundingMode.HALF_UP);
			return (bda.toString().compareTo(bdb.toString()) >= 0.0);
		} else if (sortingType == SortingType.ASCENDING)
			return this.theMap.get(b) <= this.theMap.get(a) + NumericalArgumentRanking.PRECISION;
		else if (sortingType == SortingType.DESCENDING)
			return this.theMap.get(a) <= this.theMap.get(b) + NumericalArgumentRanking.PRECISION;
		else
			throw new IllegalArgumentException("Unknown sorting type " + sortingType);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.dung.semantics.AbstractArgumentationInterpretation#
	 * getArgumentsOfStatus(net.sf.tweety.arg.dung.semantics.ArgumentStatus)
	 */
	@Override
	public Extension getArgumentsOfStatus(ArgumentStatus status) {
		if (status.equals(ArgumentStatus.IN))
			return new Extension(this.getMaximallyAcceptedArguments(this.keySet()));
		if (status.equals(ArgumentStatus.OUT))
			return new Extension(this.getMinimallyAcceptedArguments(this.keySet()));
		Collection<Argument> undec = new HashSet<>(this.keySet());
		undec.removeAll(this.getMaximallyAcceptedArguments(this.keySet()));
		undec.removeAll(this.getMinimallyAcceptedArguments(this.keySet()));
		return new Extension(undec);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.dung.semantics.AbstractArgumentationInterpretation#toString
	 * ()
	 */
	@Override
	public String toString() {
		return this.theMap.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear() {
		this.theMap.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object arg0) {
		return this.theMap.containsKey(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object arg0) {
		return this.theMap.containsValue(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public Set<java.util.Map.Entry<Argument, Double>> entrySet() {
		return this.theMap.entrySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public Double get(Object arg0) {
		return this.theMap.get(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.theMap.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#keySet()
	 */
	@Override
	public Set<Argument> keySet() {
		return this.theMap.keySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Double put(Argument arg0, Double arg1) {
		return this.theMap.put(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends Argument, ? extends Double> arg0) {
		this.theMap.putAll(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public Double remove(Object arg0) {
		return this.theMap.remove(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#size()
	 */
	@Override
	public int size() {
		return this.theMap.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#values()
	 */
	@Override
	public Collection<Double> values() {
		return this.theMap.values();
	}

	/**
	 * @return sorting type of ranking values (descending, ascending or sorted
	 *         lexicographically)
	 */
	public SortingType getSortingType() {
		return sortingType;
	}

	/**
	 * Set the sorting type for ranking values. For example, the "ascending" type means that
	 * smaller values signify a higher ranking than bigger values.
	 * 
	 * @param order
	 */
	public void setSortingType(SortingType order) {
		this.sortingType = order;
	}

	@Override
	public boolean isIncomparable(Argument a, Argument b) {
		return false;
	}

}
