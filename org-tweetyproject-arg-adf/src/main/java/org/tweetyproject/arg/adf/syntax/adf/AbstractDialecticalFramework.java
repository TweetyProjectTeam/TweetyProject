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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.tweetyproject.arg.adf.semantics.link.Link;
import org.tweetyproject.arg.adf.semantics.link.LinkStrategy;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.transform.Transformer;

/**
 * The implementing subtypes must ensure the following properties:
 * <ul>
 * 	<li>Immutability</li>
 * 	<li>All methods return a non-null value if its parameters, e.g. arguments, are from this ADF</li>
 * 	<li>If a method returns a collection or stream, all its elements are non-null</li>
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
public interface AbstractDialecticalFramework {

	static AbstractDialecticalFramework empty() {
		return EmptyAbstractDialecticalFramework.INSTANCE;
	}

	static Builder builder() {
		return new GraphAbstractDialecticalFramework.Builder();
	}

	static Builder fromMap(Map<Argument, AcceptanceCondition> map) {
		Builder builder = new GraphAbstractDialecticalFramework.Builder();
		for (Entry<Argument, AcceptanceCondition> entry : map.entrySet()) {
			builder.add(entry.getKey(), entry.getValue());
		}
		return builder;
	}
	
	/**
	 * Creates a copy of the given AbstractDialecticalFramework, which can be modified before build() is called.
	 * 
	 * @param adf the ADF to copy
	 * @return the builder initialized with the values of the original ADF
	 */
	static Builder copy(AbstractDialecticalFramework adf) {
		Builder builder = new GraphAbstractDialecticalFramework.Builder();
		for (Argument arg : adf.getArguments()) {
			builder.add(arg, adf.getAcceptanceCondition(arg));
		}
		// TODO: rethink this, we may not want to compute links here - which may be the case if we call links()
		for (Link link : adf.links()) {
			builder.add(link);
		}
		return builder;
	}

	/**
	 * Creates a new {@link AbstractDialecticalFramework} with transformed acceptance conditions.
	 * 
	 * @param adf the ADF
	 * @param transformer the transformed to use
	 * @return the builder which is initialized with the transformed acceptance conditions
	 */
	static Builder transform(AbstractDialecticalFramework adf,
			Transformer<AcceptanceCondition> transformer) {
		Builder builder = new GraphAbstractDialecticalFramework.Builder();
		for (Argument arg : adf.getArguments()) {
			AcceptanceCondition acc = adf.getAcceptanceCondition(arg);
			builder.add(arg, transformer.transform(acc));
		}
		return builder;
	}
	
	default int size() {
		return getArguments().size();
	}

	/**
	 * 
	 * @return an unmodifiable set of all the arguments
	 */
	Set<Argument> getArguments();

	/**
	 * If the caller just consumes some links, this method should be used.
	 * Depending on the implementation, the links may be computed lazily and
	 * therefore we may avoid unnecessary computation.
	 * 
	 * @return a stream of the links of this adf
	 */
	default Stream<Link> linksStream() {
		return links().stream();
	}

	/**
	 * If the caller just consumes some of the links, the method
	 * {@link #linksStream()} should be preferred to this one. Depending on the
	 * implementation, the links may be computed lazily and therefore we may
	 * avoid unnecessary computation.
	 * 
	 * @return an unmodifiable set of all the links
	 */
	Set<Link> links();

	/**
	 * Computes the link (parent, child) iff necessary and returns it
	 * afterwards.
	 * 
	 * @param parent the parent
	 * @param child the child
	 * @throws IllegalArgumentException if the adf does not contain a link (parent, child)
	 * @return the link (parent, child)
	 */
	Link link(Argument parent, Argument child);

	/**
	 * 
	 * @param child the child
	 * @return a set of links (parent, child)
	 */
	Set<Link> linksTo(Argument child);

	/**
	 * 
	 * @param parent the parent
	 * @return a set of links (parent, child)
	 */
	Set<Link> linksFrom(Argument parent);

	Set<Argument> parents(Argument child);

	Set<Argument> children(Argument parent);
	
	int outgoingDegree(Argument arg);
	
	int incomingDegree(Argument arg);
	
	boolean contains(Argument arg);

	/**
	 * Guaranteed to be non-null if the ADF contains the argument.
	 * 
	 * @param argument some argument of this ADF
	 * @throws IllegalArgumentException if the argument is not contained in the ADF
	 * @return the found acceptance condition, never <code>null</code>
	 */
	AcceptanceCondition getAcceptanceCondition(Argument argument);

	/**
	 * Checks if the ADF is bipolar. May compute all links to do so.
	 * 
	 * @return true iff all of the links are bipolar
	 */
	default boolean bipolar() {
		return kBipolar() == 0;
	}

	/**
	 * Returns the count k of non-bipolar links, which makes this ADF k-bipolar.
	 * 
	 * @return k
	 */
	int kBipolar();

	interface Builder {

		Builder lazy(LinkStrategy linkStrategy);

		Builder provided();

		Builder eager(LinkStrategy linkStrategy);

		Builder add(Argument arg, AcceptanceCondition acc);
		
		Builder add(Link link);
		
		Builder remove(Argument arg);
		
		AbstractDialecticalFramework build();

	}

}
