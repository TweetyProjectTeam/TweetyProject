package net.sf.tweety.agents.gridworldsim.server;

/**
 * Objects made from this class represent private messages.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class PrivateMessage extends PublicMessage {

    private final Agent receiver;

    /**
     * Constructs a new {@code PrivateMessage}
     * @param sender the sender of the {@code PrivateMessage}
     * @param receiver the receiver of the {@code PrivateMessage}
     * @param message the message
     */
    public PrivateMessage(Agent sender, Agent receiver, String message) {
        super(sender, message);
        this.receiver = receiver;
    }

    /**
     * Get the receiver of this {@code PrivateMessage}.
     * @return the receiver of this {@code PrivateMessage}
     */
    public Agent getReceiver() {
        return receiver;
    }
}
