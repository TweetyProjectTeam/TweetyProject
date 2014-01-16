package net.sf.tweety.agents.gridworldsim.server.items;

import net.sf.tweety.agents.gridworldsim.commons.SocketConnection;

/**
 * This item wraps a disconnection incident.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class DisconnectedItem {

    private final SocketConnection sConnect;
    private final boolean agent;

    /**
     * Creates a new {@code DisconnectedItem}.
     * @param sConnect the {@link SocketConnection} that got disconnected
     * @param agent true if this {@code DisconnectedItem} was put into the incoming incident queue by an {@code AgentThread}, false if by an {@code ObserverThread}
     */
    public DisconnectedItem(SocketConnection sConnect, boolean agent) {
        this.sConnect = sConnect;
        this.agent = agent;
    }

    /**
     * Get the {@link SocketConnection} that got disconnected.
     * @return the {@link SocketConnection} of this item.
     */
    public SocketConnection getsConnect() {
        return sConnect;
    }

    /**
     * Tells if the disconnection happened for an agent client or observer client
     * @return true if an {@code Agent} disconnected, false if an observer client disconnected
     */
    public boolean isAgent() {
        return agent;
    }
}
