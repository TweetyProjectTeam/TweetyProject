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

/**
 * Objects made from this class represent the declaration of an {@link Agent} to accept a specific {@link GridObject} from a specific {@link Agent}.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public final class HandOverAccept {

    private final Agent agent;
    private final GridObject gridObject;

    /**
     * Constructs a new {@code HandOverAccept}.
     * @param agent the {@link Agent} from which to accept receiving the {@link GridObject}
     * @param gridObject the {@link GridObject} which to accept from the {@link Agent}
     */
    public HandOverAccept(Agent agent, GridObject gridObject) {
        this.agent = agent;
        this.gridObject = gridObject;
    }

    /**
     * Get the {@link Agent} of this {@code HandOverAccept}.
     * @return the {@link Agent} from which to accept receiving the {@link GridObject}
     */
    public Agent getAgent() {
        return agent;
    }

    /**
     * Get the {@link GridObject} of this {@code HandOverAccept}
     * @return the {@link GridObject} which to accept from the {@link Agent}
     */
    public GridObject getGridObject() {
        return gridObject;
    }

    /**
     * Checks if this {@code HandOverAccept} is equal to another {@code Object}.
     * @param obj the {@code Object} to check equality for
     * @return true it is equal, otherwise false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || this.getClass() != obj.getClass()) {
            return false;
        }

        HandOverAccept checkMe = (HandOverAccept) (obj);


        return ((agent == checkMe.getAgent()) || (agent != null && agent.equals(checkMe.getAgent()))
                && (gridObject == checkMe.getGridObject()) || (gridObject != null && gridObject.equals(checkMe.getGridObject())));
    }

    /**
     * Returns a hash code value for this {@code Object}.
     * @return a hash code value for this {@code Object}
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (null == agent ? 0 : agent.hashCode());
        hash = 67 * hash + (null == gridObject ? 0 : gridObject.hashCode());
        return hash;
    }
}
