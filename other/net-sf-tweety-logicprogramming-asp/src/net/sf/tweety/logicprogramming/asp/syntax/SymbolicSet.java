package net.sf.tweety.logicprogramming.asp.syntax;

import java.util.*;

/**
 * this class models a symbolic set, which is
 * a set of local variables over a set of literals.
 * this is a dlv specific extension over the language
 * of extended logical programs, and used by aggregate
 * functions.
 * 
 * @author Thomas Vengels
 *
 */
public class SymbolicSet {
	
	List<String> 		variables = new LinkedList<String>();
	List<ELPLiteral>	literals = new LinkedList<ELPLiteral>();
	
	public SymbolicSet() {
		
	}
	
	public void addVariable(String varname) {
		this.variables.add(varname);
	}
	
	public void addLiteral(ELPLiteral lit) {
		this.literals.add(lit);
	}
	
	public List<String> getVariables() {
		return this.variables;
	}
	
	public List<ELPLiteral> getLiterals() {
		return this.literals;
	}
	
	/**
	 * this method returns a list of literal expressions which
	 * should be added to the body of an elp rule containing
	 * an aggregate with unstratified literals.
	 * @return
	 */
	public List<ELPLiteral> getLiteralGrounding() {
		return null;
	}
	
	@Override
	public String toString() {
		String ret = "{";
		boolean first = true;
		for (String v : variables) {
			if (!first)
				ret += ",";
			ret += v;
			first = false;
		}
		ret += ":";
		first = true;
		for (ELPLiteral l : literals) {
			if (!first)
				ret += ",";
			ret += l;
			first = false;
		}
		ret += "}";
		return ret;
	}
}
