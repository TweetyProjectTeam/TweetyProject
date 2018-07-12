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
