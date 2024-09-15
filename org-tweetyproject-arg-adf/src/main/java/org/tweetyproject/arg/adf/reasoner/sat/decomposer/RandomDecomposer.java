package org.tweetyproject.arg.adf.reasoner.sat.decomposer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * The {@code RandomDecomposer} is a concrete implementation of the
 * {@link AbstractDecomposer}, designed to randomly partition the arguments
 * of an {@link AbstractDialecticalFramework} (ADF).
 * <p>
 * The decomposition randomly shuffles the arguments in the ADF and then
 * selects a subset of them based on the specified count. This allows for
 * random partitions of the argument set, which can be useful for certain
 * applications like random sampling or stochastic analysis of argumentation
 * frameworks.
 * </p>


 * <p>
 * Usage of this class involves instantiating it with a specific ADF and then
 * calling the decomposition methods provided by the superclass
 * {@link AbstractDecomposer}.
 * </p>
 *
 * @author Sebastian Matthias Thimm
 */
public final class RandomDecomposer extends AbstractDecomposer {

    /**
     * Constructs a {@code RandomDecomposer} for the specified
     * {@link AbstractDialecticalFramework}.
     *
     * @param adf the Abstract Dialectical Framework to decompose, must not be
     *            {@code null}
     */
    public RandomDecomposer(AbstractDialecticalFramework adf) {
        super(adf);
    }

    /**
     * Randomly partitions the arguments in the given ADF into a subset of the
     * specified size.
     * <p>
     * This method shuffles the list of arguments in the ADF and selects the
     * first {@code count} arguments as a random partition.
     * </p>
     *
     * @param adf the Abstract Dialectical Framework containing the arguments
     *            to partition
     * @param count the number of arguments to include in the partition, must
     *              be non-negative and less than or equal to the number of
     *              arguments in the ADF
     * @return a randomly selected subset of arguments from the ADF
     * @throws IllegalArgumentException if {@code count} is negative or exceeds
     *         the number of arguments in the ADF
     */
    @Override
    Set<Argument> partition(AbstractDialecticalFramework adf, int count) {
        List<Argument> list = new ArrayList<>(adf.getArguments());
        Collections.shuffle(list);
        return Set.copyOf(list.subList(0, count));
    }

}

