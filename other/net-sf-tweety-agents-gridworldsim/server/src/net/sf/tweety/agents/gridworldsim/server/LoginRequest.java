/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
