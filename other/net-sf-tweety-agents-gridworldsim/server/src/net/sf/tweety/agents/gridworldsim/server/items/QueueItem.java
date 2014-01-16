package net.sf.tweety.agents.gridworldsim.server.items;

/**
 * Objects made from this class are inserted into the queue of incoming incidents, which is processed by the server. A {@code QueueItem}
 * wraps other items and tells which kind of item it wraps.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class QueueItem {

    private final Object queueObject;
    private final short type;
    public static final short ACTIONREQUESTOP = 1;
    public static final short DISCONNECTED = 2;
    public static final short NEWOBSERVER = 3;
    public static final short STATETRANSITEM = 4;

    /**
     * Creates a new {@code QueueItem}.
     * @param queueObject the {@code Object} to wrap
     * @param type the type of the {@code Object} (see public static final fields for possible values)
     */
    public QueueItem(Object queueObject, short type) {
        this.queueObject = queueObject;
        this.type = type;
    }

    /**
     * Get the wrapped {@code Object}.
     * @return the wrapped {@code Object}
     */
    public Object getQueueObject() {
        return queueObject;
    }

    /**
     * Get the type of the {@code Object} (see public static final fields for possible values)
     * @return the type of the {@code Object} (see public static final fields for possible values)
     */
    public short getType() {
        return type;
    }
}
