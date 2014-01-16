package net.sf.tweety.logicprogramming.asp;

/**
 * This class models a general exception for asp calls.
 * 
 * @author Sebastian Homann.
 */
public class AspException
  extends RuntimeException
{
  
  private static final long serialVersionUID = 1L;
  
  /**
   * Creates a new asp exception with the given message.
   * 
   * @param message a string.
   */
  public AspException( String message )
  {
    super( message );
  }
  
  /**
   * Creates a new asp exception with the given sub exception.
   * 
   * @param e an exception.
   */
  public AspException( Exception e )
  {
    super( e );
  }
}
