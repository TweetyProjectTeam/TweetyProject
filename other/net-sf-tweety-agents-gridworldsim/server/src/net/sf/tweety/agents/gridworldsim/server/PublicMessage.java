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
