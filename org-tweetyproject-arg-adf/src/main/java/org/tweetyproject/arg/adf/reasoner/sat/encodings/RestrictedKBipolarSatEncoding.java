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
 * The {@code RestrictedKBipolarSatEncoding} class implements a SAT encoding
 * specifically for restricted bipolar Abstract Dialectical Frameworks (ADFs).
 * It leverages partial interpretations and completes undecided arguments
 * in a two-valued manner. The class uses Tseitin transformations to generate
 * propositional clauses.
 * <p>
 * This encoding is restricted because it only applies the encoding to arguments
 * that have undecided dependencies. It also filters completions where the
 * interpretation contradicts the partial interpretation.
 * </p>
 *
 * @author Mathias Hofer
 *
 */
public final class RestrictedKBipolarSatEncoding implements SatEncoding {

	private final AbstractDialecticalFramework adf;

	private final PropositionalMapping mapping;

	private final Interpretation partial;

	/**
	 * Constructs a new RestrictedKBipolarSatEncoding with the specified Abstract Dialectical Framework,
	 * propositional mapping, and partial interpretation.
	 *
	 * @param adf the Abstract Dialectical Framework
	 * @param mapping the propositional mapping for arguments and links
	 * @param partial the partial interpretation to be used during encoding
	 */
	public RestrictedKBipolarSatEncoding(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation partial) {
		this.adf = Objects.requireNonNull(adf);
		this.mapping = Objects.requireNonNull(mapping);
		this.partial = Objects.requireNonNull(partial);
	}

	/**
	 * Encodes the bipolar Abstract Dialectical Framework (ADF) into a set of SAT clauses.
	 * The encoding is restricted to arguments that have undecided dependencies. The encoding
	 * applies the Tseitin transformation to convert the acceptance conditions of these arguments
	 * into propositional clauses.
	 *
	 * @param consumer the consumer that accepts the generated SAT clauses
	 */
	@Override
	public void encode(Consumer<Clause> consumer) {
		for (Argument to : adf.getArguments()) {
			if (partial.undecided(to)) continue;

			Set<Argument> undecidedDependees = dependsOn(to);

			if (!undecidedDependees.isEmpty()) {
				Iterator<Interpretation> completionIterator = new TwoValuedInterpretationIterator(List.copyOf(undecidedDependees));

				while (completionIterator.hasNext()) {
					Interpretation completion = completionIterator.next();

					if(checkCompletion(completion)) continue; // clauses trivially true

					AcceptanceCondition acc = adf.getAcceptanceCondition(to);
					AcceptanceCondition simplified = new FixPartialTransformer(completion).transform(acc);

					TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(from -> mapping.getLink(from, to), false);
					if (partial.satisfied(to)) {
						transformer = TseitinTransformer.ofPositivePolarity(from -> mapping.getLink(from, to), true);
					} else if (partial.unsatisfied(to)) {
						transformer = TseitinTransformer.ofNegativePolarity(from -> mapping.getLink(from, to), true);
					}

					Literal accName = transformer.collect(simplified, consumer);

					// the condition when the checks are active, they are trivially satisfied if the current interpretation does not match the completion
					Collection<Literal> condition = new ArrayList<>();
					for(Argument from : completion.arguments()) {
						if (completion.satisfied(from)) {
							condition.add(mapping.getFalse(from));
						} else {
							condition.add(mapping.getTrue(from));
						}
					}

					// the check for s^t
					if (!partial.unsatisfied(to)) {
						consumer.accept(Clause.of(condition, mapping.getTrue(to).neg(), accName));
					}

					// the check for s^f
					if (!partial.satisfied(to)) {
						consumer.accept(Clause.of(condition, mapping.getFalse(to).neg(), accName.neg()));
					}
				}
			}
		}

	}

	/**
	 * Checks whether the given completion contradicts the partial interpretation.
	 *
	 * @param completion the completion to check
	 * @return {@code true} if the completion contradicts the partial interpretation, {@code false} otherwise
	 */
	private boolean checkCompletion(Interpretation completion) {
		for (Argument arg : partial.arguments()) {
			if (partial.satisfied(arg) && completion.unsatisfied(arg) || partial.unsatisfied(arg) && completion.satisfied(arg)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the set of arguments {@code r} such that there exists a dependent link (r,s)
	 * where {@code s} is the given argument.
	 *
	 * @param s the argument for which to find dependent links
	 * @return the set of arguments that have a dependent link to the given argument
	 */
	private Set<Argument> dependsOn(Argument s) {
		return adf.linksTo(s).stream()
				.filter(l -> l.getType().isDependent())
				.map(Link::getFrom)
				.collect(Collectors.toSet());
	}

}
