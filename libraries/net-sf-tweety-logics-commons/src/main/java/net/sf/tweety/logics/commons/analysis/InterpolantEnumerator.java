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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;

import net.sf.tweety.commons.Formula;

/**
 * Interface for classes enumerating (Craig) interpolants. Given two consistent
 * sets of formulas K1 and K2, an interpolant I of K1 wrt. K2 is a formula with
 * <ul>
 * <li>I is entailed by K1</li>
 * <li>I and K2 are inconsistent</li>
 * <li>I only uses vocabulary common to both K1 and K2</li>
 * </ul>
 * 
 * @author Matthias Thimm
 * @param <S> the type of formulas
 *
 */
public interface InterpolantEnumerator<S extends Formula> {
	/**
	 * Returns the set of all interpolants of K1 wrt. K2 (modulo semantical
	 * equivalence).
	 * @param k1 some set of formulas
	 * @param k2 some set of formulas
	 * @return the set of all interpolants of K1 wrt. K2 (modulo semantical
	 * equivalence).
	 */
	public Collection<S> getInterpolants(Collection<S> k1, Collection<S> k2);
	
	/**
	 * Returns the strongest interpolant (up to semantical equivalence)
	 * of K1 wrt. K2, i.e., the interpolant IS such that IS entails I for
	 * every other interpolant I.
	 * @param k1 some set of formulas
	 * @param k2 some set of formulas
	 * @return the strongest interpolant of K1 wrt. K2
	 */
	public S getStrongestInterpolant(Collection<S> k1, Collection<S> k2);
	
	/**
	 * Returns the weakest interpolant (up to semantical equivalence)
	 * of K1 wrt. K2, i.e., the interpolant IW such that I entails IW for
	 * every other interpolant I.
	 * @param k1 some set of formulas
	 * @param k2 some set of formulas
	 * @return the weakest interpolant of K1 wrt. K2
	 */
	public S getWeakestInterpolant(Collection<S> k1, Collection<S> k2);
}
