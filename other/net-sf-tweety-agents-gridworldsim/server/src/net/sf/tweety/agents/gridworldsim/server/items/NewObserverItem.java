package net.sf.tweety.agents.gridworldsim.server.items;

import net.sf.tweety.agents.gridworldsim.commons.SocketConnection;
import net.sf.tweety.agents.gridworldsim.server.ObserverLogin;

/**
 * This item wrapped the incident that a new observer client has connected.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class NewObserverItem {

    private final SocketConnection sConnect;
    private final ObserverLogin observerLogin;

    /**
     * Constructs a new {@code NewObserverItem}.
     * @param sConnect the {@link SocketConnection} of the new observer client
     * @param observerLogin the {@link ObserverLogin} login data of the new observer client
     */
    public NewObserverItem(SocketConnection sConnect, ObserverLogin observerLogin) {
        this.sConnect = sConnect;
        this.observerLogin = observerLogin;

    }

    /**
     * Get the {@link SocketConnection} of the new observer client
     * @return the {@link SocketConnection} of the new observer client
     */
    public SocketConnection getsConnect() {
        return sConnect;
    }

    /**
     * Get the login data of the new observer client
     * @return the login data of the new observer client
     */
    public ObserverLogin getObserverLogin() {
        return observerLogin;
    }
}
