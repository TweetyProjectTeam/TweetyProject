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
package net.sf.tweety.logics.dl.syntax;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Signature;

/**
 * This abstract class provides the base for terminological and assertional axioms
 * 
 * @author Bastian Wolf
 *
 */

public abstract class Axiom implements Formula {

	public Axiom() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

}
