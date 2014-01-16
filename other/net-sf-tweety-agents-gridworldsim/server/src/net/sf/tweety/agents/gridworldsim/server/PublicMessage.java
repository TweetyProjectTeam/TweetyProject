package net.sf.tweety.agents.gridworldsim.server;

/**
 * Objects made from this class represent public messages.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class PublicMessage {

    private final Agent sender;
    private final String message;

    /**
     * Constructs a new {@code PublicMessage}.
     * @param sender the sender of the {@code PublicMessage}
     * @param message the receiver of the {@code PublicMessage}
     */
    public PublicMessage(Agent sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    /**
     * Gets the message content of this {@code PublicMessage}.
     * @return the message content of this {@code PublicMessage}
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the sender of this {@code PublicMessage}.
     * @return the sender of this {@code PublicMessage}
     */
    public Agent getSender() {
        return sender;
    }
}
