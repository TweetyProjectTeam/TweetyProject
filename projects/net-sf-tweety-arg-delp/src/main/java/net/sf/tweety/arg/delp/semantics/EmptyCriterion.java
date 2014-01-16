package net.sf.tweety.arg.delp.semantics;

import net.sf.tweety.arg.delp.*;
import net.sf.tweety.arg.delp.syntax.*;

/**
 * This class implements the empty criterion to compare two arguments. Using this criterion no two arguments are comparable.
 *
 * @author Matthias Thimm
 *
 */
public class EmptyCriterion extends ComparisonCriterion {

	/* (non-Javadoc)
	 * @see edu.cs.ai.thimm.uacs.defeasiblelogicprogramming.ComparisonCriterion#compare(edu.cs.ai.thimm.uacs.defeasiblelogicprogramming.DeLPArgument, edu.cs.ai.thimm.uacs.defeasiblelogicprogramming.DeLPArgument, edu.cs.ai.thimm.uacs.defeasiblelogicprogramming.DefeasibleLogicProgram)
	 */
	@Override
	public int compare(DelpArgument argument1, DelpArgument argument2, DefeasibleLogicProgram context) {
		return ComparisonCriterion.NOT_COMPARABLE;
	}

}
