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

import net.sf.tweety.agents.gridworldsim.commons.TwoDimLocation;

/**
 * Objects made from this class represent a location on the grid.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class GridLocation extends TwoDimLocation {

    /**
     * Constructs a new {@code GridLocation}.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public GridLocation(int x, int y) {
        super(x, y);
    }

    /**
     * Constructs a new empty/uninitialized {@code GridLocation}.
     */
    public GridLocation() {
        super();
    }

    /**
     * Constructs a new {@code GridLocation}.
     * @param twoDim the {@link TwoDimLocation} containing the two dimensional location
     */
    public GridLocation(TwoDimLocation twoDim) {
        this.setXY(twoDim.getX(), twoDim.getY());
    }

    /**
     * Checks if this {@code GridLocation} is a valid location in a given {@link GridWorld}.
     * @param gridWorld the {@link GridWorld} to check if this {@code GridLocation} is valid there
     * @return true if this {@code GridLocation} is valid for the given {@link GridWorld}, false otherwise
     */
    public boolean isOnGrid(GridWorld gridWorld) {
        if (this.getX() < 0 || this.getY() < 0 || this.getX() > gridWorld.getXDimension() - 1 || this.getY() > gridWorld.getYDimension() - 1) {
            return false;
        }
        return true;
    }

    /**
     * Checks if this {@code GridLocation} is describing the same location as another {@code GridLocation}.
     * @param obj the {@code Object} to check location equality for
     * @return true if the comparison {@code Object} is a {@code GridLocation} and locations are equal, otherwise false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || this.getClass() != obj.getClass()) {
            return false;
        }

        GridLocation checkMe = (GridLocation) (obj);

        if (checkMe.getX() == this.getX() && checkMe.getY() == this.getY()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns a hash code value for this {@code GridLocation}.
     * @return a hash code value for this {@code GridLocation}
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.getX();
        hash = 67 * hash + this.getY();
        return hash;
    }

    /**
     * Get the {@link GridCell} for this {@code GridLocation} in a {@link GridWorld}
     * @param gridWorld the {@link GridWorld}
     * @return the {@link GridCell} for this {@code GridLocation}
     */
    public GridCell getGridCell(GridWorld gridWorld) {
        return gridWorld.getGridCells()[this.getX()][this.getY()];
    }
}
