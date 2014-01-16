package net.sf.tweety.logicprogramming.asp.solver;

import java.util.*;

import net.sf.tweety.logicprogramming.asp.syntax.*;


/**
 * a class representing an answer set. 
 * 
 * @author Thomas Vengels
 *
 */
public class AnswerSet extends HashSet<ELPLiteral> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AnswerSet() {
		super();
		weight = 0;
		level = 0;
	}	
	
	public AnswerSet(Set<ELPLiteral> lits, int weight, int level) {
		this.addAll(lits);
		this.weight = weight;
		this.level = level;
	}
	
	public final int				weight;
	public final int				level;
}
