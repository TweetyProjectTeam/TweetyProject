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
package net.sf.tweety.agents.gridworldsim.server.statetrans;

import net.sf.tweety.agents.gridworldsim.server.*;
import org.apache.log4j.Logger;
import net.sf.tweety.agents.gridworldsim.commons.*;

/**
 * This class processes move actions
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class MoveAgentRule implements StateTransRule {

    private static final Logger logger = Logger.getLogger(MoveAgentRule.class);
    private boolean diagcorrect;

    /**
     * Constructs a new {@code MoveAgentRule}
     */
    public MoveAgentRule() {
        diagcorrect = false;
    }

    /**
     * Checks if this rule is triggered (which is the case for "lock" and "unlock" {@link ActionRequest}s).
     * @param request the {@link ActionRequest} to check
     * @return true if this rule gets triggered, false otherwise
     */
    @Override
    public boolean isTriggered(ActionRequest request) {
        return request.getRequestOp().getType().equals("move");
    }

    /**
     * Do the transition.
     * @param gridWorld the {@link GridWorld} whose state should be change by this rule
     * @param request the {@link ActionRequest} that triggers this change
     */
    @Override
    public void doTransition(GridWorld gridWorld, ActionRequest request) {
        GridObject obj = request.getAgent();
        String direction = request.getRequestOp().getParameterValue("direction");
        HelperFunctions.isValidDirection(direction);
        logger.debug("Starting to process a move request of agent " + obj.getName() + " to direction " + direction);

        GridObjectLocation newObjLoc = obj.getLocation().neighborLocation(direction);
        boolean success;
        if (diagcorrect && HelperFunctions.isDiagnonal(direction)) {
            success = gridWorld.moveGridObjectWithDiagCorrect(obj, newObjLoc);
        } else {
            success = gridWorld.moveGridObject(obj, newObjLoc);
        }

        if (success) {
            logger.debug("Finished to process a move request of agent " + obj.getName() + " to direction " + direction + ".");
        } else {
            logger.debug("Cannot move agent " + obj.getName() + " to direction " + direction + " because that would violate an integrity constraint.");
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
