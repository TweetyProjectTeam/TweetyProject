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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.commons;

import java.util.Collection;

/**
 * Instances of this interface model reasoners that determine the (selected) models
 * for a given set of formulas.
 * 
 * @author Matthias Thimm
 *
 * @param <S> the type of formulas
 * @param <B> the type of belief bases
 * @param <T> the type of interpretations
 */
public interface ModelProvider<S extends Formula,B extends BeliefBase, T extends Interpretation<B,S>> {
	
	/** Returns a characterizing model of the given belief base
	 * @param bbase some belief base
	 * @return the (selected) models of the belief base
	 */
	public Collection<T> getModels(B bbase);
	
	/**
	 * Returns a single (dedicated) model of the given belief base.
	 * If the implemented method allows for more than one dedicated model,
	 * the selection may be non-deterministic.
	 * @param formulas some belief base
	 * @return a selected model of the belief base.
	 */
	public T getModel(B bbase);
}
