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
