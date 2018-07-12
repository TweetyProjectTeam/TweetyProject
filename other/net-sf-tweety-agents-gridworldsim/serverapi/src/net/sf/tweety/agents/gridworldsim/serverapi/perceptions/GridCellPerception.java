/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
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
package net.sf.tweety.agents.gridworldsim.serverapi.perceptions;

import java.util.Collection;
import java.util.HashSet;

/**
 * Objects made from this class represent a perceived {@code GridCell}
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class GridCellPerception {

    private final Collection<GridObjectPerception> objects;
    private final int xPos;
    private final int yPos;
    private boolean wall;
    private boolean trench;
    private boolean curtain;
    private boolean fog;
    private boolean visible;
    private boolean interference;
    private int freeCap;

    /**
     * Constructs a new {@code GridCellPerception}.
     * @param xPos the x position of this {@code GridCellPerception}
     * @param yPos the y position of this {@code GridCellPerception}
     */
    public GridCellPerception(int xPos, int yPos) {
        objects = new HashSet<GridObjectPerception>();
        this.xPos = xPos;
        this.yPos = yPos;
        this.visible = false;
    }

    /**
     * Adds a {@link GridObjectPerception} to the {@code GridCellPerception}. This method is protected because it should not
     * be used directly to add {@code GridObjectPerception}s to the cell. Use the {@code addtocell} method of {@link GridWorldPerception}
     * instead.
     * @param obj the {@link GridObjectPerception} to add
     */
    protected void addToCell(GridObjectPerception obj) {
        objects.add(obj);
    }

    /**
     * Get the {@code Collection} of all {@link GridObjectPerception}s in this cell.
     * @return the {@code Collection} of all {@link GridObjectPerception}s in this cell
     */
    public Collection<GridObjectPerception> getObjectSet() {
        return objects;
    }

    /**
     * Does this {@code GridCellPerception} contain a wall?
     * @return true if this {@code GridCellPerception} contains a wall, false otherwise
     */
    public boolean isWall() {
        return wall;
    }

    /**
     * Set if this {@code GridCellPerception} contains a wall.
     * @param wall true if this {@code GridCellPerception} contains a wall, false otherwise
     */
    public void setWall(boolean wall) {
        this.wall = wall;
    }

    /**
     * Does this {@code GridCellPerception} contain a curtain?
     * @return true if this {@code GridCellPerception} contains a curtain, false otherwise
     */
    public boolean isCurtain() {
        return curtain;
    }

    /**
     * Does this {@code GridCellPerception} contain fog?
     * @return true if this {@code GridCellPerception} contains fog, false otherwise
     */
    public boolean isFog() {
        return fog;
    }

    /**
     * Does this {@code GridCellPerception} contain a trench?
     * @return true if this {@code GridCellPerception} contains a trench, false otherwise
     */
    public boolean isTrench() {
        return trench;
    }

    /**
     * Set if this {@code GridCellPerception} contains a curtain.
     * @param curtain true if this {@code GridCellPerception} contains a curtain, false otherwise
     */
    public void setCurtain(boolean curtain) {
        this.curtain = curtain;
    }

    /**
     * Set if this {@code GridCellPerception} contains fog.
     * @param fog true if this {@code GridCellPerception} contains fog, false otherwise
     */
    public void setFog(boolean fog) {
        this.fog = fog;
    }

    /**
     * Set if this {@code GridCellPerception} contains a trench.
     * @param trench true if this {@code GridCellPerception} contains a trench, false otherwise
     */
    public void setTrench(boolean trench) {
        this.trench = trench;
    }

    /**
     * Tells if the cell is accessible, i.e. does contain neither wall nor trench and has enough space for the given parameter
     * @param the capacity need to check for
     * @return true if the cell has enough space for the given capacity and contains neither wall nor trench, false otherwise
     */
    public boolean isAccessible(int cap) {
        if (cap>freeCap) {
            return false;
        }

        return !(wall || trench);
    }

    /**
     * Check if this {@code GridCellPerception} is visible.
     * @return true if visible, false if not visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Set if this {@code GridCellPerception} is visible.
     * @param visible true if visible, false if not visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Get the x coordinate of this cell.
     * @return the x coordinate of this cell
     */
    public int getxPos() {
        return xPos;
    }

    /** Get the y coordinate of this cell.
     * @return the y coordinate of this cell
     */
    public int getyPos() {
        return yPos;
    }

    /** Get the free capacity of this cell.
     * @return the free capacity of this cell
     */
    public int getFreeCap() {
        return freeCap;
    }

    /**
     * Set the free capacity of this cell.
     * @param freeCap the new free capacity of this cell
     */
    public void setFreeCap(int freeCap) {
        this.freeCap = freeCap;
    }

    /**
     * Check if this cell contains an interference.
     * @return true if this cell contains an interference, false otherwise
     */
    public boolean isInterference() {
        return interference;
    }

    /**
     * Set if this cell contains an interference.
     * @param interference true if this cell contains an interference, false otherwise
     */
    public void setInterference(boolean interference) {
        this.interference = interference;
    }
}
