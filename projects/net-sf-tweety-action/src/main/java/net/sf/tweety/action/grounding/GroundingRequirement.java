package net.sf.tweety.action.grounding;

import java.util.Map;

import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Variable;

/**
 * This is a common interface for grounding constraints, which have to be met by
 * a grounder when calculating possible applications of constants to variables.
 * Example: 
 *   caused at(X) after go(X) && at(Y) requires X <> Y 
 * Here, variables X and Y are required to have different values.
 * 
 * @author Sebastian Homann
 */
public interface GroundingRequirement
{
  /**
   * This method checks, if an assignment of constants to variables satisfies a
   * given grounding condition.
   * 
   * @param assignment the assignment to be validated.
   * @return true, if the assignment satisfies this requirement.
   */
  public boolean isValid( Map< Variable, Constant > assignment );
}
