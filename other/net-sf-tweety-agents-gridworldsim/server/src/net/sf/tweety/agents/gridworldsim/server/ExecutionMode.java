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
 * Objects made from this class represent an execution mode for state transition.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class ExecutionMode {

    /**
     * Only do state transition if an {@link ActionRequest} has been received from all active {@link Agent}s.
     */
    public static final short WAIT_FOR_ALL = 0;
    /**
     * Do state transition immediately when something has happened.
     */
    public static final short ON_DEMAND = 1;
    /**
     * Do state transition given a fixed time interval.
     */
    public static final short FIXED_TIME = 2;
    /**
     * Only do state transition if an observer client has explicitly requested that.
     */
    public static final short MANUAL = 3;
    private final short mode;
    private final int timeout;

    /**
     * Creates a new {@code ExecutionMode}
     * @param modeString the XML element name indicating the execution mode ("waitforall", "ondemand", "fixed" or "manual")
     * @param timeout the timeout (optional in case of "wait for all") or interval (required in case of "fixed time")
     */
    public ExecutionMode(String modeString, int timeout) {
        if (modeString.equals("waitforall")) {
            mode = WAIT_FOR_ALL;
        } else if (modeString.equals("ondemand")) {
            mode = ON_DEMAND;
        } else if (modeString.equals("fixed")) {
            mode = FIXED_TIME;
        } else if (modeString.equals("manual")) {
            mode = MANUAL;
        } else {
            mode = ON_DEMAND;
        }

        this.timeout = timeout;
    }

    /**
     * Get the execution mode.
     * @return the execution mode
     */
    public short getMode() {
        return mode;
    }

    /**
     * Get the timeout (irrelevant for "ondemand" or "manual".
     * @return the timeout (0 means "no timeout")
     */
    public int getTimeout() {
        return timeout;
    }
}
