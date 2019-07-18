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

import java.text.Collator;
import java.util.*;

import net.sf.tweety.logics.commons.syntax.interfaces.TypedStructure;


/**
 * The abstract parent for predicates and functors implementing the 
 * TypedStructure interface. This class implements their common
 * functionalities.
 * 
 * @author Matthias Thimm, Tim Janus
 */
public abstract class TypedStructureAdapter implements TypedStructure, Comparable<TypedStructureAdapter> {
	
	/** The name of this structure */
	private String name;
	
	/**
	 * This list constrains the possible arguments of this structure
	 * to the given sorts. Therefore the arity of this structure is
	 * the size of the list if the structure isComplete().
	 */
	private List<Sort> arguments = new ArrayList<Sort>();
	
	/** the number of arguments for a complete structure */
	private int arity;
	
	/** Default-Ctor: Creating empty typed structure */
	public TypedStructureAdapter() {
		
	}
	
	/**
	 * Initializes a structure of arity zero with the given name; 
	 * @param name the name of the structure
	 */
	public TypedStructureAdapter(String name){
		this.name = name;
	}
	
	/**
	 * Initializes a structure with the given name and of the given arity.
	 * Every argument gets the sort Sort.THING. 
	 * @param name the name of the structure
	 * @param arity the arity of this structure
	 */
	public TypedStructureAdapter(String name, int arity){
		this(name);
		for(int i = 0; i < arity; i++)
			this.arguments.add(Sort.THING);
	}
	
	/**
	 * Initializes a structure with the given name and the given list
	 * of argument sorts.
	 * @param name the name of the structure
	 * @param arguments the sorts of the arguments
	 */
	public TypedStructureAdapter(String name, List<Sort> arguments){
		this(name);
		this.arguments.addAll(arguments);
		this.arity = arguments.size();
	}
	
	@Override
	public String getName(){
		return this.name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int getArity(){
		return this.arguments.size();
	}

	@Override
	public List<Sort> getArgumentTypes(){
		return Collections.unmodifiableList(this.arguments);
	}
	
	@Override
	public void addArgumentType(Sort argType) {
		arguments.add(argType);
	}
	
	@Override
	public Sort removeArgumentType(int index) {
		return arguments.remove(index);
	}
	
	@Override
	public boolean removeArgumentType(Sort argType) {
		return arguments.remove(argType);
	}	
	
	@Override
	public boolean isTyped() {
		for(Sort s : arguments) {
			if(s != Sort.THING)
				return true;
		}
		return false;
	}
	
	@Override
	public boolean isComplete() {
		return arity == arguments.size();
	}

	@Override
	public String toString(){
		String s = this.name;
		Iterator<Sort> it = this.arguments.iterator();
		if(!it.hasNext())
			return s;
		s += "(" + it.next();
		while(it.hasNext())
			s += "," + it.next();
		s += ")";		
		return s;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((arguments == null) ? 0 : arguments.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypedStructureAdapter other = (TypedStructureAdapter) obj;
		if (arguments == null) {
			if (other.arguments != null)
				return false;
		} else if (!arguments.equals(other.arguments))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	protected List<Sort> copyArgumentTypes() {
		List<Sort> argumentTypes = new LinkedList<Sort>();
		for(Sort sort : this.getArgumentTypes()) {
			argumentTypes.add(sort.clone());
		}
		return argumentTypes;
	}
	
	@Override
	public abstract TypedStructure clone();
	
	@Override
	public int compareTo(TypedStructureAdapter o) {
		return Collator.getInstance().compare(name, o.name);
	}
	
}
