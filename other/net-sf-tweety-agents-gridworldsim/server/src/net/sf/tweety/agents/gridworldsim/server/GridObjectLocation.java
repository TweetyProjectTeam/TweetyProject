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

import net.sf.tweety.agents.gridworldsim.commons.Constants;

/**
 * Objects made from this class represent the location of a {@link GridObject}, which is either a position on the grid
 * (like in a {@code GridLocation}) or the inventory of an {@link Agent}.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class GridObjectLocation extends GridLocation {

    private GridObject inventoryObject;

    /**
     * Constructs a new {@code GridObjectLocation} on the grid
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public GridObjectLocation(int x, int y) {
        super(x, y);
        inventoryObject = null;
    }

    /**
     * Constructs a new {@code GridObjectLocation} in an {@link GridObject}'s inventory
     * @param inventoryObject the {@code GridObject} to whose inventory this {@code GridObjectLocation} should belong
     */
    public GridObjectLocation(GridObject inventoryObject) {
        super(-1, -1);
        this.inventoryObject = inventoryObject;
    }

    /**
     * Check if this {@code GridObjectLocation} is in some {@link GridObject}'s inventory or on the grid
     * @return true if this {@code GridObjectLocation} is in some {@link GridObject}'s inventory, false if it is on the grid
     */
    public boolean isInInventory() {
        if (inventoryObject != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Set this {@code GridObjectLocation} to be in some {@link GridObject}'s inventory.
     * @param gridObject the {@link GridObject} to whose inventory this {@code GridObjectLocation} should belong
     */
    public void setToInventory(GridObject gridObject) {
        inventoryObject = gridObject;
        this.setXY(-1, -1);
    }

    /**
     * set x and y coordinates
     * @param x the x coordinate
     * @param y the y coordinate
     */
    @Override
    public void setXY(int x, int y) {
        inventoryObject = null;
        this.setXY(x, y);
    }

    /**
     * get the {@link GridObject} to whose inventory this location belongs ({@code null} if the location is on the grid)
     * @return the {@link GridObject} to whose inventory this location belongs ({@code null} if the location is on the grid)
     */
    public GridObject getInventoryObject() {
        return inventoryObject;
    }

    /**
     * Returns a hash code value for the object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + this.getX();
        hash = 29 * hash + this.getY();
        hash = 29 * hash + (this.inventoryObject != null ? this.inventoryObject.hashCode() : 0);
        return hash;
    }

    /**
     * Gets the {@link GridObjectLocation} that is the neighbor location of this location in a given direction.
     * @param direction the direction for which to get the neighbor
     * @return the neighbor {@link GridObjectLocation}
     */
    public GridObjectLocation neighborLocation(String direction) {
        int newX = getX();
        int newY = getY();
        if (direction.equals(Constants.NORTH) || direction.equals(Constants.NORTHEAST) || direction.equals(Constants.NORTHWEST)) {
            newY = newY + 1;
        }

        if (direction.equals(Constants.SOUTH) || direction.equals(Constants.SOUTHEAST) || direction.equals(Constants.SOUTHWEST)) {
            newY = newY - 1;
        }

        if (direction.equals(Constants.NORTHEAST) || direction.equals(Constants.EAST) || direction.equals(Constants.SOUTHEAST)) {
            newX = newX + 1;
        }

        if (direction.equals(Constants.SOUTHWEST) || direction.equals(Constants.WEST) || direction.equals(Constants.NORTHWEST)) {
            newX = newX - 1;
        }
        return new GridObjectLocation(newX, newY);
    }

    /**
     * Return a {@code GridLocation} representation of this {@code GridObjectLocation}
     * @return {@code GridLocation} with the same coordinates if this {@code GridObjectLocation} is on the grid, null if it is in an inventory
     */
    public GridLocation getTwoDimLocation() {
        if (!isInInventory()) {
            return new GridLocation(getX(), getY());
        } else {
            return null;
        }
    }

    /**
     * checks if this {@code GridObjectLocation} is describing the same location as another {@code Object} of type {@code GridObjectLocation}
     * @param obj the {@code Object} to check location equality for
     * @return true if location is equal, otherwise false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }


        GridObjectLocation checkMe = (GridObjectLocation) (obj);

        if (checkMe.getX() == this.getX() && checkMe.getY() == this.getY() && inventoryObject == checkMe.getInventoryObject()) {
            return true;
        } else {
            return false;
        }
    }
}
