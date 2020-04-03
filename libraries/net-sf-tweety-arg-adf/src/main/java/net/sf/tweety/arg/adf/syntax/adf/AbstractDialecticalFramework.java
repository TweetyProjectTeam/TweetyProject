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
package net.sf.tweety.arg.adf.syntax.adf;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import net.sf.tweety.arg.adf.semantics.Link;
import net.sf.tweety.arg.adf.semantics.LinkStrategy;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition;
import net.sf.tweety.arg.adf.transform.Transformer;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.SingleSetSignature;

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
public interface AbstractDialecticalFramework extends BeliefBase {

	static AbstractDialecticalFramework empty() {
		return EmptyAbstractDialecticalFramework.INSTANCE;
	}

	static Builder builder() {
		return new GraphAbstractDialecticalFramework.GraphBuilder();
	}

	static Builder fromMap(Map<Argument, AcceptanceCondition> map) {
		Builder builder = new GraphAbstractDialecticalFramework.GraphBuilder();
		for (Entry<Argument, AcceptanceCondition> entry : map.entrySet()) {
			builder.add(entry.getKey(), entry.getValue());
		}
		return builder;
	}

	/**
	 * Allows to add, remove and change links, arguments and acceptance
	 * conditions of the provided adf.
	 * <p>
	 * Since {@link AbstractDialecticalFramework} is immutable, it does not
	 * change the adf directly, the resulting adf however behaves as if this was
	 * the case.
	 * <p>
	 * The Builder tries as hard as it can to keep the overhead as low as
	 * possible by reusing already computed links. This works especially well if
	 * we only add new arguments. If we change the acceptance condition of some
	 * argument, then we obviously must compute a new link.
	 * 
	 * @param adf
	 * @return
	 */
	static Builder modify(AbstractDialecticalFramework adf) {
		return null;
	}
	
	/**
	 * Allows to add arguments and acceptance conditions. Throws an exception if
	 * any other operation is performed on the ADF.
	 * 
	 * @param adf
	 * @return
	 */
	static Builder extend(AbstractDialecticalFramework adf) {
		return null;
	}
	
	/**
	 * Allows to remove arguments and acceptance conditions. Throws an exception
	 * if any other operation is performed on the ADF.
	 * 
	 * @param adf
	 * @return
	 */
	static Builder reduce(AbstractDialecticalFramework adf) {
		return null;
	}

	/**
	 * Creates a new {@link AbstractDialecticalFramework} with transformed acceptance conditions.
	 * 
	 * @param adf
	 * @param transformer
	 * @param linkStrategy
	 * @return
	 */
	static AbstractDialecticalFramework transformed(AbstractDialecticalFramework adf,
			Transformer<AcceptanceCondition> transformer, LinkStrategy linkStrategy) {
		Builder builder = new GraphAbstractDialecticalFramework.GraphBuilder().eager(linkStrategy);
		for (Argument arg : adf.getArguments()) {
			AcceptanceCondition acc = adf.getAcceptanceCondition(arg);
			builder.add(arg, transformer.transform(acc));
		}
		return builder.build();
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
	 * @param a
	 * @param b
	 * @throws IllegalArgumentException if the adf does not contain a link (parent, child)
	 * @return the link (parent, child)
	 */
	Link link(Argument parent, Argument child);

	/**
	 * 
	 * @param child
	 * @return a set of links (parent, child)
	 */
	Set<Link> linksTo(Argument child);

	/**
	 * 
	 * @param parent
	 * @return a set of links (parent, child)
	 */
	Set<Link> linksFrom(Argument parent);

	Set<Argument> parents(Argument child);

	Set<Argument> children(Argument parent);
	
	boolean contains(Argument arg);

	/**
	 * Guaranteed to be non-null if the ADF contains the argument.
	 * 
	 * @param argument
	 * @throws IllegalArgumentException if the argument is not contained in the ADF
	 * @return
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.commons.BeliefBase#getMinimalSignature()
	 */
	@Override
	default Signature getMinimalSignature() {
		return new Signature(getArguments());
	}

	interface Builder {

		Builder lazy(LinkStrategy linkStrategy);

		Builder provided();

		Builder eager(LinkStrategy linkStrategy);

		Builder add(Argument arg, AcceptanceCondition acc);
		
		Builder add(Link link);
		
		Builder remove(Argument arg);
		
		AbstractDialecticalFramework build();

	}

	static class Signature extends SingleSetSignature<Argument> {

		/**
		 * @param formulas
		 */
		public Signature(Set<Argument> formulas) {
			super(formulas);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see net.sf.tweety.commons.Signature#add(java.lang.Object)
		 */
		@Override
		public void add(Object obj) {
			if (obj instanceof Argument) {
				formulas.add((Argument) obj);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see net.sf.tweety.commons.SingleSetSignature#clone()
		 */
		@Override
		public SingleSetSignature<Argument> clone() {
			return new Signature(new HashSet<>(formulas));
		}

	}

}
