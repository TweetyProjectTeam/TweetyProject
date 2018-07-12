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
package net.sf.tweety.logics.commons.syntax;


import java.util.Set;

import net.sf.tweety.commons.util.Pair;

/**
 * This class implements a role used in description logics
 * Every role is an predicate consisting of two individuals
 * @author Bastian Wolf
 *
 */

public class Role extends Predicate {
	
	public Role(){
		super();
	}
	
	public Role(String name){
		super(name, 2);
	}
	
//	public Role(Strin)
	
	
	public Role(String name, Set<Pair<Individual, Individual>> roles){ // arity = 2
		
	}
}
