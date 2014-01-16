package net.sf.tweety.agents.gridworldsim.server;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * An object made from this class represents a single cell of a {@link GridWorld}.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class GridCell {

    private final List<GridObject> objectList;
    private final GridLocation location;
    private boolean wall;
    private boolean trench;
    private boolean curtain;
    private boolean fog;
    private boolean interference;
    private int initCap;

    /**
     * Constructs a new {@code GridCell}.
     * @param xPos the x coordinate of this {@code GridCell}
     * @param yPos the y coordinate of this {@code GridCell}
     * @param initCap the initial free capacity of this {@code GridCell}
     */
    public GridCell(int xPos, int yPos, int initCap) {
        objectList = new LinkedList<GridObject>();
        location = new GridLocation(xPos, yPos);
        wall = false;
        trench = false;
        curtain = false;
        fog = false;
        interference = false;
        this.initCap = initCap;
    }

    /**
     * Does this {@code GridCell} contain a wall?
     * @return true if this {@code GridCell} contains a wall, false otherwise
     */
    public boolean isWall() {
        return wall;
    }

    /**
     * Set that this {@code GridCell} contains a wall (if it doesn't already contain an exclusive obstacle).
     * @param wall true if this {@code GridCell} contains a wall, false otherwise
     */
    public void setWall(boolean wall) {
        this.wall = (wall && !hasExclusiveObstacle());
    }

    /**
     * Does this {@code GridCell} contain a trench?
     * @return true if this {@code GridCell} contains a trench, false otherwise
     */
    public boolean isTrench() {
        return trench;
    }

    /**
     * Set that this {@code GridCell} contains a trench (if it doesn't already contain an exclusive obstacle).
     * @param trench true if this {@code GridCell} contains a trench, false otherwise
     */
    public void setTrench(boolean trench) {
        this.trench = (trench && !hasExclusiveObstacle());
    }

    /**
     * Does this {@code GridCell} contain a curtain?
     * @return true if this {@code GridCell} contains a curtain, false otherwise
     */
    public boolean isCurtain() {
        return curtain;
    }

    /**
     * Set that this {@code GridCell} contains a curtain (if it doesn't already contain an exclusive obstacle).
     * @param curtain true if this {@code GridCell} contains a curtain, false otherwise
     */
    public void setCurtain(boolean curtain) {
        this.curtain = (curtain && !hasExclusiveObstacle());
    }

    /**
     * Does this {@code GridCell} contain an interference?
     * @return true if this {@code GridCell} contains an interference, false otherwise
     */
    public boolean isInterference() {
        return interference;
    }

    /**
     * Set that this {@code GridCell} contains an interference.
     * @param interference true if this {@code GridCell} contains an interference, false otherwise
     */
    public void setInterference(boolean interference) {
        this.interference = interference;
    }

    /**
     * Does this {@code GridCell} contain fog?
     * @return true if this {@code GridCell} contains fog, false otherwise
     */
    public boolean isFog() {
        return fog;
    }

    /**
     * Set that this {@code GridCell} contains fog.
     * @param fog true if this {@code GridCell} contains a fog, false otherwise
     */
    public void setFog(boolean fog) {
        this.fog = fog;
    }

    /**
     * Does this {@code GridCell} have no visibility blocking obstacle?
     * @return true if this {@code GridCell} is visible, false otherwise
     */
    public boolean isVisible() {
        return (!wall && !curtain && !fog);
    }

    /**
     * Is this {@code GridCell} accessible?
     * @return true if this {@code GridCell} is accessible, false otherwise
     */
    public boolean isAccessible() {
        return (!wall && !trench);
    }

    /**
     * Adds a {@link GridObject} to the {@code GridCell}.
     * This method is protected, because {@link GridWorld} should handle such updates.
     * @param obj the {@link GridObject} to add
     * @return true if the adding was successful, false otherwise
     */
    protected boolean addToCell(GridObject obj) {
        if (enoughFreeCap(obj.getCapNeed()) && !wall && !trench) {
            objectList.add(obj);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Move a {@link GridObject} to this {@code GridCell}. This method is protected, because {@link GridWorld} should handle such updates.
     * @param obj the {@link GridObject} to move
     * @param oldLoc the old {@link GridObjectLocation}
     * @param gridWorld the {@link GridWorld} of this {@code GridCell}
     * @return true if the moving was successful, false otherwise
     */
    protected boolean moveToCell(GridObject obj, GridObjectLocation oldLoc, GridWorld gridWorld) {
        /**
         * If the GridObject to move is in an inventory of an agent in this cell, just trying to add it would result in
         * wrong free capacity checking, so special handling is required.
         */
        if (oldLoc.isInInventory()) {
            GridObjectLocation parentLocation = gridWorld.getGridObjectLocation(oldLoc.getInventoryObject());
            if (!parentLocation.equals(new GridObjectLocation(location.getX(), location.getY()))) {
                return addToCell(obj);
            } else {
                if (!wall && !trench) {
                    objectList.add(obj);
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return addToCell(obj);
        }
    }

    /**
     * Removes a {@link GridObject} from this {@code GridCell}.
     * This method is protected, because GridWorld should handle such updates.
     * @param obj the {@link GridObject} to remove
     */
    protected void removeFromCell(GridObject obj) {
        objectList.remove(obj);
    }

    /**
     * Returns the {@code Collection} of all {@link GridObject}s in this {@code GridCell}.
     * @return the {@code Collection} of {@link GridObject}s in this {@code GridCell}
     */
    public Collection<GridObject> getObjectList() {
        return objectList;
    }

    /**
     * Get the {@link GridLocation} of this {@code GridCell}
     * @return the {@link GridLocation} of this {@code GridCell}
     */
    public GridLocation getLocation() {
        return location;
    }

    /**
     * Has this {@code GridCell} already an exclusive obstacle type (wall, trench, curtain) which cannot be combined with another
     * exclusive obstacle type?
     * @return true if this {@code GridCell} already contains a wall, trench or curtain
     */
    public boolean hasExclusiveObstacle() {
        return (wall || trench || curtain);
    }

    /**
     * Has this {@code GridCell} enough free capacity for the given capacity need?
     * @param need the capacity need to check for if this {@code GridCell} can satisfy it
     * @return true if the capacity need can be satisfied, false otherwise
     */
    private boolean enoughFreeCap(int need) {
        if (getFreeCap() == -1) {
            return true;
        }

        if (getFreeCap() >= need) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the free capacity of this {@code GridCell}.
     * @return the free capacity of this {@code GridCell} (-1 means infinite)
     */
    public int getFreeCap() {
        if (initCap == -1) {
            return initCap;
        }

        int freeCap = initCap;
        for (Iterator<GridObject> i = objectList.iterator(); i.hasNext();) {
            GridObject currentObject = i.next();
            freeCap = freeCap - currentObject.getCapNeed();
        }
        return freeCap;
    }

    /**
     * Set the initial free capacity of this {@code GridCell}.
     * @param initCap the initial free capacity of this {@code GridCell} (-1 means infinite)
     */
    public void setInitCap(int initCap) {
        this.initCap = initCap;
    }
}
