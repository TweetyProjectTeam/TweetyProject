package net.sf.tweety.logics.commons.syntax;

import java.util.*;


/**
 * A functor for logic language, i.e. an identifier for
 * functional terms.
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 */
public class Functor extends TypedStructureAdapter {
	
	/**
	 * The sort of a functional term using this functor
	 */
	private Sort targetSort;
	
	/**
	 * Initializes a functor of arity zero with the given name;
	 * the target sort is initialized to Sort.THING. 
	 * @param name the name of the functor
	 */
	public Functor(String name){
		super(name);
		this.targetSort = Sort.THING;
	}
	/**
	 * Initializes a functor with the given name and of the given arity.
	 * Every argument and the target gets the sort Sort.THING. 
	 * @param name the name of the functor
	 */
	public Functor(String name, int arity){
		super(name,arity);
		this.targetSort = Sort.THING;
	}
	
	/**
	 * Initializes a functor with the given name, the given list
	 * of argument sorts, and the given target sort.
	 * @param name the name of the functor
	 * @param arguments the sorts of the arguments
	 */
	public Functor(String name, List<Sort> arguments, Sort targetSort){
		super(name,arguments);
		this.targetSort = targetSort;
	}
	
	/**
	 * Returns the sort of this functor
	 * @return the sort of this functor
	 */
	public Sort getTargetSort(){
		return this.targetSort;
	}
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((targetSort == null) ? 0 : targetSort.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!super.equals(obj))
			return false;
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;		
		Functor other = (Functor) obj;
		if (targetSort == null) {
			if (other.targetSort != null)
				return false;
		} else if (!targetSort.equals(other.targetSort))
			return false;
		return true;
	}

	@Override
	public Functor clone() {
		return new Functor(this.getName(), copyArgumentTypes(), this.targetSort.clone());
	}
}
