package net.sf.tweety.arg.delp.semantics;

import net.sf.tweety.arg.delp.*;
import net.sf.tweety.arg.delp.syntax.*;

/**
 * This class is the superclass for all comparison criterions between two arguments in defeasible logic programming.
 *
 * @author Matthias Thimm
 *
 */
public abstract class ComparisonCriterion {

	/**
	 * relation indicator "IS_BETTER", may be returned by the <source>compare</source> method
	 */
	public final static int IS_BETTER = 0;
	/**
	 * relation indicator "NOT_COMPARABLE", may be returned by the <source>compare</source> method
	 */
	public final static int NOT_COMPARABLE = 1;
	/**
	 * relation indicator "IS_WORSE", may be returned by the <source>compare</source> method
	 */
	public final static int IS_WORSE = 2;
	/**
	 * relation indicator "IS_EQUAL", may be returned by the <source>compare</source> method
	 */
	public final static int IS_EQUAL = 3;

	/**
	 * This method returns the relation of <source>argument1</source> to <source>argument2</source> given <source>context</source>.
	 * @param argument1 a DeLP argument
	 * @param argument2 a DeLP argument
	 * @param context a defeasible logic program as context
	 * @return
	 * 	<br>- ComparisonCriterion.IS_BETTER iff <source>argument1</source> is better than <source>argument2</source>
	 *  <br>- ComparisonCriterion.IS_WORSE iff <source>argument1</source> is worse than <source>argument2</source>
	 *  <br>- ComparisonCriterion.IS_EQUAL iff <source>argument1</source> and <source>argument2</source> are in the same equivalence class
	 *  <br>- ComparisonCriterion.NOT_COMPARABLE iff <source>argument1</source> and <source>argument2</source> are not comparable
	 */
	public abstract int compare(DelpArgument argument1, DelpArgument argument2, DefeasibleLogicProgram context);
}
