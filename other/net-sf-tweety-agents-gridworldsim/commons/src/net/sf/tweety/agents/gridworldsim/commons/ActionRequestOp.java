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

import java.util.Map;
import java.util.HashMap;

/**
 * Objects made from this class are a data structure for an agent action request operation, i.e. the action request
 * without the sending agent (which will be determined by the server automatically or is implicitly clear for clients).
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class ActionRequestOp {

    private final String type;
    private Map<String, String> parameters;
    private String internalState;

    /**
     * Construct a new {@code ActionRequestOp}
     * @param type the type of the {@code ActionRequestOp}
     */
    public ActionRequestOp(String type) {
        this.type = type;
        parameters = new HashMap<String, String>();
    }

    /**
     * Get the value of a given parameter.
     * @param parameter the parameter to get the value for
     * @return the value of the given parameter
     */
    public String getParameterValue(String parameter) {
        return parameters.get(parameter);
    }

    /**
     * Get the {@code Map} of all parameters of this {@code ActionRequestOp}.
     * @return the map of all parameters of this {@code ActionRequestOp}
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Get the type of this {@code ActionRequestOp}.
     * @return the type of this {@code ActionRequestOp}
     */
    public String getType() {
        return type;
    }

    /**
     * Add a new parameter to this {@code ActionRequestOp}
     * @param parameter the parameter to add
     */
    public void addParameter(String parameter, String value) {
        parameters.put(parameter, value);
    }

    /**
     * Get the (optional) internal state that an Agent has at the time of sending this {@code ActionRequestOp}.
     * @return the (optional) internal state that an Agent has at the time of sending this {@code ActionRequestOp}
     */
    public String getInternalState() {
        return internalState;
    }

    /**
     * Set the (optional) internal state that an Agent has at the time of sending this {@code ActionRequestOp}.
     * @param internalstate the (optional) internal state that an Agent has at the time of sending this {@code ActionRequestOp}
     */
    public void setInternalState(String internalstate) {
        this.internalState = internalstate;
    }

    /**
     * Tells if this {@code ActionRequestOp} contains an internal state
     * @return true if this {@code ActionRequestOp} contains an internal state, false otherwise
     */
    public boolean hasInternalState() {
        return !(internalState == null);
    }
}
