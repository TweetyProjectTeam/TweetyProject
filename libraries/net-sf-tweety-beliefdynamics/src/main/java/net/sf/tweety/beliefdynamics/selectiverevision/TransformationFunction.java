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
package net.sf.tweety.beliefdynamics.selectiverevision;

import net.sf.tweety.commons.*;

/**
 * This interface represents a transformation function for selective revision [Ferme:1999].
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas this transformation function works on.
 */
public interface TransformationFunction<T extends Formula> {

	/**
	 * Transforms the given formula for selective revision.
	 * @param formula some formula.
	 * @return a formula.
	 */
	public T transform(T formula);
}
