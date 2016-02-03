/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Interpretation;

/**
 * Provides methods for returning some model (if it exists) of 
 * a set of formulas.
 * 
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas.
 */
public interface ConsistencyWitnessProvider<S extends Formula> {

	/**
	 * If the collection of formulas is consistent this method
	 * returns some model of it or, if it is inconsistent, null.
	 * @return some model of the formulas or null.
	 */
	public Interpretation getWitness(Collection<S> formulas);
	
	/**
	 * If the formula is consistent this method
	 * returns some model of it or, if it is inconsistent, null.
	 * @return some model of the formula or null.
	 */
	public Interpretation getWitness(S formula);
	
	/**
	 * If the belief set is consistent this method
	 * returns some model of it or, if it is inconsistent, null.
	 * @return some model of the belief set or null.
	 */
	public Interpretation getWitness(BeliefSet<S> bs);
}
