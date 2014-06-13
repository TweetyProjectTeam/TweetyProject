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
 * This class provides constants used by different software components of GridWorldSim.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class Constants {

    /**
     * Indicates direction "North"/"up" on the grid
     */
    public static final String NORTH = "n";
    /**
     * Indicates direction "North East"/"up-and-right" on the grid
     */
    public static final String NORTHEAST = "ne";
    /**
     * Indicates direction "East"/"right" on the grid
     */
    public static final String EAST = "e";
    /**
     * Indicates direction "South East"/"down-and-right" on the grid
     */
    public static final String SOUTHEAST = "se";
    /**
     * Indicates direction "South"/"down" on the grid
     */
    public static final String SOUTH = "s";
    /**
     * Indicates direction "South West"/"down-and-left" on the grid
     */
    public static final String SOUTHWEST = "sw";
    /**
     * Indicates direction "West"/"left" on the grid
     */
    public static final String WEST = "w";
    /**
     * Indicates direction "North West"/"up-and-left" on the grid
     */
    public static final String NORTHWEST = "nw";
    /**
     * Indicates an inventory
     */
    public static final String INVENTORY = "inventory";
    /**
     * Indicates the grid
     */
    public static final String GRID = "grid";
    /**
     * Remote location of the XML Schema for verifying perception (gridworld-percept) XML documents
     */
    public static final String PERCEPTION_SCHEMA_REMOTE_LOCATION = "http://www.tittel.net/gridworldsim/schemas/perception-1.0.xsd";
    /**
     * Remote location of the XML Schema for verifying actions XML documents
     */
    public static final String ACTIONS_SCHEMA_REMOTE_LOCATION = "http://www.tittel.net/gridworldsim/schemas/actionrequest-1.0.xsd";
}
