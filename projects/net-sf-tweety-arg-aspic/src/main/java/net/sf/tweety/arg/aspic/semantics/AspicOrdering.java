package net.sf.tweety.arg.aspic.semantics;

import net.sf.tweety.arg.aspic.syntax.AspicArgument;

public interface AspicOrdering {

	boolean leq(AspicArgument a, AspicArgument b);
}
