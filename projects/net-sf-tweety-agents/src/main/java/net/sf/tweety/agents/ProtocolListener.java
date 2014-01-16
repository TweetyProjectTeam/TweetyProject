package net.sf.tweety.agents;

/**
 * A protocol listener listens on a protocol and is notified whenever an action is executed.
 * 
 * @author Matthias Thimm
 */
public interface ProtocolListener {

	/**
	 * This method is called when an action has been performed in the given protocol.
	 * @param actionEvent an action event.
	 */
	public void actionPerformed(ActionEvent actionEvent);
	
	/**
	 * This method is called when the protocol terminates.
	 */
	public void protocolTerminated();
}
