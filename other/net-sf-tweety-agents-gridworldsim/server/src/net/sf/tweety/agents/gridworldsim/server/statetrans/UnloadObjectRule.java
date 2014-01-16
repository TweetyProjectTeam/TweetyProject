package net.sf.tweety.agents.gridworldsim.server.statetrans;

import net.sf.tweety.agents.gridworldsim.server.ActionRequest;
import net.sf.tweety.agents.gridworldsim.server.Agent;
import net.sf.tweety.agents.gridworldsim.server.GridObject;
import net.sf.tweety.agents.gridworldsim.server.GridObjectLocation;
import net.sf.tweety.agents.gridworldsim.server.GridWorld;
import org.apache.log4j.Logger;

/**
 * This rule realized the unloading a {@link GridObject} from the inventory of another {@link GridObject}, which is not an {@link Agent}.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class UnloadObjectRule implements StateTransRule {

    private static final Logger logger = Logger.getLogger(UnloadObjectRule.class);

    /**
     * Tells if this rule gets triggered by a certain {@link ActionRequest}
     * @param request the request to check if this rule gets triggered
     * @return true if this rule gets triggered, otherwise false
     */
    @Override
    public boolean isTriggered(ActionRequest request) {
        if (request.getRequestOp().getType().equals("unload")) {
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
        String direction = request.getRequestOp().getParameterValue("destination");

        logger.debug("Starting to process a request by agent " + agent.getName() + " to unload " + moverString + " to " + direction + ".");
        GridObject mover = gridWorld.getGridObjectByName(moverString);

        if (mover == null || mover instanceof Agent) {
            logger.debug("Couldn't find object in the grid world.");
            return;
        }

        if (direction == null || (!direction.equals("grid") && !direction.equals("inventory"))) {
            logger.debug("Direction is missing or illegal.");
            return;
        }

        GridObjectLocation moverLocation = gridWorld.getGridObjectLocation(mover);

        if (!moverLocation.isInInventory()) {
            logger.debug("Location of the object is not in an inventory.");
            return;
        }

        GridObject container = moverLocation.getInventoryObject();

        if (container instanceof Agent) {
            logger.debug("This object is contained by an agent, won't let theft happen!");
            return;
        }

        GridObjectLocation containerLocation = gridWorld.getGridObjectLocation(container);
        if (!containerLocation.equals(gridWorld.getGridObjectLocation(agent))) {
            logger.debug("Agent and containing object are not in the same cell.");
            return;
        }


        GridObjectLocation newLocation;
        if (direction.equals("grid")) {
            newLocation = new GridObjectLocation(containerLocation.getX(), containerLocation.getY());
        } else {
            newLocation = new GridObjectLocation(agent);
        }

        boolean success = false;
        success = gridWorld.moveGridObject(mover, newLocation);

        if (success) {
            logger.debug("Unload successful");
        } else {
            logger.debug("Unload unsuccessful, because operation would have violated integrity constraint.");
        }
    }

    @Override
    public void setParameter(String parameter, String value) {
        // no parameters possible
    }
}
