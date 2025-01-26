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

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public boolean isLabelEditable() {
        return labelEditable;
    }

    public void setLabelEditable(boolean labelEditable) {
        this.labelEditable = labelEditable;
    }

    public boolean isAllowOutgoingLinks() {
        return allowOutgoingLinks;
    }

    public void setAllowOutgoingLinks(boolean allowOutgoingLinks) {
        this.allowOutgoingLinks = allowOutgoingLinks;
    }

    public boolean isFixedPositionX() {
        return fixedPositionX;
    }

    public void setFixedPositionX(boolean fixedPositionX) {
        this.fixedPositionX = fixedPositionX;
    }

    public boolean isFixedPositionY() {
        return fixedPositionY;
    }

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
