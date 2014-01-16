package net.sf.tweety.action.signature;

import java.util.List;

import net.sf.tweety.action.ActionName;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;

/**
 * This class represents an action name. It is implemented as a fol predicate to
 * allow for easy grounding of action descriptions.
 * 
 * @author Sebastian Homann
 */
public class FolActionName
  extends Predicate
  implements ActionName
{
  
  /**
   * Creates a new actionname predicate with the given name and arity.
   * 
   * @param name
   * @param arity
   */
  public FolActionName( String name, int arity )
  {
    super( name, arity );
  }
  
  /**
   * Creates a new actionname predicate with the given name and a list of
   * argument sorts, whose element count equals the arity of this predicate.
   * These arguments are used for grounding.
   * 
   * @param name a name
   * @param arguments a list of arguments (either variables or constants)
   */
  public FolActionName( String name, List< Sort > arguments )
  {
    super( name, arguments );
  }
  
  /**
   * Creates a new action name predicate with the given name and zero-arity.
   * 
   * @param name a name
   */
  public FolActionName( String name )
  {
    super( name );
  }
  
  /*
   * (non-Javadoc)
   * @see net.sf.tweety.logics.firstorderlogic.syntax.FolBasicStructure#toString()
   */
  public String toString() {
    return "action " + this.getName();
  }
}
