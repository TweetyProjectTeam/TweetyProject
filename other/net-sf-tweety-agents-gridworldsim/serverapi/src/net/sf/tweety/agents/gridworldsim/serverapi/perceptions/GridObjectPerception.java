package net.sf.tweety.agents.gridworldsim.serverapi.perceptions;

import java.util.Collection;
import java.util.Iterator;
import java.util.HashSet;

/**
 * Objects made from this class represent perceived objects.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class GridObjectPerception implements Comparable {

    private final String name;
    private final int freeCap;
    private final int capNeed;
    private final Collection<GridObjectPerception> inventory;
    private final Collection<String> properties;

    /**
     * Constructs a new {@code GridObjectPerception}.
     * @param name the name of the perceived object
     * @param capNeed the capacity need of the perceived object
     * @param freeCap the free capacity of the perceived object
     * @param properties the properties of the perceived object
     */
    public GridObjectPerception(String name, int capNeed, int freeCap, Collection<String> properties) {
        this.name = name;
        this.capNeed = capNeed;
        this.freeCap = freeCap;
        this.properties = properties;
        inventory = new HashSet<GridObjectPerception>();
        properties = new HashSet<String>();
    }

    /**
     * Get the name of this {@code GridObjectPerception}.
     * @return the name of this {@code GridObjectPerception}
     */
    public String getName() {
        return name;
    }

    /**
     * Get the capacity need of this {@code GridObjectPerception}.
     * @return the capacity need of this {@code GridObjectPerception}
     */
    public int getCapNeed() {
        return capNeed;
    }

    /**
     * Get the free capacity of this {@code GridObjectPerception}.
     * @return the free capacity of this {@code GridObjectPerception}
     */
    public int getFreeCap() {
        return freeCap;
    }

    /**
     * Get the inventory {@code Collection} of this {@code GridObjectPerception}.
     * @return the inventory {@code Collection} of this {@code GridObjectPerception}
     */
    public Collection<GridObjectPerception> getInventory() {
        return inventory;
    }

    /**
     * Add a {@code GridObjectPerception} to the inventory of this {@code GridObjectPerception}. This method is protected
     * because it should not be used directly, instead use the {@code addToInventory} method of {@link GridWorldPerception}.
     * @param obj the {@code GridObjectPerception} to add
     */
    protected void addToInventory(GridObjectPerception obj) {
        inventory.add(obj);
    }

    /**
     * Get a {@code GridObjectPerception} from the inventory of this {@code GridObjectPerception} by name.
     * @param name the name to look for
     * @return the {@code GridObjectPerception} with the given name from the inventory ({@code null} if not found)
     */
    public GridObjectPerception getInventoryObjecyByName(String name) {
        for (Iterator<GridObjectPerception> i = inventory.iterator(); i.hasNext();) {
            GridObjectPerception currentObject = i.next();
            if (currentObject.getName().equals(name)) {
                return currentObject;
            }
        }
        return null;
    }

    /**
     * Compares this {@code GridObjectPerception} to another {@code Object}. This is done for lexicographical sorting
     * of {@code GridObjectPerception}s and {@link AgentPerception}s. The only exception to lexicographical sorting is that
     * an {@link AgentPerception} in which the agent is perceiving himself is always sorted first.
     * @param o the {@code Object} to compare with
     * @return negative value if this {@code GridObjectPerception} is sorted before another object, 0 if both are equal regarding the order and positive value if this {@code GridObjectPerception} comes after the other {@code Object} in the ordering.
     */
    public int compareTo(Object o) {
        if (o instanceof GridObjectPerception) {
            if (o instanceof AgentPerception) {
                AgentPerception compareAgent = (AgentPerception) o;
                if (compareAgent.isSelf()) {
                    return 1;
                }
            }
            GridObjectPerception compareTo = (GridObjectPerception) o;
            int compare = name.compareTo(compareTo.getName());
            return compare;
        }
        return 0;
    }

    /**
     * Get the properties.
     * @return the properties
     */
    public Collection<String> getProperties() {
        return properties;
    }
}
