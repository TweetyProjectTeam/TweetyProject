package net.sf.tweety.action.transitionsystem;

import net.sf.tweety.action.signature.FolAction;

/**
 * Represents a transition in an action transition system, which is a
 * representation of the execution of an action which causes a state change from
 * a source state to a target state.
 * 
 * @author Sebastian Homann
 */
public class Transition
{
  private State from;
  private State to;
  private FolAction action;
  
  /**
   * Creates a new transition with the given parameters.
   * 
   * @param from the state from which this transition origins.
   * @param action the action that causes this transition.
   * @param to the state representing the consequence of the execution of
   *          action.
   */
  public Transition( State from, FolAction action, State to )
  {
    this.from = from;
    this.to = to;
    this.action = action;
  }
  
  /**
   * Returns the source state of this transition.
   * 
   * @return the source state of this transition.
   */
  public State getFrom()
  {
    return from;
  }
  
  /**
   * Returns the target state of this transition.
   * 
   * @return the target state of this transition.
   */
  public State getTo()
  {
    return to;
  }
  
  /**
   * Returns the action that causes this transition.
   * 
   * @return the action that causes this transition.
   */
  public FolAction getAction()
  {
    return action;
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return "(" + from.toString() + ", " + action.toString() + ", " +
      to.toString() + ")";
  }
}
