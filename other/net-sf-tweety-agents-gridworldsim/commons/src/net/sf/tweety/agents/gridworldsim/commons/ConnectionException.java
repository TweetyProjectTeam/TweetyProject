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
package net.sf.tweety.agents.gridworldsim.commons;

/**
 * This exception indicates that a connection has been lost.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class ConnectionException extends Exception {

    private final boolean expected;

    /**
     * Creates a new {@code ConnectionException}
     * @param msg a message describing the disconnection
     * @param expected true if the disconnection happened under normal circumstances, false if not
     * @param t the low-level exception (usually an {@code IOException}) that is wrapped by this exception
     */
    public ConnectionException(String msg, boolean expected, Throwable t) {
        super(msg, t);
        this.expected = expected;
    }

    /**
     * Creates a new {@code ConnectionException}
     * @param msg a message describing the disconnection (used for logger output)
     * @param expected true if the disconnected happened under normal circumstances, false if not
     */
    public ConnectionException(String msg, boolean expected) {
        super(msg);
        this.expected = expected;
    }

    /**
     * Tells if the disconnection happened under normal circumstances.
     * @return true if the disconnection happened under normal circumstances, false if not
     */
    public boolean isExpected() {
        return expected;
    }
}
