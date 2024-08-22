/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.syntax.adf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.tweetyproject.arg.adf.io.KppADFFormatParser;
import org.tweetyproject.arg.adf.reasoner.query.SemanticsStep;
import org.tweetyproject.arg.adf.reasoner.sat.query.SatQueryBuilder;
import org.tweetyproject.arg.adf.sat.solver.NativeMinisatSolver;
import org.tweetyproject.arg.adf.semantics.link.Link;
import org.tweetyproject.arg.adf.semantics.link.LinkStrategy;
import org.tweetyproject.arg.adf.semantics.link.SatLinkStrategy;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.transform.Transformer;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.graphs.Graph;
/**
 * The implementing subtypes must ensure the following properties:
 * <ul>
 *  <li>Immutability</li>
 *  <li>All methods return a non-null value if its parameters, e.g. arguments, are from this ADF</li>
 *  <li>If a method returns a collection or stream, all its elements are non-null</li>
 * </ul>
 * This makes the usage of {@link AbstractDialecticalFramework} implementations
 * more convenient to the user, since it avoids unnecessary nullchecks and
 * therefore leads to more readable code. Immutability should lead to more
 * robust code, since an ADF always remains in a valid state after creation. It
 * makes it also easier to use in a parallel context.
 *
 * @author Mathias Hofer
 *
 */
public interface AbstractDialecticalFramework extends Graph<Argument>, Collection<Argument>, ArgumentationFramework<Argument> {

    /**
     * Returns an empty {@link AbstractDialecticalFramework}.
     * This can be used to create an ADF with no arguments or links.
     *
     * @return an empty AbstractDialecticalFramework instance
     */
    static AbstractDialecticalFramework empty() {
        return EmptyAbstractDialecticalFramework.INSTANCE;
    }

    /**
     * Returns a builder for constructing a new {@link AbstractDialecticalFramework}.
     * The builder allows for adding arguments, acceptance conditions, and links incrementally.
     *
     * @return a builder for constructing an AbstractDialecticalFramework
     */
    static Builder builder() {
        return new GraphAbstractDialecticalFramework.Builder();
    }

    /**
     * Reads an {@link AbstractDialecticalFramework} from a file.
     * The file is parsed into an ADF using a format parser.
     *
     * @param file the file containing the ADF definition
     * @return an AbstractDialecticalFramework parsed from the file
     * @throws FileNotFoundException if the file is not found
     * @throws IOException if an I/O error occurs while reading the file
     */
    static AbstractDialecticalFramework fromFile(File file) throws FileNotFoundException, IOException {
        return new KppADFFormatParser(new SatLinkStrategy(new NativeMinisatSolver()), true).parse(file);
    }

    /**
     * Creates a builder from a map of arguments and their corresponding acceptance conditions.
     *
     * @param map a map of arguments to acceptance conditions
     * @return a builder for constructing an AbstractDialecticalFramework from the given map
     */
    static Builder fromMap(Map<Argument, AcceptanceCondition> map) {
        Builder builder = new GraphAbstractDialecticalFramework.Builder();
        for (Entry<Argument, AcceptanceCondition> entry : map.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder;
    }

    /**
     * Creates a new {@link AbstractDialecticalFramework} with acceptance conditions transformed
     * using the provided function.
     *
     * @param transformer the transformer to apply to each acceptance condition
     * @return a new ADF with transformed acceptance conditions
     */
    AbstractDialecticalFramework transform(Function<AcceptanceCondition, AcceptanceCondition> transformer);

    /**
     * Creates a new {@link AbstractDialecticalFramework} with acceptance conditions transformed
     * based on both the argument and its acceptance condition.
     *
     * @param transformer the transformer to apply to each acceptance condition and argument
     * @return a new ADF with transformed acceptance conditions
     */
    AbstractDialecticalFramework transform(BiFunction<Argument, AcceptanceCondition, AcceptanceCondition> transformer);

    /**
     * Creates a new {@link AbstractDialecticalFramework} with acceptance conditions transformed
     * using a {@link Transformer}.
     *
     * @param transformer the transformer to apply to each acceptance condition
     * @return a new ADF with transformed acceptance conditions
     */
    AbstractDialecticalFramework transform(Transformer<AcceptanceCondition> transformer);

    /**
     * Initiates a query on this {@link AbstractDialecticalFramework} using the default configuration.
     * The returned {@link SemanticsStep} allows further refinement of the query.
     *
     * @return a SemanticsStep to refine the query
     */
    default SemanticsStep query() {
        return new SatQueryBuilder(this).defaultConfiguration();
    }

    /**
     * Returns the number of arguments in this {@link AbstractDialecticalFramework}.
     *
     * @return the number of arguments in this ADF
     */
    default int size() {
        return getArguments().size();
    }

    /**
     * Returns an unmodifiable set of all the arguments in this {@link AbstractDialecticalFramework}.
     *
     * @return an unmodifiable set of arguments
     */
    Set<Argument> getArguments();

    /**
     * Returns a stream of the links in this {@link AbstractDialecticalFramework}.
     * This method is preferable if the caller only needs to consume some of the links.
     *
     * @return a stream of links in this ADF
     */
    default Stream<Link> linksStream() {
        return links().stream();
    }

    /**
     * Returns an unmodifiable set of all the links in this {@link AbstractDialecticalFramework}.
     * This method returns all the links and should be used when access to all links is needed.
     *
     * @return an unmodifiable set of links in this ADF
     */
    Set<Link> links();

    /**
     * Computes and returns the link between the given parent and child arguments.
     *
     * @param parent the parent argument
     * @param child the child argument
     * @throws IllegalArgumentException if the ADF does not contain a link between the parent and child
     * @return the link between the parent and child
     */
    Link link(Argument parent, Argument child);

    /**
     * Returns a set of links directed towards the specified child argument.
     *
     * @param child the target argument
     * @return a set of links pointing to the given argument
     */
    Set<Link> linksTo(Argument child);

    /**
     * Returns a set of links originating from the specified parent argument.
     *
     * @param parent the source argument
     * @return a set of links originating from the given argument
     */
    Set<Link> linksFrom(Argument parent);

    /**
     * Returns a set of parent arguments for the given child argument.
     *
     * @param child the target argument
     * @return a set of parent arguments
     */
    Set<Argument> parents(Argument child);

    /**
     * Returns a set of child arguments for the given parent argument.
     *
     * @param parent the source argument
     * @return a set of child arguments
     */
    Set<Argument> children(Argument parent);

    /**
     * Returns the number of outgoing links from the given argument.
     *
     * @param arg the argument
     * @return the number of outgoing links
     */
    int outgoingDegree(Argument arg);

    /**
     * Returns the number of incoming links to the given argument.
     *
     * @param arg the argument
     * @return the number of incoming links
     */
    int incomingDegree(Argument arg);

    /**
     * Checks if the given argument is contained in this {@link AbstractDialecticalFramework}.
     *
     * @param arg the argument to check
     * @return true if the argument is contained, false otherwise
     */
    boolean contains(Argument arg);

    /**
     * Retrieves the acceptance condition for the specified argument.
     * The acceptance condition is guaranteed to be non-null if the argument is present in the ADF.
     *
     * @param argument the argument for which to retrieve the acceptance condition
     * @throws IllegalArgumentException if the argument is not contained in the ADF
     * @return the acceptance condition for the given argument
     */
    AcceptanceCondition getAcceptanceCondition(Argument argument);

    /**
     * Checks if the ADF is bipolar, meaning all links are either supporting or attacking.
     *
     * @return true if the ADF is bipolar, false otherwise
     */
    default boolean bipolar() {
        return kBipolar() == 0;
    }

    /**
     * Returns the number of non-bipolar links, indicating the degree of bipolarity in this ADF.
     *
     * @return the count of non-bipolar links
     */
    int kBipolar();

    /**
     * Builder interface for constructing instances of {@link AbstractDialecticalFramework}.
     *
     * @author Sebastian
     *
     */
    interface Builder {

        Builder lazy(LinkStrategy linkStrategy);

        Builder provided();

        Builder eager(LinkStrategy linkStrategy);

        /**
         * Adds an argument and its acceptance condition to the ADF.
         *
         * @param arg the argument to add
         * @param acc the acceptance condition of the argument
         * @return this Builder instance
         */
        Builder add(Argument arg, AcceptanceCondition acc);

        /**
         * Adds a link between arguments to the ADF.
         *
         * @param link the link to add
         * @return this Builder instance
         */
        Builder add(Link link);

        Builder remove(Argument arg);

        /**
         * Builds and returns the constructed {@link AbstractDialecticalFramework}.
         *
         * @return the constructed AbstractDialecticalFramework
         */
        AbstractDialecticalFramework build();
    }
}
