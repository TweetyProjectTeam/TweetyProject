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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * Fixes the two valued assignments and tries to find a two valued assignment for at least one of the undecided ones.
 * 
 * @author Mathias Hofer
 *
 */
public class LargerInterpretationSatEncoding implements SatEncoding {
	
	private final Interpretation interpretation;
	
	/**
	 * @param interpretation the interpretation which is used as a lower bound
	 */
	public LargerInterpretationSatEncoding(Interpretation interpretation) {
		this.interpretation = Objects.requireNonNull(interpretation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.arg.adf.reasoner.strategy.sat.SatEncoding#encode(org.tweetyproject.arg.
	 * adf.reasoner.sat.SatEncodingContext)
	 */
	@Override
	public void encode(Consumer<Clause> consumer, PropositionalMapping context, AbstractDialecticalFramework adf) {
		// fix the already decided arguments
		for (Argument a : interpretation.satisfied()) {
			consumer.accept(Clause.of(context.getTrue(a)));
		}
		for (Argument a : interpretation.unsatisfied()) {
			consumer.accept(Clause.of(context.getFalse(a)));
		}

		// guess a not yet decided argument
		Set<Literal> undecided = new HashSet<>();
		for (Argument a : interpretation.undecided()) {
			undecided.add(context.getTrue(a));
			undecided.add(context.getFalse(a));
		}
		consumer.accept(Clause.of(undecided));
	}

}
