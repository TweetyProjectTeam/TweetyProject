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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.reasoner.generator;

import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * 
 * @author Mathias Hofer
 *
 * @param <S> the state
 */
public interface CandidateGenerator<S> {

	public S initialize(AbstractDialecticalFramework adf);

	/**
	 * Does not return the same candidate on two calls on the same instance.
	 * 
	 * @param state
	 * @param adf
	 * @return an interpretation
	 */
	public Interpretation generate(S state, AbstractDialecticalFramework adf);

}
