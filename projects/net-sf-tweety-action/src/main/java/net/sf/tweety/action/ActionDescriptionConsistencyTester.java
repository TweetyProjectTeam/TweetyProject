package net.sf.tweety.action;

import java.util.Set;

import net.sf.tweety.ConsistencyTester;

/**
 * Classes implementing this interface are capable of checking whether a given
 * action description is consistent according to some consistency measurements.
 * 
 * @author Sebastian Homann
 * @author Tim Janus
 */
public interface ActionDescriptionConsistencyTester
  extends ConsistencyTester
{
  
  /**
   * Checks whether the given set of causal rules is consistent.
   * 
   * @param causalRules a set of causal rules.
   * @return true iff the given set of causal rules is consistent.
   */
  boolean isConsistent( Set< CausalLaw > causalRules );
}
