package net.sf.tweety.action.transitionsystem;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.action.signature.FolFluentName;
import net.sf.tweety.logics.fol.syntax.FOLAtom;

/**
 * Represents a state in an action transition system, which is a representation
 * of an interpretation of all fluent names in an action description.
 * 
 * @author Sebastian Homann
 */
public class State
{
  private Set< FOLAtom > fluents = new HashSet< FOLAtom >();
  
  /**
   * Creates a new State with a set of fluents that are mapped to true.
   * 
   * @param fluents The fluents which are mapped to true by this state.
   */
  public State( Set< FOLAtom > fluents )
  {
    this.fluents.addAll( fluents );
  }
  
  /**
   * Returns true iff the fluent given is mapped to true by this state.
   * 
   * @param fluent
   * @return true iff the fluent given is mapped to true by this state.
   */
  public boolean isMappedToTrue( FOLAtom fluent )
  {
    if ( fluent.getPredicate() instanceof FolFluentName )
      return fluents.contains( fluent );
    return false;
  }
  
  /**
   * Returns the set of fluent atoms that are mapped to true by this state.
   */
  public Set< FOLAtom > getPositiveFluents()
  {
    return new HashSet< FOLAtom >( fluents );
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object obj )
  {
    return fluents.equals( obj );
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    return fluents.hashCode();
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return fluents.toString();
  }
}
