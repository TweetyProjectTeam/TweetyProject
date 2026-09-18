package org.tweetyproject.web.services.serialisation;

import org.tweetyproject.arg.dung.reasoner.SerialisedExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationSequence;
import org.tweetyproject.arg.dung.serialisability.syntax.SelectionFunction;
import org.tweetyproject.arg.dung.serialisability.syntax.TerminationFunction;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.web.services.Callee;

import java.util.Collection;

/**
 * Callee for the getSequences operation of the SerialisedExtensionReasoner for the given selection and termination functions
 */
public class SerialisationGetSequencesCallee extends Callee {
    /**
     * The DungTheory on which the evaluation is performed
     */
    private final DungTheory bbase;

    /**
     * The selection function to use
     */
    private final SelectionFunction selectionFunction;

    /**
     * The termination function to use
     */
    private final TerminationFunction terminationFunction;

    /**
     * Constructs a new SerialisationGetSequencesCallee with the specified DungTheory and functions.
     *
     * @param bbase The base DungTheory on which the serialisation is performed
     * @param alpha The selection function to be used
     * @param beta  The termination function to be used
     */
    public SerialisationGetSequencesCallee(DungTheory bbase, SelectionFunction alpha, TerminationFunction beta) {
        this.bbase = bbase;
        this.selectionFunction = alpha;
        this.terminationFunction = beta;
    }

    /**
     * Executes the getSequences operation using the specified DungTheory the fitting serialised reasoner.
     *
     * @return the set of sequences
     * @throws Exception If an error occurs during the getSequences operation
     */
    @Override
    public Collection<SerialisationSequence> call() throws Exception {
        SerialisedExtensionReasoner reasoner = new SerialisedExtensionReasoner(this.selectionFunction, this.terminationFunction);
        return reasoner.getSequences(this.bbase);
    }
}