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

import org.tweetyproject.graphs.DirectedEdge;

/**
 * Representation of a link/edge in a graph.
 * Implements and handles all options relevant for the aig-graph-component
 *
 * @see <a href="https://github.com/aig-hagen/aig_graph_component">AIG Graph Component</a>
 *
 * @author Lars Bengel
 */
public class AigLink extends DirectedEdge<AigNode> {

    /** whether the link is deletable via the GUI */
    private boolean deletable = true;
    /** whether the link label is editable via the GUI */
    private boolean labelEditable = true;

    /** the color of this link */
    private String color;

    /**
     * Initializes a new link for the given two nodes
     * @param source some node
     * @param target some node
     */
    public AigLink(AigNode source, AigNode target) {
        super(source, target);
    }

    /**
     * Initializes a new link for the given two nodes with the label
     * @param source some node
     * @param target some node
     * @param label some link label
     */
    public AigLink(AigNode source, AigNode target, String label) {
        super(source, target, label);
    }

    /**
     * Converts the link to a JSON-String while overriding the options with the given values
     * @param deletable whether the link should be deletable
     * @param labelEditable whether the link label should be editable
     * @return JSON-String representation of this link
     */
    public String toJson(boolean deletable, boolean labelEditable) {
        this.setDeletable(deletable);
        this.setLabelEditable(labelEditable);
        return toJson();
    }

    /**
     * Converts the link to a JSON-String
     * @return JSON-String representation of this link
     */
    public String toJson() {
        StringBuilder s = new StringBuilder("{");

        s.append(String.format("sourceId: %s, ", getNodeA().getId()));
        s.append(String.format("targetId: %s, ", getNodeB().getId()));

        if (color != null) {
            s.append(String.format("color: %s, ", getColor()));
        }

        if (getLabel() != null) {
            s.append(String.format("label: \"%s\", ", getLabel()));
        } else {
            s.append("label: \"\", ");
        }

        if (!isDeletable())
            s.append("deletable: false, ");

        if (!isLabelEditable())
            s.append("labelEditable: false, ");

        s.append("}");
        return s.toString();
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o){
        if(!o.getClass().equals(this.getClass())) return false;
        if(!this.getNodeA().equals(((AigLink)o).getNodeA())) return false;
        return this.getNodeB().equals(((AigLink) o).getNodeB());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode(){
        return this.getNodeB().hashCode() + 7 * this.getNodeA().hashCode() + 13 * this.getLabel().hashCode();
    }
}
