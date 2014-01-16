package net.sf.tweety.agents.gridworldsim.server.statetrans;

import net.sf.tweety.agents.gridworldsim.server.ActionRequest;
import net.sf.tweety.agents.gridworldsim.server.GridWorld;
import org.apache.log4j.Logger;
import net.sf.tweety.agents.gridworldsim.server.Agent;
import net.sf.tweety.agents.gridworldsim.server.GridObject;
import net.sf.tweety.agents.gridworldsim.server.GridObjectLocation;

/**
 * State transition rule for a hand over {@link ActionRequest}
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class TransferObjectRule implements StateTransRule {

    private boolean diagcorrect;
    private static final Logger logger = Logger.getLogger(TransferObjectRule.class);

    /**
     * Constructs a new {@code TransferObjectRule}
     */
    public TransferObjectRule() {
        diagcorrect = false;
    }

    /**
     * This rule gets triggered by a "handOver" {@link ActionRequest}. Is this the case for the current request?
     * @param request the {@link ActionRequest} which is being processed
     * @return true if this rule gets triggered, false otherwise
     */
    @Override
    public boolean isTriggered(ActionRequest request) {
        return request.getRequestOp().getType().equals("handOver");
    }

    /**
     * Do the state transition for a hand over {@link ActionRequest}
     * @param gridWorld the {@link GridWorld} whose state should be modified by the operation
     * @param request the {@link ActionRequest} to process
     */
    @Override
    public void doTransition(GridWorld gridWorld, ActionRequest request) {

        logger.debug("Starting to process the hand over request of agent " + request.getAgent().getName());

        Agent requester = request.getAgent();
        String receiverString = request.getRequestOp().getParameterValue("agent");
        String transferObjectString = request.getRequestOp().getParameterValue("object");

        /* Get the necessary objects (including sanity checks) */
        if (receiverString == null || transferObjectString == null) {
            logger.debug("Didn't receive all necessary parameters");
            return;
        }
        Agent receiver = null;
        if (gridWorld.getGridObjectByName(receiverString) instanceof Agent) {
            receiver = (Agent) gridWorld.getGridObjectByName(receiverString);
        }
        GridObject transferObject = gridWorld.getGridObjectByName(transferObjectString);
        if (receiver == null || transferObject == null) {
            logger.debug("Couldn't find both - Agent and GridObject - on the grid");
            return;
        }

        /* check if the giving agent has the object in his inventory*/
        if (!requester.hasInInventory(transferObject)) {
            logger.debug(request.getAgent() + " tried to hand over " + transferObject.getName() + " but it is not in his inventory");
            return;
        }

        /* check if agent locations allow the transfer*/
        GridObjectLocation requesterLocation = gridWorld.getGridObjectLocation(requester);
        GridObjectLocation receiverLocation = gridWorld.getGridObjectLocation(receiver);
        if (!receiverLocation.equals(requesterLocation) && !receiverLocation.isNeighbor(requesterLocation)) {
            logger.debug("Can only hand over objects to agents in the same cell or in a neighboring cell");
            return;
        }

        /* check if the GridCell of the receiver has enough space in case we move to a neighbor */
        if (receiverLocation.isNeighbor(requesterLocation)) {
            if (receiverLocation.getGridCell(gridWorld).getFreeCap() < transferObject.getCapNeed()) {
                logger.debug("The cell of the receiver does not have enough free capacity");
                return;
            }
        }

        /* check if the agent is accpeting the object from the other agent*/
        if (!receiver.isHandOverAccepted(requester, transferObject)) {
            logger.debug("The receiving agent does not accept the object from the giving agent");
            return;
        }

        GridObjectLocation receiverInventory = new GridObjectLocation(receiver);
        boolean success;
        if (diagcorrect) {
            success = gridWorld.moveGridObjectWithDiagCorrect(transferObject, receiverInventory);
        } else {
            success = gridWorld.moveGridObject(transferObject, receiverInventory);
        }

        if (!success) {
            logger.warn("Transfer of object " + transferObject.getName() + " from Agent " + requester.getName() + " to Agent " + receiver.getName() + " failed because it would have violated an integrity constraint.");
        } else {
            logger.warn("Transfer of object " + transferObject.getName() + " from Agent " + requester.getName() + " to Agent " + receiver.getName() + " successful.");
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


        if (parameter.equals("diagcorrect") && valueBoolean) {
            diagcorrect = true;


        }
        if (parameter.equals("diagcorrect") && !valueBoolean) {
            diagcorrect = false;

        }
    }
}
