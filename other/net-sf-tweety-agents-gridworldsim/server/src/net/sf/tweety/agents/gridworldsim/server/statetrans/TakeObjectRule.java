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
package net.sf.tweety.agents.gridworldsim.server.statetrans;

import net.sf.tweety.agents.gridworldsim.server.*;
import org.apache.log4j.Logger;

/**
 * This rule processes the taking of an object to the inventory
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class TakeObjectRule implements StateTransRule {

    private static final Logger logger = Logger.getLogger(TakeObjectRule.class);

    /**
     * This rule gets triggered by a "declareAccept" {@link ActionRequest}. Is this the case for the current request?
     * @param request the {@link ActionRequest} which is being processed
     * @return true if this rule gets triggered, false otherwise
     */
    @Override
    public boolean isTriggered(ActionRequest request) {
        if (request.getRequestOp().getType().equals("take")) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * Do the transition.
     * @param gridWorld the {@link GridWorld} whose state should be change by this rule
     * @param request the {@link ActionRequest} that triggers this change
     */
    @Override
    public void doTransition(GridWorld gridWorld, ActionRequest request) {

        Agent agent = request.getAgent();
        String objectName = request.getRequestOp().getParameterValue("object");

        logger.debug("Starting to process a take request from agent " + agent.getName() + " of object " + objectName);

        /* get the GridObject to take*/
        if (gridWorld.getGridObjectByName(objectName) == null) {
            logger.debug("Agent " + agent.getName() + " tried to take object " + objectName + ", but the requested object doesn't exist."
                    + " Igorning the request.");
            return;
        }

        GridObject obj = gridWorld.getGridObjectByName(objectName);

        /* check if the agent is in the same location as the object*/
        if (!obj.getLocation().equals(agent.getLocation())) {
            logger.debug(agent.getName() + " is not in the same location as " + obj.getName() + ". Ignoring the agent's request to take the object.");
            return;
        }

        /* check if the object to take is a non-agent object*/
        if (obj instanceof Agent) {
            logger.debug("Agent " + agent.getName() + " tries to take " + obj.getName() + " which is an agent, not a non-agent object. Ignoring the request.");
            return;
        }

        /* put the object to the inventory */
        boolean success = false;
        success = gridWorld.moveGridObject(obj, new GridObjectLocation(agent));

        if (success) {
            logger.debug("Successfully finished to process a take request from agent " + agent.getName() + " of object " + objectName);
        } else {
            logger.debug("Take unsuccessful, because operation would have violated integrity constraint.");
        }
    }

    /**
     * Set up parameters (none supported) for this rule
     * @param parameter the parameter to set (none supported)
     * @param value the value for that parameter
     */
    @Override
    public void setParameter(String parameter, String value) {
    }
}
