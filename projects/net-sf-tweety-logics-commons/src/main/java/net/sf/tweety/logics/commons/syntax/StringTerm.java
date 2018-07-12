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

/**
 * This class represents terms which are objects identified by a
 * string. Subclasses are Variable and Constant.
 * 
 * @author Tim Janus
 * @author Thomas Vengels
 */
public abstract class StringTerm extends TermAdapter<String> {

	/** the value of the term */
	protected String value;

	/** 
	 * Ctor: Creates a string term with the given String as value, uses the
	 * Sort "Thing"
	 * @param value	The value for the string term.
	 */
	public StringTerm(String value) {
		super(value);
	}
	
	/**
	 * Ctor: Create a string term with the given value and sort.
	 * @param value	The value of for the string term.
	 * @param sort	The sort representing the type of the StringTerm.
	 */
	public StringTerm(String value, Sort sort) {
		super(value, sort);
	}
	
	/** 
	 * Copy-Ctor: Creates a deep copy of the StringTerm
	 * @param other	The StringTerm that acts as source for the copy
	 */
	public StringTerm(StringTerm other) {
		super(other.value, other.getSort());
	}
	
	@Override
	public abstract void set(String value);

	@Override
	public String get() {
		return value;
	}
	
	@Override
	public String toString(){
		return this.value;
	}
}
