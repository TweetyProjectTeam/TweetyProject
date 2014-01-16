package net.sf.tweety.arg.deductive.semantics;

import java.util.Collection;

import net.sf.tweety.graphs.Node;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * Instances of this class represent nodes in the compilation of
 * a knowledge base, i.e. minimal inconsistent subsets of the
 * knowledge base.
 * 
 * @author Matthias Thimm
 */
public class CompilationNode extends PlBeliefSet implements Node {
	
	/**
	 * Creates a new compilation node with the given
	 * set of formulas.
	 * @param formulas a set of formulas.
	 */
	public CompilationNode(Collection<? extends PropositionalFormula> formulas){
		super(formulas);
	}
}
