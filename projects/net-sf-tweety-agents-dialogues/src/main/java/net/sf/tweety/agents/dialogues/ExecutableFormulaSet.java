package net.sf.tweety.agents.dialogues;

import java.util.*;

import net.sf.tweety.agents.*;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * This class packs a set of formulas into an executable object.
 * 
 * @author Matthias Thimm
 */
public class ExecutableFormulaSet extends HashSet<PropositionalFormula> implements Executable {

	/** Fpr serialization.  */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new empty set.
	 */
	public ExecutableFormulaSet() {
		super();
	}
	
	/**
	 * Creates a new set for the given formulas.
	 * @param formulas a collection of arguments.
	 */
	public ExecutableFormulaSet(Collection<? extends PropositionalFormula> formulas) {
		super(formulas);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.Executable#isNoOperation()
	 */
	@Override
	public boolean isNoOperation() {
		return this.isEmpty();
	}
}
