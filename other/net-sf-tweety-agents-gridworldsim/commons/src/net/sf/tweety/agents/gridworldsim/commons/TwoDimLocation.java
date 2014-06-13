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
package net.sf.tweety.agents.gridworldsim.commons;

/**
 * Objects made from this class represent a two-dimensional location
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class TwoDimLocation {

    private int x;
    private int y;

    /**
     * Constructs a new {@code TwoDimLocation}.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public TwoDimLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a new empty/uninitialized {@code TwoDimLocation}.
     */
    public TwoDimLocation() {
    }

    /**
     * Get the x coordinate.
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Get the y coordinate.
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Set x and y coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns a hash code value for the object.
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.x;
        hash = 67 * hash + this.y;
        return hash;
    }

    /**
     * checks if this {@code TwoDimLocation} is describing the same location as another
     * @param obj the {@code Object} to check location equality for
     * @return true if location is equal, otherwise false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || this.getClass() != obj.getClass()) {
            return false;
        }

        TwoDimLocation checkMe = (TwoDimLocation) (obj);

        if (checkMe.getX() == x && checkMe.getY() == y) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if another {@code TwoDimLocation} is a neighbor location of this {@code TwoDimLocation}.
     * @param loc the {@code TwoDimLocation} to check
     * @return true if the other {@code TwoDimLocation} is a neighbor location of this {@code TwoDimLocation}, otherwise false
     */
    public boolean isNeighbor(TwoDimLocation loc) {
        int xDiff = Math.abs(loc.getX() - x);
        int yDiff = Math.abs(loc.getY() - y);
        return (xDiff != 0 || yDiff != 0) && xDiff <= 1 && yDiff <= 1;
    }

    /**
     * Check if another {@code TwoDimLocation} is a diagonal neighbor of this {@code TwoDimLocation}.
     * @param loc the {@code TwoDimLocation} to check
     * @return true if the other {@code TwoDimLocation} is a diagonal neighbor of this {@code TwoDimLocation}, otherwise false
     */
    public boolean isDiagonalNeighbor(TwoDimLocation loc) {
        int xDiff = Math.abs(loc.getX() - x);
        int yDiff = Math.abs(loc.getY() - y);
        return xDiff==1 && yDiff==1;
    }
}
