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

import net.sf.tweety.agents.gridworldsim.server.ActionRequest;
import net.sf.tweety.agents.gridworldsim.server.Agent;
import net.sf.tweety.agents.gridworldsim.server.GridObject;
import org.apache.log4j.Logger;

/**
 * This rule handle the removal of an acceptance to receive (certain) {@link GridObject}s from (certain) {@link Agent}s.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class RemoveAcceptRule extends AbstractCreateRecallAcceptRule {

    private static final Logger logger = Logger.getLogger(RemoveAcceptRule.class);

    /**
     * This rule gets triggered by a "declareAccept" {@link ActionRequest}. Is this the case for the current request?
     * @param request the {@link ActionRequest} which is being processed
     * @return true if this rule gets triggered, false otherwise
     */
    @Override
    public boolean isTriggered(ActionRequest request) {
        return request.getRequestOp().getType().equals("retractAccept");
    }

    /**
     * Update an {@link Agent} to recall the acceptance of a {@link GridObject} from all other {@link Agent}s
     * @param agentToUpdate the {@link Agent} to update
     * @param gridObject the {@link GridObject} not to generally accept from all other {@link Agent}s anymore
     */
    @Override
    protected void updateAllObject(Agent agentToUpdate, GridObject gridObject) {
        agentToUpdate.removeAllAcceptObject(gridObject);
        logger.debug("Processed recall of general declaration of acceptance of agent " + agentToUpdate.getName() + " for object " + gridObject.getName());
    }

    /**
     * Update an {@link Agent} to recall the acceptance of all {@link GridObject}s from another {@link Agent}
     * @param agentToUpdate the {@link Agent} to update
     * @param agent the {@link Agent} from which not to generally accept all {@link GridObject}s anymore
     */
    @Override
    protected void updateAllAgent(Agent agentToUpdate, Agent agent) {
        agentToUpdate.removeAllAcceptAgent(agent);
        logger.debug("Processed recall of general declaration of acceptance of agent " + agentToUpdate.getName() + " for agent " + agent.getName());
    }

    /**
     * Update an {@link Agent} to recall the acceptance of a {@link GridObject} from another {@link Agent}
     * @param agentToUpdate the {@link Agent} to update
     * @param agent the {@link Agent} from which not to accept the {@link GridObject} anymore
     * @param gridObject the {@link GridObject} which should not be accepted from the {@link Agent} anymore
     */
    @Override
    protected void updateHandOver(Agent agentToUpdate, Agent agent, GridObject gridObject) {
        agentToUpdate.removeHandOverAccept(agent, gridObject);
        logger.debug("Processed recall of specific declaration of acceptance of agent " + agentToUpdate.getName() + " to receive object " + gridObject.getName() + " from agent " + agent.getName());
    }
}
