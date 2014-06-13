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

import net.sf.tweety.agents.gridworldsim.server.ActionRequest;
import net.sf.tweety.agents.gridworldsim.server.Agent;
import net.sf.tweety.agents.gridworldsim.server.GridObject;
import net.sf.tweety.agents.gridworldsim.server.GridObjectLocation;
import net.sf.tweety.agents.gridworldsim.server.GridWorld;
import org.apache.log4j.Logger;

/**
 * This rule realizes the loading of one object into another.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class LoadObjectRule implements StateTransRule {

    private static final Logger logger = Logger.getLogger(LoadObjectRule.class);

    /**
     * Checks if this rule is triggered (which is the case for "load" {@link ActionRequest}s).
     * @param request the {@link ActionRequest} to check
     * @return true if this rule gets triggered, false otherwise
     */
    @Override
    public boolean isTriggered(ActionRequest request) {
        if (request.getRequestOp().getType().equals("load")) {
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
        String moverString = request.getRequestOp().getParameterValue("object");
        String receiverString = request.getRequestOp().getParameterValue("receiver");

        logger.debug("Starting to process a request by agent " + agent.getName() + " to load " + moverString + " into " + receiverString + ".");

        /* get objects and locations */
        GridObject mover = gridWorld.getGridObjectByName(moverString);
        GridObject receiver = gridWorld.getGridObjectByName(receiverString);
        GridObjectLocation agentLocation = gridWorld.getGridObjectLocation(agent);
        GridObjectLocation moverLocation = gridWorld.getGridObjectLocation(mover);
        GridObjectLocation receiverLocation = gridWorld.getGridObjectLocation(receiver);

        /* condition checking */
        if (mover == null || receiver == null || mover instanceof Agent || receiver instanceof Agent) {
            logger.debug("Could not find one of the specificed objects in the gridworld.");
            return;
        }

        if (mover == receiver) {
            logger.debug("Cannot load an object into itself");
            return;
        }

        if (!agentLocation.equals(receiverLocation)) {
            logger.debug("Receiving object and agent are not in the same cell.");
            return;
        }

        if (!moverLocation.equals(agentLocation) && !agent.hasInInventory(mover)) {
            logger.debug("Object to load is neither in the inventory of the agent nor in the same cell as the agent");
            return;
        }

        if (agent.getFreeCap() < mover.getCapNeed() && !agent.hasInInventory(mover)) {
            logger.debug("Agent has not enough free capacity to load object.");
            return;
        }

        GridObjectLocation newLocation = new GridObjectLocation(receiver);

        boolean success = false;
        success = gridWorld.moveGridObject(mover, newLocation);

        if (success) {
            logger.debug("Load successful");
        } else {
            logger.debug("Load unsuccessful, because operation would have violated integrity constraint.");
        }
    }

    /**
     * set the parameters (none supported)
     * @param parameter this rule doesn't support parameters
     * @param value this rule doesn't support parameters
     */
    @Override
    public void setParameter(String parameter, String value) {
        // no parameters possible
    }
}
