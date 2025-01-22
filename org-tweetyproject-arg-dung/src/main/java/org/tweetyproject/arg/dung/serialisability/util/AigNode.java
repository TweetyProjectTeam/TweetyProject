package org.tweetyproject.arg.dung.serialisability.util;

public class AigNode {
    private final int id;
    private String label;
    private int x = -1;
    private int y = -1;
    private String color;

    private boolean deletable = true;
    private boolean labelEditable = true;
    private boolean allowOutgoingLinks = true;
    private boolean fixedPositionX = false;
    private boolean fixedPositionY = false;

    public AigNode(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public String toJson(boolean deletable, boolean labelEditable, boolean allowOutgoingLinks, boolean fixedPositionX, boolean fixedPositionY) {
        this.setDeletable(deletable);
        this.setLabelEditable(labelEditable);
        this.setAllowOutgoingLinks(allowOutgoingLinks);
        this.setFixedPositionX(fixedPositionX);
        this.setFixedPositionY(fixedPositionY);
        return this.toJson();
    }

    public String toJson() {
        StringBuilder s = new StringBuilder("{");
        s.append(String.format("id: %s, ", getId()));
        s.append(String.format("label: \"%s\", ", getLabel()));

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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
}
