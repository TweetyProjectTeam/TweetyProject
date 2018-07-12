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
 * Instances of this interface model reasoner that determine the models
 * for a given set of formulas.
 * 
 * @author Matthias Thimm
 *
 * @param <S> the type of formulas
 * @param <T> the type of interpretations
 */
public interface ModelProvider<S extends Formula,T extends Interpretation<S>> {
	
	/** Returns a characterizing model of the given belief base
	 * @param beliefbase some belief base
	 * @return the characterizing model of the belief base
	 */
	public Collection<T> getModels(Collection<S> formulas);
}
