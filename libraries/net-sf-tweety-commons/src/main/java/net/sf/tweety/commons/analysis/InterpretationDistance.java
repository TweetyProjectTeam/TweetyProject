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
package net.sf.tweety.commons.analysis;

import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Interpretation;

/**
 * Classes implementing this interface represent distance functions
 * between two interpretations.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The actual type of interpretation used
 * @param <B> the type of belief bases
 * @param <S> the type of formulas
 */
public interface InterpretationDistance<T extends Interpretation<B,S>,B extends BeliefBase, S extends Formula> {

	/**
	 * Measures the distance between the two given interpretations.
	 * @param a some interpretation
	 * @param b some interpretation
	 * @return the distance between the two given interpretations.
	 */
	public double distance(T a, T b);
	
	/**
	 * Measures the distance between a formula and some
	 * interpretation by taking the minimal distance from all models
	 * of the formula to the given interpretation.
	 * @param f some formula
	 * @param b some interpretation.
	 * @return the distance between the set of models of the formula to the
	 * 	given interpretation.
	 */
	public double distance(S f, T b);
}
