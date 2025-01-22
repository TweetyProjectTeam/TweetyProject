package org.tweetyproject.arg.dung.serialisability.util;

public class AigLink {
    private final int sourceId;
    private final int targetId;
    private String label;

    private boolean deletable = true;
    private boolean labelEditable = true;

    public AigLink(int sourceId, int targetId) {
        this(sourceId, targetId, "");
    }

    public AigLink(int sourceId, int targetId, String label) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.label = label;
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

        s.append(String.format("label: \"%s\", ", getLabel()));

        if (!isDeletable())
            s.append("deletable: false, ");

        if (!isLabelEditable())
            s.append("labelEditable: false, ");

        s.append("}");
        return s.toString();
    }

    public int getSourceId() {
        return sourceId;
    }

    public int getTargetId() {
        return targetId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
}
