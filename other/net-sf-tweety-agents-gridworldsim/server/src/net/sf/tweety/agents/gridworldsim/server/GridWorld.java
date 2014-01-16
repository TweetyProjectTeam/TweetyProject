package net.sf.tweety.agents.gridworldsim.server;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import net.sf.tweety.agents.gridworldsim.server.statetrans.StateTransRule;
import java.util.List;
import java.util.Random;
import net.sf.tweety.agents.gridworldsim.server.factories.AgentFactory;
import org.apache.log4j.Logger;

/**
 * Object made from this class represent a GridWorld.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class GridWorld {

    private final GridCell[][] gridCells;
    private final int xDimension;
    private final int yDimension;
    private final Map<GridObject, GridObjectLocation> gridObjectToLocation;
    private final Map<String, GridObject> nameToGridObject;
    private final List<StateTransRule> rules;
    private final Collection<PublicMessage> pubMessages;
    private final Collection<PrivateMessage> privMessages;
    private final boolean msgSightReq;
    private final ExecutionMode execMode;
    private static final Logger logger = Logger.getLogger(GridWorld.class);
    private AgentFactory agentFactory;

    /**
     * Constructs a new {@code GridWorld}.
     * @param xDimension the x dimension of this {@code GridWorld}
     * @param yDimension the y dimension of this {@code GridWorld}
     * @param freeCapDefault the free capacity default for {@link GridCell}'s of this {@code GridWorld}
     * @param rules the {@link StateTransRule}s that realize state transitions for this {@code GridWorld}
     * @param msgSightReq set if an {@link Agent} needs to see the sender of a public message to know it
     * @param execMode the {@link ExecutionMode} for this {@code GridWorld}
     */
    public GridWorld(int xDimension, int yDimension, int freeCapDefault, List<StateTransRule> rules, boolean msgSightReq, ExecutionMode execMode) {
        gridObjectToLocation = new HashMap<GridObject, GridObjectLocation>();
        nameToGridObject = new HashMap<String, GridObject>();
        this.xDimension = xDimension;
        this.yDimension = yDimension;
        this.rules = rules;
        gridCells = new GridCell[xDimension][yDimension];
        for (int i = 0; i < gridCells.length; i++) {
            GridCell[] currentLine = gridCells[i];
            for (int j = 0; j < currentLine.length; j++) {
                currentLine[j] = new GridCell(i, j, freeCapDefault);
            }
        }
        pubMessages = new HashSet<PublicMessage>();
        privMessages = new HashSet<PrivateMessage>();
        this.msgSightReq = msgSightReq;
        this.execMode = execMode;
    }

    /**
     * Set the {@link AgentFactory} that will create the {@link Agent}s for this {@code GridWorld}.
     * @param agentFactory the {@link AgentFactory} that will create the {@link Agent}s for this {@code GridWorld}
     */
    public void setAgentFactory(AgentFactory agentFactory) {
        this.agentFactory = agentFactory;
    }

    /**
     * Get the grid of this {@code GridWorld} consisting of {@link GridCell}s
     * @return the two-dimensional array of {@link GridCell}s making up the actual grid
     */
    public GridCell[][] getGridCells() {
        return gridCells;
    }

    /**
     * Get the X dimension of this {@code GridWorld}.
     * @return the X dimension of this {@code GridWorld}
     */
    public int getXDimension() {
        return xDimension;
    }

    /**
     * Get the Y dimension of this {@code GridWorld}.
     * @return the Y dimension of this {@code GridWorld}
     */
    public int getYDimension() {
        return yDimension;
    }

    /**
     * Adds a new {@link Agent} to this {@code GridWorld}.
     * @param agent the {@link Agent} to add
     * @return true if it was successful, false otherwise
     */
    public boolean addAgent(Agent agent) {
        GridObjectLocation loc = new GridObjectLocation(agent.getInitXPos(), agent.getInitYPos());
        boolean addingSuccess = false;

        /* don't add an agent with a name that already exists in the GridWorld */
        if (nameToGridObject.get(agent.getName()) != null) {
            return false;
        }

        addingSuccess = addGridObject(agent, loc);

        if (addingSuccess == false) {
            return false;
        }
        agentFactory.setupInventory(agent);

        return true;
    }

    /* Recursively remove all objects in the inventory of a GridObject. */
    private void removeInventoryRecursive(GridObject inventoryObject) {
        Collection<GridObject> inventory = inventoryObject.getInventory();
        Collection<GridObject> removeList = new LinkedList<GridObject>();
        removeList.addAll(inventory);
        for (Iterator<GridObject> i = removeList.iterator(); i.hasNext();) {
            GridObject currentObject = i.next();
            removeInventoryRecursive(currentObject);
            inventoryObject.removeFromInventory(currentObject);
            nameToGridObject.remove(currentObject.getName());
            gridObjectToLocation.remove(currentObject);
        }
    }

    /**
     * Remove an {@link Agent} from the {@code GridWorld}.
     * @param agent the {@link Agent} to remove
     */
    public void removeAgent(Agent agent) {
        GridObjectLocation agentLoc = agent.getLocation();
        removeInventoryRecursive(agent);
        gridCells[agentLoc.getX()][agentLoc.getY()].getObjectList().remove(agent);
        nameToGridObject.remove(agent.getName());
        gridObjectToLocation.remove(agent);
    }

    /**
     * Adds a new {@link GridObject} to the {@code GridWorld}.
     * @param obj the {@link GridObject} to add
     * @param loc the new {@link GridObjectLocation} of that object
     * @return true if adding was successful, false otherwise
     */
    public boolean addGridObject(GridObject obj, GridObjectLocation loc) {
        boolean addingSuccess = false;

        if (!loc.isInInventory()) {
            if (loc.isOnGrid(this)) {
                addingSuccess = gridCells[loc.getX()][loc.getY()].addToCell(obj);
            }
        } else {
            addingSuccess = loc.getInventoryObject().addToInventory(obj);
        }

        if (addingSuccess) {
            gridObjectToLocation.put(obj, loc);
            nameToGridObject.put(obj.getName(), obj);
        }

        return addingSuccess;
    }

    /**
     * Remove a {@link GridObject} from the {@code GridWorld}.
     * @param obj the {@link GridObject} to remove
     */
    public void removeGridObject(GridObject obj) {
        GridObjectLocation loc = gridObjectToLocation.get(obj);
        gridObjectToLocation.remove(obj);
        nameToGridObject.remove(obj.getName());
        if (!loc.isInInventory()) {
            gridCells[loc.getX()][loc.getY()].removeFromCell(obj);
        } else {
            loc.getInventoryObject().removeFromInventory(obj);
        }
    }

    /**
     * Move a {@code GridObject} to another {@link GridObjectLocation} with a chance of 1/sqrt(2).
     * @param obj the {@link GridObject} to move
     * @param loc the new {@link GridObjectLocation}
     * @return true if moving was successful, false otherwise
     */
    public boolean moveGridObjectWithDiagCorrect(GridObject obj, GridObjectLocation loc) {
        boolean success = false;
        double diagprob = 1 / Math.sqrt(2);
        Random r = new Random();
        if (r.nextDouble() < diagprob) {
            success = moveGridObject(obj, loc);
        }
        return success;
    }

    /**
     * Move a {@code GridObject} to another {@link GridObjectLocation}
     * @param obj the {@link GridObject} to move
     * @param loc the new {@link GridObjectLocation}
     * @return true if the moving was successful, false otherwise
     */
    public boolean moveGridObject(GridObject obj, GridObjectLocation loc) {
        GridObjectLocation oldLoc = gridObjectToLocation.get(obj);

        boolean moveSuccess = false;

        if (loc.isInInventory() && !loc.getInventoryObject().isInventoryAccessible()) {
            return false;
        }

        if (oldLoc.isInInventory() && !oldLoc.getInventoryObject().isInventoryAccessible()) {
            return false;
        }


        if (!loc.isInInventory() && loc.isOnGrid(this)) {
            moveSuccess = gridCells[loc.getX()][loc.getY()].moveToCell(obj, oldLoc, this);
        }

        if (loc.isInInventory()) {
            moveSuccess = loc.getInventoryObject().addToInventory(obj);
        }

        if (moveSuccess) {
            gridObjectToLocation.put(obj, loc);

            if (!oldLoc.isInInventory()) {
                gridCells[oldLoc.getX()][oldLoc.getY()].removeFromCell(obj);
            } else {
                GridObject inventoryObject = oldLoc.getInventoryObject();
                inventoryObject.removeFromInventory(obj);
            }
        }
        return moveSuccess;
    }

    /**
     * Get the {@link GridObjectLocation} of a {@link GridObject}.
     * @param obj the {@link GridObject} to get the {@link GridObjectLocation} for
     * @return the {@link GridObjectLocation} of the {@link GridObject}
     */
    public GridObjectLocation getGridObjectLocation(GridObject obj) {
        return gridObjectToLocation.get(obj);
    }

    /**
     * Get a {@link GridObject} by its name.
     * @param name the name of the {@link GridObject} to get
     * @return the requested {@link GridObject}
     */
    public GridObject getGridObjectByName(String name) {
        return nameToGridObject.get(name);
    }

    /**
     * Get the list of {@link StateTransRule}s that get used for this {@code GridWorld}.
     * @return the list of {@link StateTransRule}s that get used for this {@code GridWorld}
     */
    public List<StateTransRule> getRules() {
        return rules;
    }

    /**
     * Get all {@link PublicMessage}s currently in this {@code GridWorld}.
     * @return the {@link PublicMessage}s currently in this {@code GridWorld}
     */
    public Collection<PublicMessage> getPublicMessages() {
        return pubMessages;
    }

    /**
     * Add a {@link PublicMessage} to this {@code GridWorld}.
     * @param message the {@link PublicMessage} to add
     */
    public void addPublicMessage(PublicMessage message) {
        pubMessages.add(message);
    }

    /**
     * Clear all {@link PublicMessage}s from this {@code GridWorld}.
     */
    public void clearPublicMessages() {
        pubMessages.clear();
    }

    /**
     * Get all {@link PrivateMessage}s in this {@code GridWorld}.
     * @return all {@link PrivateMessage}s in this {@code GridWorld}
     */
    public Collection<PrivateMessage> getPrivMessages() {
        return privMessages;
    }

    /**
     * Add a {@link PrivateMessage} to this {@code GridWorld}.
     * @param message the {@link PrivateMessage} to add
     */
    public void addPrivateMessage(PrivateMessage message) {
        privMessages.add(message);
    }

    /**
     * Clear all {@link PrivateMessage}s from this {@code GridWorld}.
     */
    public void clearPrivMessages() {
        privMessages.clear();
    }

    /**
     * Tells if it is necessary for the receiver of a message to see the sender to know the origin of the message.
     * @return true if this is the case, false otherwise.
     */
    public boolean isMsgSightReq() {
        return msgSightReq;
    }

    /**
     * Gets this {@link ExecutionMode} of this {@code GridWorld}.
     * @return the {@link ExecutionMode} of this {@code GridWorld}
     */
    public ExecutionMode getExecMode() {
        return execMode;
    }
}
