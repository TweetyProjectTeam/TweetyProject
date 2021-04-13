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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.lp.asp.grounder;

import org.tweetyproject.lp.asp.syntax.Program;

/**
 * This class provides a common interface for asp grounders.
 * For a given asp program, a grounder computes an equivalent 
 * ground program (= equivalent program without variables).
 * 
 * @author Anna Gessler
 */
public abstract class ASPGrounder {
	
	/**
	 * Computes an equivalent ground program for the given program.
	 * 
	 * @param p a program
	 * @return equivalent ground (variable-free) version of p
	 */
	public abstract Program getGroundProgram(Program p);

}
