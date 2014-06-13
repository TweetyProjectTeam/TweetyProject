/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
package net.sf.tweety.agents.gridworldsim.serverapi;

/**
 * Objects made from this class realize message requests. If the contained {@code Object} is a {@code String}, this {@code String} will be put
 * without transformation to the XML (inside a CDATA section).  If the contained {@code Object} is not a {@code String} is must be
 * serializable and will be serialized and BASE64 encoded before being put to the XML.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class MessageRequest {

    private final String receiver;
    private final Object message;

    /**
     * Creates a new {@code MessageRequest} for a private message.
     * @param receiver the name of the receiver of the message
     * @param message the message content (either a {@code String} or an arbitrary {@code Object} implementing {@code Serializable})
     */
    public MessageRequest(String receiver, Object message) {
        this.receiver = receiver;
        this.message = message;
    }

    /**
     * Creates a new {@code MessageRequest} for a public message.
     * @param message the message content (either a {@code String} or an arbitrary {@code Object} implementing {@code Serializable})
     */
    public MessageRequest(Object message) {
        this.message = message;
        receiver = null;
    }

    /**
     * Says if this {@code MessageRequest} is public or private.
     * @return true if public, false otherwise
     */
    public boolean isPublic() {
        if (receiver == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the message content
     * @return the {@code Object} constituting the message content
     */
    public Object getMessage() {
        return message;
    }

    /**
     * Get the name of the receiver (if this is a private message)
     * @return the name of the receiver ({@code null} if this is a public message)
     */
    public String getReceiver() {
        return receiver;
    }
}
