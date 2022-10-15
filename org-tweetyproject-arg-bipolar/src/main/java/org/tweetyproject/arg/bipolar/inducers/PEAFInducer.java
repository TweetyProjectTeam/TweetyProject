package org.tweetyproject.arg.bipolar.inducers;

import org.tweetyproject.arg.bipolar.syntax.InducibleEAF;

import java.util.function.Consumer;

/**
 * The interface for PEAFInducers, these create a subset of the graph that is generated from the given PEAF
 *
 * @author Taha Dogan Gunes
 */
public interface PEAFInducer {
    /**
     * Inducer induces inducibleEAFs and gives to a consumer function
     *
     * @param consumer the function that consumes InducibleEAFs
     */
    void induce(Consumer<InducibleEAF> consumer);
}
