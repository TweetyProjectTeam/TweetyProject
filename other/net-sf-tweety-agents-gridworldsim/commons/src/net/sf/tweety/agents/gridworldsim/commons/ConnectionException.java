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
