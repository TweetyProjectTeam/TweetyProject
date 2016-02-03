/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.action.description;

import java.io.IOException;
import java.util.Set;

import net.sf.tweety.action.ActionDescription;
import net.sf.tweety.action.ActionDescriptionConsistencyTester;
import net.sf.tweety.action.description.syntax.CLaw;
import net.sf.tweety.action.signature.ActionSignature;
import net.sf.tweety.action.transitionsystem.State;
import net.sf.tweety.lp.asp.solver.AspInterface;

/**
 * This class is able to check, whether a given action description in the action
 * language C is consistent with regards to one simple consistency requirement:
 * The transition system described by the action description has at least one
 * state. This check is accomplished using the CTransitionSystemCalculator and
 * thus an answer set solver.
 * 
 * @author Sebastian Homann
 * @author Matthias Thimm
 */
public class CActionDescriptionConsistencyTester
  implements ActionDescriptionConsistencyTester<CLaw>
{
  private AspInterface aspsolver;
  
  /**
   * Creates a new consistency tester which will use the given answer set
   * solver.
   * 
   * @param aspsolver
   */
  public CActionDescriptionConsistencyTester( AspInterface aspsolver )
  {
    this.aspsolver = aspsolver;
  }
  
  /**
   * Checks, whether the given action description in the action language C is
   * consistent.
   * 
   * @param actionDescription an action description.
   * @return true iff the action description is consistent.
   */
  public boolean isConsistent( CActionDescription actionDescription )
  {
    CTransitionSystemCalculator tcalc =
      new CTransitionSystemCalculator( aspsolver );
    Set< State > states = null;
    try {
      states =
        tcalc.calculateStates( actionDescription,
          (ActionSignature) actionDescription.getSignature() );
    }
    catch ( IOException e ) {
      e.printStackTrace();
    }
    return !states.isEmpty();
  }
  
  /* (non-Javadoc)
   * @see net.sf.tweety.action.ActionDescriptionConsistencyTester#isConsistent(net.sf.tweety.action.ActionDescription)
   */
  @Override
  public boolean isConsistent(ActionDescription<CLaw> causalRules) {
	  return this.isConsistent(new CActionDescription(causalRules));
  }  
}
