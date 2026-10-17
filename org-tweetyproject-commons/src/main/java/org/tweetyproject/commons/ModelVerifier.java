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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.commons;

/**
 * Instances of this interface model reasoners that decide whether a given
 * interpretation is one of the (selected) models of a belief base, i.e. they
 * solve the verification problem of their semantics. It complements
 * {@link ModelProvider}, which computes the models.
 *
 * @author Lars Bengel
 *
 * @param <S> the type of formulas
 * @param <B> the type of belief bases
 * @param <T> the type of interpretations
 */
public interface ModelVerifier<S extends Formula, B extends BeliefBase, T extends Interpretation<B, S>> {

	/**
	 * Decides whether the given interpretation is a (selected) model of the belief base.
	 *
	 * @param bbase some belief base
	 * @param model some interpretation
	 * @return true if the interpretation is a model of the belief base
	 */
	public boolean isModel(B bbase, T model);

}
