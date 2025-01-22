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
package org.tweetyproject.arg.adf.reasoner.sat.encodings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.interpretation.TwoValuedInterpretationIterator;
import org.tweetyproject.arg.adf.semantics.link.Link;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.transform.FixPartialTransformer;
import org.tweetyproject.arg.adf.transform.TseitinTransformer;

/**
 * The {@code KBipolarSatEncoding} class provides an encoding of a bipolar argumentation framework into a
 * propositional formula for SAT solving. It implements the {@link SatEncoding} interface and processes
 * the Abstract Dialectical Framework (ADF) along with a propositional mapping of arguments.
 * The encoding considers the undecided arguments and uses Tseitin transformation to generate the
 * propositional formula that is fed into a SAT solver.
 *
 * @author Mathias Hofer
 */
public final class KBipolarSatEncoding implements SatEncoding {

	/** The abstract dialectical framework containing arguments and links. */
	private final AbstractDialecticalFramework adf;

	/** The propositional mapping of arguments and their corresponding literals. */
	private final PropositionalMapping mapping;

	/**
	 * Constructs a {@code KBipolarSatEncoding} instance with the given ADF and propositional mapping.
	 *
	 * @param adf the abstract dialectical framework, must not be null
	 * @param mapping the propositional mapping used for the encoding, must not be null
	 * @throws NullPointerException if {@code adf} or {@code mapping} is null
	 */
	public KBipolarSatEncoding(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		this.adf = Objects.requireNonNull(adf);
		this.mapping = Objects.requireNonNull(mapping);
	}

	/**
	 * Encodes the acceptance conditions of the arguments in the ADF into a SAT formula using a given consumer.
	 * The encoding involves generating clauses based on the undecided arguments and their dependencies,
	 * and applying Tseitin transformation to handle complex acceptance conditions.
	 *
	 * @param consumer the consumer to accept the generated SAT clauses
	 */
	@Override
	public void encode(Consumer<Clause> consumer) {
		for (Argument to : adf.getArguments()) {
			Set<Argument> undecidedDependees = dependsOn(to);

			if (!undecidedDependees.isEmpty()) {
				Iterator<Interpretation> completionIterator = new TwoValuedInterpretationIterator(List.copyOf(undecidedDependees));

				while (completionIterator.hasNext()) {
					Interpretation completion = completionIterator.next();

					AcceptanceCondition acc = adf.getAcceptanceCondition(to);
					AcceptanceCondition simplified = new FixPartialTransformer(completion).transform(acc);

					TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(from -> mapping.getLink(from, to), false);
					Literal accName = transformer.collect(simplified, consumer);

					// The condition when the checks are active, they are trivially satisfied if the current interpretation does not match the completion
					Collection<Literal> condition = new ArrayList<>();
					for(Argument from : completion.arguments()) {
						if (completion.satisfied(from)) {
							condition.add(mapping.getFalse(from));
						} else {
							condition.add(mapping.getTrue(from));
						}
					}

					// The check for s^t
					consumer.accept(Clause.of(condition, mapping.getTrue(to).neg(), accName));

					// The check for s^f
					consumer.accept(Clause.of(condition, mapping.getFalse(to).neg(), accName.neg()));
				}
			}
		}
	}

	/**
	 * Finds and returns the set of arguments on which the given argument depends.
	 * An argument depends on another argument if there exists a dependent link between them.
	 *
	 * @param s the argument whose dependencies are to be found
	 * @return a set of arguments that the given argument depends on, based on the dependent links
	 */
	private Set<Argument> dependsOn(Argument s) {
		return adf.linksTo(s).stream()
				.filter(l -> l.getType().isDependent())
				.map(Link::getFrom)
				.collect(Collectors.toSet());
	}
}

