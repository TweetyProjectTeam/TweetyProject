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

import java.util.Objects;
import java.util.function.Consumer;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.link.Link;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.transform.TseitinTransformer;

/**
 * This class implements a SAT encoding for conflict-free interpretations in an Abstract Dialectical Framework (ADF).
 * It ensures that no argument is both satisfied and unsatisfied simultaneously, and properly links arguments
 * with their acceptance conditions using Tseitin transformation.
 * <p>
 * The encoding also supports both absolute and relative encoding based on the current interpretation.
 * </p>
 *
 * @author Mathias Hofer
 */
public class ConflictFreeInterpretationSatEncoding implements SatEncoding, RelativeSatEncoding {

	/** The Abstract Dialectical Framework (ADF) that this encoding is based on. */
	private final AbstractDialecticalFramework adf;

	/** The propositional mapping used to map arguments and links to propositional literals. */
	private final PropositionalMapping mapping;

	/**
	 * Constructs a new ConflictFreeInterpretationSatEncoding for the given Abstract Dialectical Framework (ADF)
	 * and propositional mapping.
	 *
	 * @param adf the Abstract Dialectical Framework for which the SAT encoding is created, must not be null
	 * @param mapping the propositional mapping for the arguments and links, must not be null
	 */
	public ConflictFreeInterpretationSatEncoding(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		this.adf = Objects.requireNonNull(adf);
		this.mapping = Objects.requireNonNull(mapping);
	}

	/**
	 * Encodes the conflict-free interpretation of the given ADF into a set of SAT clauses.
	 * These clauses ensure that no argument is both satisfied and unsatisfied at the same time
	 * and links arguments to their acceptance conditions.
	 *
	 * @param consumer the consumer that accepts the generated SAT clauses
	 */
	@Override
	public void encode(Consumer<Clause> consumer) {
		for (Argument s : adf.getArguments()) {
			handleUnfixed(consumer, s);
		}
	}

	/**
	 * Encodes the conflict-free interpretation of the given ADF into a set of SAT clauses
	 * based on the provided interpretation. This method adapts the encoding depending on
	 * whether an argument is satisfied, unsatisfied, or undecided.
	 *
	 * @param consumer the consumer that accepts the generated SAT clauses
	 * @param interpretation the current interpretation of the ADF
	 */
	@Override
	public void encode(Consumer<Clause> consumer, Interpretation interpretation) {
		for (Argument s : adf.getArguments()) {
			if (interpretation.satisfied(s)) {
				handleSatisfied(consumer, s);
			} else if (interpretation.unsatisfied(s)) {
				handleUnsatisfied(consumer, s);
			} else if (interpretation.undecided(s)) {
				handleUndecided(consumer, s);
			} else {
				handleUnfixed(consumer, s);
			}
		}
	}

	/**
	 * Handles the case where an argument is unfixed (i.e., it has not been determined whether
	 * the argument is satisfied or unsatisfied). It creates the appropriate SAT clauses linking
	 * the argument to its acceptance condition and ensures that no conflicting assignments are made.
	 *
	 * @param consumer the consumer that accepts the generated SAT clauses
	 * @param s the argument being processed
	 */
	private void handleUnfixed(Consumer<Clause> consumer, Argument s) {
		AcceptanceCondition acc = adf.getAcceptanceCondition(s);

		TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(r -> mapping.getLink(r, s), false);
		Literal accName = transformer.collect(acc, consumer);

		// The propositions represent the assignment of s
		Literal trueRepr = mapping.getTrue(s);
		Literal falseRepr = mapping.getFalse(s);

		// Link the arguments to their acceptance conditions
		consumer.accept(Clause.of(trueRepr.neg(), accName));
		consumer.accept(Clause.of(falseRepr.neg(), accName.neg()));

		// Ensure that no conflicting assignments are made
		for (Link relation : adf.linksFrom(s)) {
			Literal linkRepr = mapping.getLink(relation);

			consumer.accept(Clause.of(trueRepr.neg(), linkRepr));
			consumer.accept(Clause.of(falseRepr.neg(), linkRepr.neg()));
		}

		// Ensure that both trueRepr and falseRepr cannot be true at the same time
		consumer.accept(Clause.of(trueRepr.neg(), falseRepr.neg()));
	}

	/**
	 * Handles the case where an argument is satisfied in the interpretation.
	 * It creates the appropriate SAT clauses that link the argument's satisfaction
	 * to its acceptance condition and outgoing links.
	 *
	 * @param consumer the consumer that accepts the generated SAT clauses
	 * @param s the satisfied argument being processed
	 */
	private void handleSatisfied(Consumer<Clause> consumer, Argument s) {
		AcceptanceCondition acc = adf.getAcceptanceCondition(s);

		TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(r -> mapping.getLink(r, s), true);
		Literal accName = transformer.collect(acc, consumer);

		// Generate clauses for satisfied argument
		consumer.accept(Clause.of(accName));
		consumer.accept(Clause.of(mapping.getTrue(s)));
		consumer.accept(Clause.of(mapping.getFalse(s).neg()));

		// Link outgoing relations
		for (Link relation : adf.linksFrom(s)) {
			consumer.accept(Clause.of(mapping.getLink(relation)));
		}
	}

	/**
	 * Handles the case where an argument is unsatisfied in the interpretation.
	 * It creates the appropriate SAT clauses that link the argument's unsatisfaction
	 * to its acceptance condition and outgoing links.
	 *
	 * @param consumer the consumer that accepts the generated SAT clauses
	 * @param s the unsatisfied argument being processed
	 */
	private void handleUnsatisfied(Consumer<Clause> consumer, Argument s) {
		AcceptanceCondition acc = adf.getAcceptanceCondition(s);

		TseitinTransformer transformer = TseitinTransformer.ofNegativePolarity(r -> mapping.getLink(r, s), true);
		Literal accName = transformer.collect(acc, consumer);

		// Generate clauses for unsatisfied argument
		consumer.accept(Clause.of(accName.neg()));
		consumer.accept(Clause.of(mapping.getTrue(s).neg()));
		consumer.accept(Clause.of(mapping.getFalse(s)));

		// Link outgoing relations
		for (Link relation : adf.linksFrom(s)) {
			consumer.accept(Clause.of(mapping.getLink(relation).neg()));
		}
	}

	/**
	 * Handles the case where an argument is undecided in the interpretation.
	 * It creates the appropriate SAT clauses indicating that both the satisfied
	 * and unsatisfied literals are negated.
	 *
	 * @param consumer the consumer that accepts the generated SAT clauses
	 * @param s the undecided argument being processed
	 */
	private void handleUndecided(Consumer<Clause> consumer, Argument s) {
		consumer.accept(Clause.of(mapping.getTrue(s).neg()));
		consumer.accept(Clause.of(mapping.getFalse(s).neg()));
	}
}

