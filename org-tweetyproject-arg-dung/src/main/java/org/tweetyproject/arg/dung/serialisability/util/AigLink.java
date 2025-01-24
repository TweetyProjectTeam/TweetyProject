package org.tweetyproject.arg.dung.serialisability.util;

import org.tweetyproject.graphs.DirectedEdge;

public class AigLink extends DirectedEdge<AigNode> {

    private boolean deletable = true;
    private boolean labelEditable = true;

    private String color;

    public AigLink(AigNode source, AigNode target) {
        super(source, target);
    }

    public AigLink(AigNode source, AigNode target, String label) {
        super(source, target, label);
    }

    public String toJson(boolean deletable, boolean labelEditable) {
        this.setDeletable(deletable);
        this.setLabelEditable(labelEditable);
        return toJson();
    }
    public String toJson() {
        StringBuilder s = new StringBuilder("{");

        s.append(String.format("sourceId: %s, ", getSourceId()));
        s.append(String.format("targetId: %s, ", getTargetId()));

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

    public int getSourceId() {
        return getNodeA().getId();
    }

    public int getTargetId() {
        return getNodeB().getId();
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
