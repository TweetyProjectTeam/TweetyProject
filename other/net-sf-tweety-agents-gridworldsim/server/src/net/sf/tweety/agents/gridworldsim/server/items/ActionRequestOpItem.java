package net.sf.tweety.agents.gridworldsim.server.items;

import net.sf.tweety.agents.gridworldsim.commons.ActionRequestOp;
import net.sf.tweety.agents.gridworldsim.commons.SocketConnection;

/**
 * This item wraps an {@link ActionRequestOp} and the {@link SocketConnection} of the {@code Agent} (or observer client) who sent it.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class ActionRequestOpItem {

    private final SocketConnection sConnect;
    private final ActionRequestOp requestOp;

    /**
     * Create a new {@code ActionReqeustOpItem}.
     * @param sConnect the {@link SocketConnection} from which the {@link ActionRequestOp} was received
     * @param requestOp the {@link ActionRequestOp} that has been received
     */
    public ActionRequestOpItem(SocketConnection sConnect, ActionRequestOp requestOp) {
        this.sConnect = sConnect;
        this.requestOp = requestOp;
    }

    /**
     * Get the wrapped {@link ActionRequestOp}.
     * @return the {@link ActionRequestOp} of this {@code ActionRequestOpItem}
     */
    public ActionRequestOp getRequestOp() {
        return requestOp;
    }

    /**
     * Get the wrapped {@link SocketConnection}
     * @return the wrappred {@link SocketConnection} of this {@code ActionRequestOpItem}
     */
    public SocketConnection getsConnect() {
        return sConnect;
    }
}
