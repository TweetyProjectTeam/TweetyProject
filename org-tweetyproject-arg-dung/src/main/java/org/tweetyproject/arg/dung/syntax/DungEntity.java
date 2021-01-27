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
package org.tweetyproject.arg.dung.syntax;

import org.tweetyproject.arg.dung.ldo.syntax.LdoFormula;
import org.tweetyproject.commons.Formula;

/**
 * This interface captures common methods of arguments and attacks of
 * abstract argumentation theories.
 * 
 * @author Matthias Thimm
 *
 */
public interface DungEntity extends Formula{
	
	/**
	 * Returns a logical representation of this entity in LDO
	 * (Logic of dialectical outcomes, cf. [Hunter, Thimm, 2015])
	 * @return the logical formula of this entity.
	 */
	public LdoFormula getLdoFormula();
}
