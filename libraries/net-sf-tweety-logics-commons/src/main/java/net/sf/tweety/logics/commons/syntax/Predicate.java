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

import java.util.List;

/**
 * A general predicate which contains an identifier that describes its meaning,
 * an argument count referred as arity and a list of sorts defining the types for 
 * the arguments of the predicate.
 * Sub classes might forbid some of the general features to form a predicate which fits
 * another language. Force the arity to be zero gives an propositional predicate for
 * example.
 * 
 * @author Matthias Thimm, Tim Janus
 */
public class Predicate extends TypedStructureAdapter {
	
	public Predicate() {
		super();
	}
	
	/**
	 * Initializes a predicate of arity zero with the given name; 
	 * @param name the name of the predicate
	 */
	public Predicate(String name){
		super(name);	
	}
	/**
	 * Initializes a predicate with the given name and of the given arity.
	 * Every argument gets the sort Sort.THING. 
	 * @param name the name of the predicate
	 * @param arity the arity of the predicate
	 */
	public Predicate(String name, int arity){
		super(name,arity);
	}
	
	/**
	 * Initializes a predicate with the given name and the given list
	 * of argument sorts.
	 * @param name the name of the predicate
	 * @param arguments the sorts of the arguments
	 */
	public Predicate(String name, List<Sort> arguments){
		super(name,arguments);
	}
			
	@Override
	public int hashCode(){
		return super.hashCode()+7;
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj == null) return false;
		if (this == obj)
			return true;		
		if (getClass() != obj.getClass())
			return false;		
		return super.equals(obj);
	}
	
	@Override
	public Predicate clone() {
		return new Predicate(this.getName(), copyArgumentTypes());
	}
}
