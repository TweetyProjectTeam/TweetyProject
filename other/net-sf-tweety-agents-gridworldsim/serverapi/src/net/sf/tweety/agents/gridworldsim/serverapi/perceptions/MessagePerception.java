package net.sf.tweety.agents.gridworldsim.serverapi.perceptions;

/**
 * Objects made from this class represent the perception of a message (private or public).
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class MessagePerception {

    private final String sender;
    private final String receiver;
    private final Object message;
    private final boolean isPublic;

    /**
     * Creates a new {@code MessagePerception} without receiver information (receiver information is only needed for observer clients handling private messages)
     * @param sender the name of the sender of the message
     * @param message the message
     * @param isPublic true if this is a public message, false otherwise
     */
    public MessagePerception(String sender, Object message, boolean isPublic) {
        this.sender = sender;
        this.message = message;
        this.isPublic = isPublic;
        receiver = null;
    }

    /**
     * Creates a new {@code MessagePerception} with receiver information (needed for observer clients, if they should know the receiver of a private message).
     * @param sender the name of the sender of the message
     * @param receiver the name of the receiver of the message
     * @param message the message
     * @param isPublic true if this is a public message, false otherwise
     */
    public MessagePerception(String sender, String receiver, Object message, boolean isPublic) {
        this.sender = sender;
        this.message = message;
        this.isPublic = isPublic;
        this.receiver = receiver;
    }

    /**
     * Checks if this message is a public message.
     * @return true if this is a public message, false if it is a private message
     */
    public boolean isPublic() {
        return isPublic;
    }

    /**
     * Get the message content.
     * @return the message content
     */
    public Object getMessage() {
        return message;
    }

    /**
     * Get the name of the sender of the message.
     * @return the name of the sender of the message
     */
    public String getSender() {
        return sender;
    }

    /**
     * Tell if this message is anonymous, i.e. the sender is not known.
     * @return true if this is an anonymous message, false otherwise
     */
    public boolean isAnonymous() {
        if (sender == null || sender.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Convenience method to always receive a {@code String} representation of the message content, even if it is not a {@code String} or {@code null}
     * @return the {@code String} representation of the message content
     */
    public String getMessageText() {
        if (message == null) {
            return "(null)";
        } else {
            return message.toString();
        }
    }

    /**
     * Get the name of the receiver of this message (can be {@code null}), useful for observer clients.
     * @return the name of the receiver of this message
     */
    public String getReceiver() {
        return receiver;
    }
}
