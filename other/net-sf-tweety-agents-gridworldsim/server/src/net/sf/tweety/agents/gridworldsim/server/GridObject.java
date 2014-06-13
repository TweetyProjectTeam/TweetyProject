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
package net.sf.tweety.agents.gridworldsim.server;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Objects made from this class represent objects on the grid.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class GridObject {

    private String name;
    private GridWorld gridWorld;
    private int initCapNeed;
    private int initCap;
    private Collection<GridObject> inventory;
    private Collection<String> properties;

    /**
     * Constructs a new {@code GridObject}, that first needs to be configured.
     */
    public GridObject() {
        properties = new HashSet<String>();
        inventory = new HashSet<GridObject>();
    }

    /**
     * Configure this {@code GridObject}.
     * @param name the name
     * @param gridWorld the {@link GridWorld} this {@code GridObject} belongs to
     * @param initCapNeed the initial capacity need
     * @param initCap the initial capacity
     */
    public void configure(String name, GridWorld gridWorld, int initCapNeed, int initCap) {
        this.name = name;
        this.gridWorld = gridWorld;
        this.initCapNeed = initCapNeed;
        this.initCap = initCap;
    }

    /**
     * Get the name of this {@code GridObject}
     * @return the name of this {@code GridObject}
     */
    public String getName() {
        return name;
    }

    /**
     * Get the location of this {@code GridObject}
     * @return the location of this {@code GridObject}
     */
    public GridObjectLocation getLocation() {
        return gridWorld.getGridObjectLocation(this);
    }

    /**
     * Get the free capacity of this {@code GridObject}
     * @return the free capacity (-1 = infinite) of this {@code GridObject}
     */
    public int getFreeCap() {
        if (initCap == -1) {
            return initCap;
        }
        int freeCap = initCap;
        for (Iterator<GridObject> i = inventory.iterator(); i.hasNext();) {
            GridObject currentObject = i.next();
            freeCap = freeCap - currentObject.getCapNeed();
        }
        return freeCap;
    }

    /**
     * Set the initial free capacity of this {@code GridObject}.
     * @param initCap the initial free capacity (-1 = infinite) of this {@code GridObject}
     */
    public void setInitCap(int initCap) {
        this.initCap = initCap;
    }

    /**
     * Get the capacity need of this {@code GridObject}.
     * @return the capacity need of this {@code GridObject}
     */
    public int getCapNeed() {
        int capNeed = initCapNeed;
        for (Iterator<GridObject> i = inventory.iterator(); i.hasNext();) {
            GridObject currentObject = i.next();
            capNeed = capNeed + currentObject.getCapNeed();
        }

        return capNeed;
    }

    /**
     * Set the initial capacity need of this {@code GridObject}.
     * @param initCapNeed the initial capacity need of this {@code GridObject}
     */
    public void setInitCapNeed(int initCapNeed) {
        this.initCapNeed = initCapNeed;
    }

    /**
     * Is there enough free capacity to store something with a capacity need of {@code capNeed}?
     * @param capNeed check if this object can satisfy {@code capNeed}
     * @return true if {@code capNeed} can be satisfied, false otherwise
     */
    private boolean enoughFreeCap(int capNeed) {
        if (getFreeCap() == -1) {
            return true;
        }
        if (getFreeCap() >= capNeed) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds another {@code GridObject} to the inventory.
     * This method is protected, because GridWorld should handle such updates.
     * @param obj the {@code GridObject} to add
     * @return true if adding was a success, false otherwise
     */
    protected boolean addToInventory(GridObject obj) {
        if (enoughFreeCap(obj.getCapNeed())) {
            inventory.add(obj);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes a {@code GridObject} from the inventory.
     * This method is protected, because GridWorld should handle such updates.
     * @param obj the {@code GridObject} to remove
     */
    protected boolean removeFromInventory(GridObject obj) {
        inventory.remove(obj);
        return true;
    }

    /**
     * Gets the inventory.
     * @return the inventory
     */
    public Collection<GridObject> getInventory() {
        return inventory;
    }

    /**
     * Tell if a given {@code GridObject} is in the inventory of this {@code GridObject}.
     * @param gridObject the {@code GridObject} to check for
     * @return true if the {@code GridObject} is in the inventory, false otherwise
     */
    public boolean hasInInventory(GridObject gridObject) {
        return inventory.contains(gridObject);
    }

    /**
     * Gets the initial capacity of this {@code GridObject}.
     * @return the initial capacity of this {@code GridObject}
     */
    public int getInitCap() {
        return initCap;
    }

    /**
     * Gets the initial capacity need of this {@code GridObject}.
     * @return the initial capacity need of this {@code GridObject}
     */
    public int getInitCapNeed() {
        return initCapNeed;
    }

    /**
     * Recursively check (during initialization of the GridWorld) that adding an object to the inventory of an object, which
     * can be in the inventory of an object etc. does not violate the free capacity constraint of any object in the
     * contains graph.
     */
    private int recursiveFreeCap(GridObject currentObject, GridObject containedObject) {
        /* if the object to look for was found, recursion ends and the capacity of this object is returned */
        if (currentObject.equals(containedObject)) {
            return containedObject.getFreeCap();
        } else {
            /* otherwise we do recursion on all childs of the currently processed object */
            Collection<GridObject> currentInventory = currentObject.getInventory();

            for (Iterator<GridObject> i = currentInventory.iterator(); i.hasNext();) {
                GridObject currentChild = i.next();
                int childFreeCap = recursiveFreeCap(currentChild, containedObject);

                /* if the child has the requested object not as a descendent, we ignore that child */
                if (childFreeCap == -2) {
                    continue;
                }

                /* if this point is reached, the child has the requested object as one of its descendents */

                int currentObjectFreeCap = currentObject.getFreeCap();

                /* make the min function handle the unbounded case correctly */
                if (childFreeCap == -1) {
                    childFreeCap = Integer.MAX_VALUE;
                }
                if (currentObjectFreeCap == -1) {
                    currentObjectFreeCap = Integer.MAX_VALUE;
                }

                int combinedFreeCap = Math.min(childFreeCap, currentObjectFreeCap);

                if (combinedFreeCap == Integer.MAX_VALUE) {
                    return -1;
                } else {
                    return combinedFreeCap;
                }
            }

            // in case the above loop didn't return a combined freecap, containedObject is not a descendent of this object
            return -2;
        }
    }

    /**
     * Get the free capacity of a {@code GridObject} in the inventory of this {@code GridObject} with respect to the free capacity of
     * all {@code GridObject}s in the contains graph.
     * @param containedObject the {@code GridObject} to check for
     * @return the free capacity of the {@code GridObject} with respect to the free capacity of all {@code GridObject}s in the contains graph
     */
    public int getContainedFreeCap(GridObject containedObject) {
        return recursiveFreeCap(this, containedObject);
    }

    /**
     * Tells if the inventory of this {@code GridObject} is accessible for {@code Agent}s.
     * @return true if the inventory is accessible, false otherwise
     */
    public boolean isInventoryAccessible() {
        return true;
    }

    /**
     * Adds a {@link GridObjectParameter} to this {@code GridObject}. This implementation only supports {@link GridObjectParameter}s of type "propery".
     * @param parameter the {@link GridObjectParameter} to add
     */
    public void addParameter(GridObjectParameter parameter) {
        if (parameter.getName().equals("property")) {
            properties.add(parameter.getValue());
        }

    }
    /**
     * Get the properties of this {@code GridObject}.
     * @param visibleOnly true if only visible properties should be returned, false otherwise. This implementation only supports visible properties.
     * @return the properties of this {@code GridObject}
     */
    public Collection<String> getProperties(boolean visibleOnly) {
        return properties;
    }
}
