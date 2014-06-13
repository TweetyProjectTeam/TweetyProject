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
package net.sf.tweety.agents.gridworldsim.server;

import net.sf.tweety.agents.gridworldsim.commons.*;

/**
 * Objects made from this class capsulate a {@link ActionRequestOp} received from an agent and the corresponding {@code Agent} object.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class ActionRequest implements Comparable {

    private final Agent agent;
    private final ActionRequestOp requestOp;

    /**
     * Construct a new {@code ActionRequest}.
     * @param agent the {@link Agent}, who sent the {@link ActionRequestOp}
     * @param requestOp the {@link ActionRequestOp} of the {@link Agent}
     */
    public ActionRequest(Agent agent, ActionRequestOp requestOp) {
        this.agent = agent;
        this.requestOp = requestOp;
    }

    /**
     * Returns the {@link Agent} of this {@code ActionRequest}.
     * @return the {@link Agent} of this {@code ActionRequest}
     */
    public Agent getAgent() {
        return agent;
    }

    /**
     * Return the {@link ActionRequestOp} of this {@code ActionRequest}.
     * @return the {@link ActionRequestOp} of this {@code ActionRequest}
     */
    public ActionRequestOp getRequestOp() {
        return requestOp;
    }

    /**
     * Compares this {@code ActionRequest} to another. The comparison is done according to the
     * priority of the {@link Agent} of this {@code ActionRequest}, if the object to compare to is
     * also an {@code ActionRequest}.
     * @param o the {@code Object} to compare to
     * @return the difference between the priority of the agent of this {@code ActionRequest} and the other, 0 if the other {@code Object} is not an {@code ActionRequest}
     */
    @Override
    public int compareTo(Object o) {
        if (o instanceof ActionRequest) {
            ActionRequest compareRequest = (ActionRequest) o;
            return agent.getPriority() - compareRequest.getAgent().getPriority();
        } else {
            return 0;
        }
    }
}
