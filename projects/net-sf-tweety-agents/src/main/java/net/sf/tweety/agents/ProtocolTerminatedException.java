package net.sf.tweety.agents;

/**
 * A ProtocolTerminatedException is thrown when 
 * a protocol is asked to perform a step but has already terminated.
 * 
 * @author Matthias Thimm
 */
public class ProtocolTerminatedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new ProtocolTerminatedException.
	 */
	public ProtocolTerminatedException(){
		super("No further step possible: protocol has already terminated.");
	}

}
