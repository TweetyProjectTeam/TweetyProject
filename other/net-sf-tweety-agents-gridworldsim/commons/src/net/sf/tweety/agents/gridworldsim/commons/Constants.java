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
