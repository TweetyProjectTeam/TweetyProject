package net.sf.tweety.agents.gridworldsim.serverapi.perceptions;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Objects made from this class represent the perception of a GridWorld.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class GridWorldPerception {

    private final GridCellPerception[][] gridCellPerc;
    private AgentPerception myAgent;
    private int myAgentXLocation;
    private int myAgentYLocation;
    private final Map<String, GridObjectPerception> gridObjectNameToGridObject;
    private final Collection<MessagePerception> messages;
    private final long time;
    private final int xDimension;
    private final int yDimension;

    /**
     * Constructs a new {@code GridWorldPerception}
     * @param xDimension the x dimension
     * @param yDimension the y dimension
     * @param time the current state time
     */
    public GridWorldPerception(int xDimension, int yDimension, long time) {
        myAgent = null;
        myAgentXLocation = -1;
        myAgentYLocation = -1;
        gridCellPerc = new GridCellPerception[xDimension][yDimension];
        for (int i = 0; i < gridCellPerc.length; i++) {
            GridCellPerception[] currentLine = gridCellPerc[i];
            for (int j = 0; j < currentLine.length; j++) {
                currentLine[j] = new GridCellPerception(i, j);
            }
        }

        gridObjectNameToGridObject = new HashMap<String, GridObjectPerception>();
        messages = new LinkedList<MessagePerception>();
        this.time = time;
        this.xDimension = xDimension;
        this.yDimension = yDimension;
    }

    /**
     * Get the x dimension.
     * @return the x dimension
     */
    public int getxDimension() {
        return xDimension;
    }

    /**
     * Get the y dimension.
     * @return the y dimension
     */
    public int getyDimension() {
        return yDimension;
    }

    /**
     * Get the {@link GridCellPerception}s of this {@code GridWorldPerception}
     * @return the {@link GridCellPerception}s of this {@code GridWorldPerception}
     */
    public GridCellPerception[][] getGridCellPerceptions() {
        return gridCellPerc;
    }

    /**
     * Get the {@link AgentPerception} of this agent (can be {@code null} for observer clients).
     * @return the {@link AgentPerception} of this agent (can be {@code null} for observer clients)
     */
    public AgentPerception getMyAgent() {
        return myAgent;
    }

    /**
     * Set the {@link AgentPerception} of the current agent (can be {@code null} for observer clients).
     * @param myAgent the {@link AgentPerception} of this agent (can be {@code null} for observer clients}
     */
    public void setMyAgent(AgentPerception myAgent, int xLocation, int yLocation) {
        this.myAgent = myAgent;
        this.myAgentXLocation = xLocation;
        this.myAgentYLocation = yLocation;
    }

    /**
     * Get the x coordinate of the current agent (can be -1 for observer clients).
     * @return the x coordinate of the current agent (can be -1 for observer clients)
     */
    public int getMyAgentXLocation() {
        return myAgentXLocation;
    }

    /**
     * Get the y coordinate of the current agent (can be -1 for observer clients).
     * @return the y coordinate of the current agent (can be -1 for observer clients)
     */
    public int getMyAgentYLocation() {
        return myAgentYLocation;
    }

    /**
     * Add a {@link GridObjectPerception} to the inventory of another.
     * @param parent the {@link GridObjectPerception} to which another should be added
     * @param child the {@link GridObjectPerception} which should be added to the inventory of another
     */
    public void addToInventory(GridObjectPerception parent, GridObjectPerception child) {
        gridObjectNameToGridObject.put(child.getName(), child);
        parent.addToInventory(child);
    }

    /**
     * Add a {@link GridObjectPerception} to a {@link GridCellPerception} of this {@code GridWorldPerception}.
     * @param cell the {@link GridCellPerception} to which a {@link GridObjectPerception} should be added
     * @param child the {@link GridObjectPerception} which should be added to a {@link GridCellPerception}
     */
    public void addToCell(GridCellPerception cell, GridObjectPerception child) {
        gridObjectNameToGridObject.put(child.getName(), child);
        cell.addToCell(child);
    }

    /**
     * Get a {@link GridObjectPerception} by its name.
     * @param name the name to look for
     * @return the {@link GridObjectPerception} with the specified name (or {@code null})
     */
    public GridObjectPerception getGridObjectPerceptionByName(String name) {
        return gridObjectNameToGridObject.get(name);
    }

    /**
     * Add a {@link MessagePerception}.
     * @param message the {@link MessagePerception} to add
     */
    public void addMessage(MessagePerception message) {
        messages.add(message);
    }

    /**
     * Get all {@link MessagePerception}s of this {@code GridWorldPerception}.
     * @return all {@link MessagePerception}s of this {@code GridWorldPerception}
     */
    public Collection<MessagePerception> getMessages() {
        return messages;
    }

    /**
     * Get the current state time.
     * @return the current state time
     */
    public long getTime() {
        return time;
    }
}
