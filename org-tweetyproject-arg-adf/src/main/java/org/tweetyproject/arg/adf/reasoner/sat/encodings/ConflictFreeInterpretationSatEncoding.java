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
 * @author Mathias Hofer
 *
 */
public class ConflictFreeInterpretationSatEncoding implements SatEncoding, RelativeSatEncoding {
	
	private final AbstractDialecticalFramework adf;
	
	private final PropositionalMapping mapping;
	
	/**
	 * @param adf
	 * @param mapping
	 */
	public ConflictFreeInterpretationSatEncoding(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		this.adf = Objects.requireNonNull(adf);
		this.mapping = Objects.requireNonNull(mapping);
	}

	@Override
	public void encode(Consumer<Clause> consumer) {
		for (Argument s : adf.getArguments()) {
			handleUnfixed(consumer, s);
		}
	}
	
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
	
	private void handleUnfixed(Consumer<Clause> consumer, Argument s) {
		AcceptanceCondition acc = adf.getAcceptanceCondition(s);
		
		TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(r -> mapping.getLink(adf.link(r, s)), false);
		Literal accName = transformer.collect(acc, consumer);
		
		// the propositions represent the assignment of s
		Literal trueRepr = mapping.getTrue(s);
		Literal falseRepr = mapping.getFalse(s);

		// link the arguments to their acceptance conditions
		consumer.accept(Clause.of(trueRepr.neg(), accName));
		consumer.accept(Clause.of(falseRepr.neg(), accName.neg()));

		// draw connection between argument and outgoing links
		for (Link relation : adf.linksFrom(s)) {
			Literal linkRepr = mapping.getLink(relation);

			consumer.accept(Clause.of(trueRepr.neg(), linkRepr));
			consumer.accept(Clause.of(falseRepr.neg(), linkRepr.neg()));
		}

		// make sure that we never satisfy s_t and s_f at the same time
		consumer.accept(Clause.of(trueRepr.neg(), falseRepr.neg()));
	}
	
	private void handleSatisfied(Consumer<Clause> consumer, Argument s) {
		AcceptanceCondition acc = adf.getAcceptanceCondition(s);
		
		TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(r -> mapping.getLink(adf.link(r, s)), true);
		Literal accName = transformer.collect(acc, consumer);

		consumer.accept(Clause.of(accName));
		
		consumer.accept(Clause.of(mapping.getTrue(s)));
		consumer.accept(Clause.of(mapping.getFalse(s).neg()));
		
		for (Link relation : adf.linksFrom(s)) {
			consumer.accept(Clause.of(mapping.getLink(relation)));
		}
	}
	
	private void handleUnsatisfied(Consumer<Clause> consumer, Argument s) {
		AcceptanceCondition acc = adf.getAcceptanceCondition(s);
		
		TseitinTransformer transformer = TseitinTransformer.ofNegativePolarity(r -> mapping.getLink(adf.link(r, s)), true);
		Literal accName = transformer.collect(acc, consumer);

		consumer.accept(Clause.of(accName.neg()));
		
		consumer.accept(Clause.of(mapping.getTrue(s).neg()));
		consumer.accept(Clause.of(mapping.getFalse(s)));
		
		for (Link relation : adf.linksFrom(s)) {
			consumer.accept(Clause.of(mapping.getLink(relation).neg()));
		}
	}
	
	private void handleUndecided(Consumer<Clause> consumer, Argument s) {
		consumer.accept(Clause.of(mapping.getTrue(s).neg()));
		consumer.accept(Clause.of(mapping.getFalse(s).neg()));
	}

}
