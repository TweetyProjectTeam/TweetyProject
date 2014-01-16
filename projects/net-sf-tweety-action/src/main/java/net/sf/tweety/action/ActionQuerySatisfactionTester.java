package net.sf.tweety.action;

import java.util.Set;

import net.sf.tweety.BeliefBase;
import net.sf.tweety.action.transitionsystem.TransitionSystem;

/**
 * Classes implementing this interface are capable of checking whether a
 * transition system satisfies an action query.
 * 
 * @author Sebastian Homann
 */
public interface ActionQuerySatisfactionTester
{
  /**
   * Checks whether the given transition system satisfies the given action
   * queries.
   * 
   * @param transitionSystem the transition system, that will be checked for
   *          satisfaction.
   * @param actionQuery a belief base containing action queries, all of which
   *          have to be satisfied by the transition system.
   * @return true iff the transition system satisfies all action queries in the
   *         given belief base.
   */
  public boolean isSatisfied( TransitionSystem transitionSystem,
    BeliefBase actionQuery );
  
  /**
   * Checks whether the given transition system satisfies the given action
   * queries.
   * 
   * @param transitionSystem the transition system, that will be checked for
   *          satisfaction.
   * @param actionQuery a set of action queries, which have to be satisfied by
   *          the transition system.
   * @return true iff the transition system satisfies all action queries in the
   *         given set.
   */
  public boolean isSatisfied( TransitionSystem transitionSystem,
    Set< ActionQuery > actionQuery );
}
