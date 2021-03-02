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

import java.util.function.Consumer;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;

/**
 * @author Mathias Hofer
 *
 */
public final class FixThreeValuedPartialSatEncoding implements SatEncoding {

	private final Interpretation partial;

	public FixThreeValuedPartialSatEncoding(Interpretation partial) {
		this.partial = partial;
	}

	@Override
	public void encode(Consumer<Clause> consumer, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		encode(partial, consumer, mapping, adf);
	}
	
	public static void encode(Interpretation partial, Consumer<Clause> consumer, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		for (Argument a : partial.satisfied()) {
			consumer.accept(Clause.of(mapping.getTrue(a)));
			consumer.accept(Clause.of(mapping.getFalse(a).neg()));
		}

		for (Argument a : partial.unsatisfied()) {
			consumer.accept(Clause.of(mapping.getFalse(a)));
			consumer.accept(Clause.of(mapping.getTrue(a).neg()));
		}

		for (Argument a : partial.undecided()) {
			consumer.accept(Clause.of(mapping.getFalse(a).neg()));
			consumer.accept(Clause.of(mapping.getTrue(a).neg()));
		}
	}

}
