package org.tweetyproject.web.services.serialisation;

import org.tweetyproject.arg.dung.reasoner.SerialisedExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.serialisability.syntax.SelectionFunction;
import org.tweetyproject.arg.dung.serialisability.syntax.TerminationFunction;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.web.services.Callee;

import java.util.Collection;

/**
 * Callee for the getModels operation of the SerialisedExtensionReasoner for the given selection and termination functions
 */
public class SerialisationGetModelsCallee extends Callee {
    /** The DungTheory on which the evaluation is performed */
    private final DungTheory bbase;

    /** The selection function to use */
    private final SelectionFunction selectionFunction;

    /** The termination function to use */
    private final TerminationFunction terminationFunction;

    /**
     * Constructs a new SerialisationGetModelsCallee with the specified DungTheory and functions.
     *
     * @param bbase     The base DungTheory on which the serialisation is performed
     * @param alpha     The selection function to be used
     * @param beta      The termination function to be used
     */
    public SerialisationGetModelsCallee(DungTheory bbase, SelectionFunction alpha, TerminationFunction beta) {
        this.bbase = bbase;
        this.selectionFunction = alpha;
        this.terminationFunction = beta;
    }

    /**
     * Executes the getModels operation using the specified DungTheory the fitting serialised reasoner.
     *
     * @return the set of models
     * @throws Exception If an error occurs during the getModels operation
     */
    @Override
    public Collection<Extension<DungTheory>> call() throws Exception {
        SerialisedExtensionReasoner reasoner = new SerialisedExtensionReasoner(this.selectionFunction, this.terminationFunction);
        return reasoner.getModels(this.bbase);
    }
}
