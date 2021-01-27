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
package org.tweetyproject.logics.commons.syntax;

/**
 * A Constant represents an constant object in the world of
 * a logical language. It is implemented as a specialized StringTerm.
 * 
 * @author Tim Janus
 */
public class Constant extends StringTerm {	
	
	/**
	 * Ctor: Creates a new Constant with the given name, uses "Thing"
	 * as sort.
	 * @param name	The name of the Constant
	 */
	public Constant(String name){
		this(name,Sort.THING);
	}
	
	/**
	 * Ctor: Creates a new Constant with the given name and sort
	 * @param name	The name of the Constant
	 * @param sort	The sort of the Constant
	 */
	public Constant(String name, Sort sort){
		super(name, sort);
	}
	
	/**
	 * Copy-Ctor: Creates a deep copy of the given Constant
	 * @param other	The Constant that acts as source for the copy
	 */
	public Constant(Constant other) {
		super(other.value, other.getSort());
	}

	@Override
	public void set(String value) {
		if(value == null || value.length() == 0)
			throw new IllegalArgumentException();
		
		this.value = value;
	}
	
	@Override
	public Constant clone() {
		return new Constant(this);
	}
}
