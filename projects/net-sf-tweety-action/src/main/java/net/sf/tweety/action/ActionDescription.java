package net.sf.tweety.action;

import java.util.Collection;

import net.sf.tweety.commons.BeliefSet;

/**
 * This class represents an action description as a set of causal laws.
 * 
 * @author Sebastian Homann
 * @param <T> Type of causal law to be kept in this action description.
 */
public abstract class ActionDescription< T extends CausalLaw >
  extends BeliefSet< T >
{
  
  /**
   * Creates a new empty action description.
   */
  public ActionDescription()
  {
    super();
  }
  
  /**
   * Creates a new action description containing all elements in the collection
   * given.
   * 
   * @param c a collection of causal laws.
   */
  public ActionDescription( Collection< ? extends T > c )
  {
    super( c );
  }
  
}
