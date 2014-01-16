package net.sf.tweety.agents;

/**
 * A protocol gives instructions in which order agents have to be asked
 * for actions in a multi-agent system. 
 * @author Matthias Thimm
 */
public interface Protocol {

	/**
	 * Adds the given listener to this protocol.
	 * @param listener a protocol listener.
	 */
	public void addProtocolListener(ProtocolListener listener);
	
	/**
	 * Removes the given protocol listener from this protocol.
	 * @param listener a protocol listener.
	 * @return "true" if the listener has been removed.
	 */
	public boolean removeProtocolListener(ProtocolListener listener);
}
