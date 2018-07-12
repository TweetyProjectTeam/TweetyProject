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

/**
 * This rule processes the releasing of an object from the inventory.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class ReleaseObjectRule implements StateTransRule {

    private static final Logger logger = Logger.getLogger(ReleaseObjectRule.class);

    /**
     * Tells if this rule gets triggered by a certain {@link ActionRequest}
     * @param request the request to check if this rule gets triggered
     * @return true if this rule gets triggered, otherwise false
     */
    @Override
    public boolean isTriggered(ActionRequest request) {
        if (request.getRequestOp().getType().equals("release")) {
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

        logger.debug("Starting to process a release request from agent "
                + agent.getName() + " of object " + objectName);

        if (objectName == null || gridWorld.getGridObjectByName(objectName) == null) {
            logger.debug("Missing object parameter or object not found in the GridWorld");
            return;
        }

        GridObject releaseObject = gridWorld.getGridObjectByName(objectName);
        if (!agent.hasInInventory(releaseObject)) {
            logger.debug("Agent " + agent.getName() + " tried to release object " + objectName + " from its inventory without having"
                    + " the object in his inventory. Ignoring the request.");
            return;
        }

        boolean success = gridWorld.moveGridObject(releaseObject, agent.getLocation());

        if (success) {
            logger.debug("Successfully finished to process a release request from agent " + agent.getName() + " for object " + objectName);
        } else {
            logger.debug("Release unsuccessful, because operation would have violated integrity constraint.");
        }

    }

    /**
     * set parameters for this rule
     * @param parameter the parameter to set (supported: diagcorrect = true or false)
     * @param value the value
     */
    @Override
    public void setParameter(String parameter, String value) {
        throw new UnsupportedOperationException("Not supported yet.");

    }
}
