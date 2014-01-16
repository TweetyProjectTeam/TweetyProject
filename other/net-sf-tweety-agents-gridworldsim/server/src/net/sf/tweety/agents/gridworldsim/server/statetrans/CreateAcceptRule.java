package net.sf.tweety.agents.gridworldsim.server.statetrans;

import net.sf.tweety.agents.gridworldsim.server.ActionRequest;
import net.sf.tweety.agents.gridworldsim.server.Agent;
import net.sf.tweety.agents.gridworldsim.server.GridObject;
import org.apache.log4j.Logger;

/**
 * Save the declaration of an {@link Agent} to accept {@link GridObject}s from other {@link Agent}s
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class CreateAcceptRule extends AbstractCreateRecallAcceptRule {

    private static final Logger logger = Logger.getLogger(CreateAcceptRule.class);

    /**
     * This rule gets triggered by a "declareAccept" {@link ActionRequest}. Is this the case for the current request?
     * @param request the {@link ActionRequest} which is being processed
     * @return true if this rule gets triggered, false otherwise
     */
    @Override
    public boolean isTriggered(ActionRequest request) {
        return request.getRequestOp().getType().equals("declareAccept");
    }

    /**
     * Update an {@link Agent} to accept a {@link GridObject} from all other {@link Agent}s
     * @param agentToUpdate the {@link Agent} to update
     * @param gridObject the {@link GridObject} to accept from all other {@link Agent}s
     */
    @Override
    protected void updateAllObject(Agent agentToUpdate, GridObject gridObject) {
        agentToUpdate.addAllAcceptObject(gridObject);
        logger.debug("Processed general declaration of acceptance of agent " + agentToUpdate.getName() + " for object " + gridObject.getName());
    }

    /**
     * Update an {@link Agent} to accept all {@link GridObject}s from another {@link Agent}
     * @param agentToUpdate the {@link Agent} to update
     * @param agent the {@link Agent} from which to accept all {@link GridObject}s
     */
    @Override
    protected void updateAllAgent(Agent agentToUpdate, Agent agent) {
        agentToUpdate.addAllAcceptAgent(agent);
        logger.debug("Processed general declaration of acceptance of agent " + agentToUpdate.getName() + " for agent " + agent.getName());
    }

    /**
     * Update an {@link Agent} to accept a {@link GridObject} from another {@link Agent}
     * @param agentToUpdate the {@link Agent} to update
     * @param agent the {@link Agent} from which to accept the {@link GridObject}
     * @param gridObject the {@link GridObject} which to accept from the {@link Agent}
     */
    @Override
    protected void updateHandOver(Agent agentToUpdate, Agent agent, GridObject gridObject) {
        agentToUpdate.addHandOverAccept(agent, gridObject);
        logger.debug("Processed specific declaration of acceptance of agent " + agentToUpdate.getName() + " to receive object " + gridObject.getName() + " from agent " + agent.getName());
    }
}
