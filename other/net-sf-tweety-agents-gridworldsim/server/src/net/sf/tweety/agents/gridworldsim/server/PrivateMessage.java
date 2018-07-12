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
