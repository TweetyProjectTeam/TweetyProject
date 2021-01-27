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
package org.tweetyproject.logics.commons.analysis;

import java.util.Collection;

import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.BeliefSet;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.Interpretation;

/**
 * Provides methods for returning some model (if it exists) of 
 * a set of formulas.
 * 
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas.
 * @param <B> the type of belief bases
 */
public interface ConsistencyWitnessProvider<B extends BeliefBase, S extends Formula> {

	/**
	 * If the collection of formulas is consistent this method
	 * returns some model of it or, if it is inconsistent, null.
	 * @param formulas a set of formulas
	 * @return some model of the formulas or null.
	 */
	public Interpretation<B,S> getWitness(Collection<S> formulas);
	
	/**
	 * If the formula is consistent this method
	 * returns some model of it or, if it is inconsistent, null.
	 * @param formula a formula
	 * @return some model of the formula or null.
	 */
	public Interpretation<B,S> getWitness(S formula);
	
	/**
	 * If the belief set is consistent this method
	 * returns some model of it or, if it is inconsistent, null.
	 * @param bs a belief set
	 * @return some model of the belief set or null.
	 */
	public Interpretation<B,S> getWitness(BeliefSet<S,?> bs);
}
