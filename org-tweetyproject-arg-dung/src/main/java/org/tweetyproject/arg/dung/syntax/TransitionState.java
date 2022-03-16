package org.tweetyproject.arg.dung.syntax;

import org.tweetyproject.arg.dung.reasoner.WeaklyAdmissibleReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;

/**
 * A state of the transition system for serialised semantics
 * It consists of an argumentation framework and a set of arguments
 */
public class TransitionState {
    private DungTheory theory;
    private Extension<DungTheory> extension;

    public TransitionState(DungTheory theory, Extension<DungTheory> extension) {
        this.theory = theory;
        this.extension = extension;
    }

    /**
     * compute the successor state for the given extension
     * @param ext a set of arguments that has been selected by a selection function
     * @return the successor state
     */
    public TransitionState getNext(Extension<DungTheory> ext) {
        DungTheory reduct = new WeaklyAdmissibleReasoner().getReduct(this.theory, ext);
        Extension<DungTheory> newExt = new Extension<>(this.extension);
        newExt.addAll(ext);

        return new TransitionState(reduct, newExt);
    }

    public DungTheory getTheory() {
        return theory;
    }

    public Extension<DungTheory> getExtension() {
        return extension;
    }
}