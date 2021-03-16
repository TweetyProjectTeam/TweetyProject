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
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.pl.Clause;

/**
 * Fixes the already assigned true/false values.
 * 
 * @author Mathias Hofer
 *
 */
public class FixPartialSatEncoding implements RelativeSatEncoding {
	
	private final PropositionalMapping mapping;
	
	/**
	 * @param mapping
	 */
	public FixPartialSatEncoding(PropositionalMapping mapping) {
		this.mapping = Objects.requireNonNull(mapping);
	}

	@Override
	public void encode(Consumer<Clause> consumer, Interpretation interpretation) {
		for (Argument a : interpretation.satisfied()) {
			consumer.accept(Clause.of(mapping.getTrue(a)));
		}

		for (Argument a : interpretation.unsatisfied()) {
			consumer.accept(Clause.of(mapping.getFalse(a)));
		}
	}

}
