package net.sf.tweety.logics.commons.syntax;

import java.util.Set;

/**
 * This class models concepts used in description logics
 * 
 * @author Bastian Wolf
 *
 */

public class Concept extends Predicate {

	public Concept() {
		// TODO Auto-generated constructor stub
	}

	public Concept(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public Concept(String name, int arity) {
		super(name, arity);
		// TODO Auto-generated constructor stub
	}

	
	public Concept(String name, Set<Individual> arguments) {
		
		// TODO Auto-generated constructor stub
	}

}
