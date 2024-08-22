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
package org.tweetyproject.logics.cl.kappa;

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.logics.cl.syntax.Conditional;

/**
 * Represents a Kappa-Value for the c-revision, it contains two {@link KappaMin}
 * instances, both {@link KappaValue} and {@link KappaMin} implement the {@link KappaTerm}
 * interface.
 *
 * Internally the value of the Kappa is represented using an integer and as long as the Kappa
 * cannot be evaluated correctly its value member is -1.
 *
 * @author Tim Janus
 */
public class KappaValue implements KappaTerm {
	/** the index of the kappa, used to differentiate the several kappa values of a c-representation */
	int index;

	/** the current value of the kappa, whereby -1 means it is not evaluated yet */
	int value = -1;

	/**
	 * This minimum contains those kappa sums of conditionals that are falsified in a world, that is verified by the
	 * {@link Conditional} of this kappa value.
	 */
	KappaMin positiveMinimum = new KappaMin();

	/**
	 * This minimum contains those kappa sums of conditionals that are falsified in a world, that is also falsified by the
	 * {@link Conditional} of this kappa value.
	 */
	KappaMin negativeMinimum = new KappaMin();

	/** The Conditional that generates this kappa value */
	Conditional cond;

	/** A flag used to store if this kappa-value is evaluating its value */
	boolean evaluateProcessing;

	/** A flag used to store if this kappa-value is processing its sub-terms */
	static boolean subtermsProcessing;

	/**
	 * Ctor: Generates a kappa value for the given {@link Conditional} with the given index.
	 * @param index an int
	 * @param cond some conditional
	 */
	public KappaValue(int index, Conditional cond) {
		this.index = index;
		this.cond = cond;
	}

	@Override
	public boolean evaluate() {
		if(evaluateProcessing)
			return false;

		evaluateProcessing = true;
		if(positiveMinimum.evaluate() && negativeMinimum.evaluate()) {
			value = 1 + positiveMinimum.value() - negativeMinimum.value();
		}
		evaluateProcessing = false;

		return value != -1;
	}

	@Override
	public int value() {
		return value;
	}

	@Override
	public int greaterEqualThan() {
		return value == -1 ? 1 : value;
	}


	@Override
	public String toString() {
		return value == -1 ? ("K_" + index) : String.valueOf(value);
	}

	/**
	 * Get the full string.
	 * @return String
	 */
	public String fullString() {
		boolean geq = value == -1;
		String reval = "K_" + index + cond.toString();
		if(geq) {
			reval += " >= 1 + " + positiveMinimum + " - " + negativeMinimum;
		} else {
			reval += " = " + String.valueOf(value);
		}
		return reval;
	}

	@Override
	public Set<KappaTerm> getSubTerms() {
		if(subtermsProcessing)
			return new HashSet<KappaTerm>();
		subtermsProcessing = true;
		Set<KappaTerm> reval = new HashSet<KappaTerm>();
		reval.add(positiveMinimum);
		reval.add(negativeMinimum);
		reval.addAll(positiveMinimum.getSubTerms());
		reval.addAll(negativeMinimum.getSubTerms());
		subtermsProcessing = false;
		return reval;
	}
}
