package net.sf.tweety.agents.gridworldsim.server;

import net.sf.tweety.agents.gridworldsim.commons.ActionRequestOp;
import net.sf.tweety.agents.gridworldsim.commons.SocketConnection;

/**
 * Objects made from this class represent a login request from an {@link Agent}.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class LoginRequest {

    private final ActionRequestOp requestOp;
    private final SocketConnection sConnect;

    /**
     * Constructs a new {@code LoginRequest}.
     * @param sConnect the {@link SocketConnection} of the request
     * @param requestOp the {@link ActionRequestOp} describing the login data
     */
    public LoginRequest(SocketConnection sConnect, ActionRequestOp requestOp) {
        this.requestOp = requestOp;
        this.sConnect = sConnect;

    }

    /**
     * Get the {@link ActionRequestOp} describing the login data.
     * @return the {@link ActionRequestOp} describing the login data
     */
    public ActionRequestOp getRequestOp() {
        return requestOp;
    }

    /**
     * Get the {@link SocketConnection} of this {@code LoginRequest}
     * @return the {@link SocketConnection} of this {@code LoginRequest}
     */
    public SocketConnection getsConnect() {
        return sConnect;
    }
}
