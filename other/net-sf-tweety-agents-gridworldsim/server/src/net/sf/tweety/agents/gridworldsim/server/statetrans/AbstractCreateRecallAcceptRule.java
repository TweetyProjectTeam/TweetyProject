package net.sf.tweety.agents.gridworldsim.server.statetrans;

import net.sf.tweety.agents.gridworldsim.server.ActionRequest;
import net.sf.tweety.agents.gridworldsim.server.GridWorld;
import net.sf.tweety.agents.gridworldsim.server.Agent;
import net.sf.tweety.agents.gridworldsim.server.GridObject;
import org.apache.log4j.Logger;

/**
 * This class contains the code that the {@link CreateAcceptRule} and {@link RemoveAcceptRule} have in common.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public abstract class AbstractCreateRecallAcceptRule implements StateTransRule {

    private static final Logger logger = Logger.getLogger(AbstractCreateRecallAcceptRule.class);

    /**
     * Process the acceptance declaration.
     * @param gridWorld the current {@link GridWorld}
     * @param request the current {@link ActionRequest} that gets processed
     */
    @Override
    public void doTransition(GridWorld gridWorld, ActionRequest request) {
        logger.debug("Starting to process a declare/recall accept request of agent " + request.getAgent().getName());

        String agentString = request.getRequestOp().getParameterValue("agent");
        String gridObjectString = request.getRequestOp().getParameterValue("object");

        /* get the necessary objects */
        Agent agent = null;
        if (agentString != null && gridWorld.getGridObjectByName(agentString) != null
                && gridWorld.getGridObjectByName(agentString) instanceof Agent) {
            agent = (Agent) gridWorld.getGridObjectByName(agentString);
        }

        GridObject gridObject = null;
        if (gridObjectString != null && gridWorld.getGridObjectByName(gridObjectString) != null) {
            gridObject = gridWorld.getGridObjectByName(gridObjectString);
        }


        /* process the case "accept/recall a certain object from all agents" */
        if (agentString == null && gridObjectString != null && agent == null && gridObject != null) {
            updateAllObject(request.getAgent(), gridObject);
            return;
        }

        /* process the case "accept/recall all objects from a certain agent" */
        if (agentString != null && gridObjectString == null && agent != null && gridObject == null
                && agent != request.getAgent()) {
            updateAllAgent(request.getAgent(), agent);
            return;
        }

        /* process the case "accept/recall a specific object from a specific agent" */
        if (agent != null && gridObject != null && agent != request.getAgent()) {
            updateHandOver(request.getAgent(), agent, gridObject);
            return;
        }

        logger.debug("Declare/recall accept request of agent " + request.getAgent().getName() + " did not result in a state change.");
    }

    /**
     * Update the acceptance status of an {@link Agent} regarding a {@link GridObject} from all other {@link Agent}s.
     * @param agentToUpdate the {@link Agent} to update
     * @param gridObject the {@link GridObject} of this status update
     */
    protected abstract void updateAllObject(Agent agentToUpdate, GridObject gridObject);

    /**
     * Update the acceptance status of an {@link Agent} regarding all {@link GridObject}s from another {@link Agent}.
     * @param agentToUpdate the {@link Agent} to update
     * @param agent the {@link Agent} of this status update
     */
    protected abstract void updateAllAgent(Agent agentToUpdate, Agent agent);

    /**
     * Update the acceptance status of an {@link Agent} regarding a {@link GridObject} from another {@link Agent}.
     * @param agentToUpdate the {@link Agent} to update
     * @param agent the {@link Agent} from which to accept the {@link GridObject}
     * @param gridObject the {@link GridObject} of this status update
     */
    protected abstract void updateHandOver(Agent agentToUpdate, Agent agent, GridObject gridObject);

    /**
     * Set up parameters (none supported) for this rule.
     * @param parameter the parameter to set (none supported)
     * @param value the value for that parameter
     */
    @Override
    public void setParameter(String parameter, String value) {
    }

    /**
     * Does this rule get triggered by a given {@link ActionRequest}?
     * @param request the {@link ActionRequest} which is being processed
     * @return true if this rule gets triggered, false otherwise
     */
    @Override
    public abstract boolean isTriggered(ActionRequest request);
}
