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
import net.sf.tweety.agents.gridworldsim.commons.*;

/**
 * State transition rule for a move object action request
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class MoveObjectRule implements StateTransRule {

    private static final Logger logger = Logger.getLogger(MoveObjectRule.class);
    private boolean diagcorrect;

    public MoveObjectRule() {
        diagcorrect = false;
    }

    /**
     * Tells if this rule gets triggered by a certain {@link ActionRequest}
     * @param request the request to check if this rule gets triggered
     * @return true if this rule gets triggered, otherwise false
     */
    @Override
    public boolean isTriggered(ActionRequest request) {
        return request.getRequestOp().getType().equals("moveObject");
    }

    /**
     * Do the transition.
     * @param gridWorld the {@link GridWorld} whose state should be change by this rule
     * @param request the {@link ActionRequest} that triggers this change
     */
    @Override
    public void doTransition(GridWorld gridWorld, ActionRequest request) {
        logger.debug("Starting to process an object move request of agent " + request.getAgent().getName());


        Agent agent = request.getAgent();
        String direction = request.getRequestOp().getParameterValue("direction");
        String objectName = request.getRequestOp().getParameterValue("object");

        if (direction == null || objectName == null) {
            logger.debug("Invalid parameters for a moveObject request");
            return;
        }

        if (!HelperFunctions.isValidDirection(direction)) {
            logger.debug("specified direction was not valid");
            return;
        }

        GridObject obj = gridWorld.getGridObjectByName(objectName);

        if (obj == null || obj instanceof Agent) {
            logger.debug("Object " + objectName + " for which a move request by agent " + agent.getName() + " was received could not be found");
            return;
        }

        GridObjectLocation agentLoc = gridWorld.getGridObjectLocation(agent);
        GridObjectLocation objLoc = gridWorld.getGridObjectLocation(obj);

        if (objLoc.isInInventory()) {
            logger.debug("Object " + objectName + " for which a move request by agent " + agent.getName() + " was received is in some inventory and hence cannot be moved");
            return;
        }

        if (obj.getCapNeed() > agent.getMoveForce()) {
            logger.debug("Object " + objectName + " has a too big capNeed to be moved by agent " + agent.getName());
            return;
        }


        if (agentLoc.equals(objLoc) || objLoc.neighborLocation(direction).equals(agentLoc)) {
            GridObjectLocation newObjLoc = objLoc.neighborLocation(direction);
            boolean success;
            if (diagcorrect && HelperFunctions.isDiagnonal(direction)) {
                success = gridWorld.moveGridObjectWithDiagCorrect(obj, newObjLoc);
            } else {
                success = gridWorld.moveGridObject(obj, newObjLoc);
            }

            if (!success) {
                logger.debug("Moving of object " + objectName + " by agent " + agent.getName() + " failed because it would have violated an integrity constraint.");
            } else {
                logger.debug("Agent " + agent.getName() + " moved object " + objectName + "to cell (" + newObjLoc.getX() + "," + newObjLoc.getY() + ").");
            }
        }
    }

    /**
     * set parameters for this rule
     * @param parameter the parameter to set (supported: diagcorrect = true or false)
     * @param value the value
     */
    @Override
    public void setParameter(String parameter, String value) {
        boolean valueBoolean = Boolean.valueOf(value).booleanValue();
        if (parameter.equals("diagcorrect") && valueBoolean == true) {
            diagcorrect = true;
        }
        if (parameter.equals("diagcorrect") && valueBoolean == false) {
            diagcorrect = false;
        }
    }
}
