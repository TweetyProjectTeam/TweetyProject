package net.sf.tweety;

/**
 * This class models a general exception for parsing.
 * 
 * @author Matthias Thimm 
 */
public class ParserException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new parser exception with the given message.
	 * @param message a string.
	 */
	public ParserException(String message){
		super(message);
	}
	
	/**
	 * Creates a new parser exception with the given sub exception.
	 * @param e an exception.
	 */
	public ParserException(Exception e){
		super(e);
	}
}
