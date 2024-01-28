package org.tweetyproject.arg.dung.serialisability.syntax;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.Node;

/**
 * TODO this should probably store a partial serialisation sequence instead of an extension
 */
public class SerialisationNode implements Node {
    /** theory */
    private DungTheory theory;
    /** extension */
    private Extension<DungTheory> extension;
    /** true if this node represents a valid extension */
    private boolean terminal;

    /**
     *
     * @param theory a dung theory
     * @param extension an extension
     */
    public SerialisationNode(DungTheory theory, Extension<DungTheory> extension) {
        this.theory = theory;
        this.extension = extension;
    }

    /**
     * @return the theory
     */
    public DungTheory getTheory() {
        return theory;
    }

    /**
     * @return the extension
     */
    public Extension<DungTheory> getExtension() {
        return extension;
    }

    /**
     *
     * @return true, if the set of arguments of this node is an extension
     */
    public boolean isTerminal() {
        return terminal;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if(!(obj instanceof SerialisationNode))
            return false;

        var other = (SerialisationNode) obj;
        return this.extension.equals(other.extension);
    }

    @Override
    public int hashCode() {
        final int prime = 67;
        int result = 1;
        result = prime * result
                + ((theory == null) ? 0 : theory.hashCode());
        result = prime * result
                + ((extension == null) ? 0 : extension.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return extension.toString();
    }
}
