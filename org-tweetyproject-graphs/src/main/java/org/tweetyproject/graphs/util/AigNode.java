/*
 * This file is part of "TweetyProject", a collection of Java libraries for
 * logical aspects of artificial intelligence and knowledge representation.
 *
 * TweetyProject is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.graphs.util;

import org.tweetyproject.graphs.Node;

/**
 * Representation of a node in a graph.
 * Implements and handles all options relevant for the aig-graph-component
 *
 * @see <a href="https://github.com/aig-hagen/aig_graph_component">AIG Graph Component</a>
 *
 * @author Lars Bengel
 */
public class AigNode implements Node, Comparable<AigNode> {
    /** unique id of this node */
    private final int id;
    /** label of this node */
    private String name;
    /** horizontal position of this node (default: random placement) */
    private int x = -1;
    /** vertical position of this node (default: random placement) */
    private int y = -1;
    /** color of this node */
    private String color;

    /** whether this node is deletable via the GUI */
    private boolean deletable = true;
    /** whether the label of this node is editable via the GUI */
    private boolean labelEditable = true;
    /** whether the horizontal position of this node should be fixed */
    private boolean fixedPositionX = false;
    /** whether the vertical position of this node should be fixed */
    private boolean fixedPositionY = false;

    private boolean allowOutgoingLinks = true;

    /**
     * Initializes a new node with the given ID and label
     * @param id unique identifier of this node
     * @param name some node label
     */
    public AigNode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Initializes a new node for the given ID and Node
     * @param id unique identifier of this node
     * @param node some node
     */
    public AigNode(int id, Node node) {
        this.id = id;
        this.name = node.toString();
    }

    /**
     * Converts the node to a JSON-String while overriding the options with the given values
     * @param deletable whether the node should be deletable
     * @param labelEditable whether the node label should be editable
     * @param fixedPositionX whether the X position should be fixed
     * @param fixedPositionY whether the Y position should be fixed
     * @return JSON-String representation of this node
     */
    public String toJson(boolean deletable, boolean labelEditable, boolean fixedPositionX, boolean fixedPositionY) {
        this.setDeletable(deletable);
        this.setLabelEditable(labelEditable);
        this.setFixedPositionX(fixedPositionX);
        this.setFixedPositionY(fixedPositionY);
        return this.toJson();
    }

    /**
     * Converts the node to a JSON-String
     * @return JSON-String representation of this node
     */
    public String toJson() {
        StringBuilder s = new StringBuilder("{");
        s.append(String.format("id: %s, ", getId()));
        s.append(String.format("label: \"%s\", ", getName()));

        if (getX() != -1)
            s.append(String.format("x: %s, ", getX()));

        if (getY() != -1)
            s.append(String.format("y: %s, ", getY()));

        if (getColor() != null)
            s.append(String.format("color: \"%s\", ", getColor()));

        if (!isDeletable())
            s.append("deletable: false, ");

        if (!isLabelEditable())
            s.append("labelEditable: false, ");

        if (!isAllowOutgoingLinks())
            s.append("allowOutgoingLinks: false, ");

        if (isFixedPositionX() || isFixedPositionY()) {
            s.append(String.format("fixedPosition: {x: %s, y: %s}", isFixedPositionX() ? "true" : "false", isFixedPositionY() ? "true" : "false"));
        }

        s.append("}");
        return s.toString();
    }

    /**
     * Returns the unique identifier of this node.
     *
     * @return the id of this node
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of this node.
     *
     * @return the name of the node as a String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this node.
     *
     * @param name the new name to assign to this node
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the x-coordinate of this node.
     *
     * @return the x-coordinate value
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of this node.
     *
     * @param x the new x-coordinate value to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Returns the Y-coordinate of this node.
     *
     * @return the Y-coordinate value.
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of this node.
     *
     * @param y the new y-coordinate value to set
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Returns the color associated with this node.
     *
     * @return the color of this node as a String
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the color of this node.
     *
     * @param color the new color to assign to this node
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Checks whether this node is deletable.
     *
     * @return {@code true} if the node can be deleted; {@code false} otherwise.
     */
    public boolean isDeletable() {
        return deletable;
    }

    /**
     * Sets whether this node can be deleted.
     *
     * @param deletable {@code true} if the node should be deletable; {@code false} otherwise
     */
    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    /**
     * Checks whether the label of this node is editable.
     *
     * @return {@code true} if the label can be edited; {@code false} otherwise.
     */
    public boolean isLabelEditable() {
        return labelEditable;
    }

    /**
     * Sets whether the label of this node is editable.
     *
     * @param labelEditable true if the label should be editable, false otherwise
     */
    public void setLabelEditable(boolean labelEditable) {
        this.labelEditable = labelEditable;
    }

    /**
     * Checks whether outgoing links are allowed for this node.
     *
     * @return {@code true} if outgoing links are permitted; {@code false} otherwise.
     */
    public boolean isAllowOutgoingLinks() {
        return allowOutgoingLinks;
    }

    /**
     * Sets whether this node is allowed to have outgoing links.
     *
     * @param allowOutgoingLinks true to allow outgoing links from this node, false otherwise
     */
    public void setAllowOutgoingLinks(boolean allowOutgoingLinks) {
        this.allowOutgoingLinks = allowOutgoingLinks;
    }

    /**
     * Returns whether the X position of this node is fixed.
     *
     * @return {@code true} if the X position is fixed; {@code false} otherwise.
     */
    public boolean isFixedPositionX() {
        return fixedPositionX;
    }

    /**
     * Sets the X position of this node is.
     *
     * @param fixedPositionX true if the X position should be fixed, false otherwise
     */
    public void setFixedPositionX(boolean fixedPositionX) {
        this.fixedPositionX = fixedPositionX;
    }

    /**
     * Checks whether the Y position of this node is fixed.
     *
     * @return {@code true} if the Y position is fixed; {@code false} otherwise.
     */
    public boolean isFixedPositionY() {
        return fixedPositionY;
    }


    /**
     * Sets whether the Y position of this node is fixed.
     *
     * @param fixedPositionY true if the Y position should be fixed; false otherwise
     */
    public void setFixedPositionY(boolean fixedPositionY) {
        this.fixedPositionY = fixedPositionY;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AigNode) {
            return getName().equals(((AigNode) obj).getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getName().hashCode()*17 + getId();
    }

    @Override
    public int compareTo(AigNode o) {
        return this.name.compareTo(o.name);
    }
}
