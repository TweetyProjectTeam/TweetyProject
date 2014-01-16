package net.sf.tweety.action;

import java.util.Collection;

import net.sf.tweety.BeliefSet;
import net.sf.tweety.action.ActionQuery;

/**
 * An Action Query Set consists of action queries in a specific query language
 * and provides some common functionalities for such queries.
 * 
 * @author Sebastian Homann
 * @param <T>
 */
public abstract class ActionQuerySet< T extends ActionQuery >
  extends BeliefSet< T >
{
  
  /**
   * Creates a new ActionQuerySet initialized with the given collection of
   * action queries.
   * 
   * @param c
   */
  public ActionQuerySet( Collection< ? extends T > c )
  {
    super( c );
  }
  
  /**
   * Creates an empty ActionQuerySet
   */
  public ActionQuerySet()
  {
    super();
  }
}
