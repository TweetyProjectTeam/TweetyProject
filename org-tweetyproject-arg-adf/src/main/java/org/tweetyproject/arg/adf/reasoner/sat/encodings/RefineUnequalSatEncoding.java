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
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * @author Mathias Hofer
 *
 */
public class RefineUnequalSatEncoding implements RelativeSatEncoding {

	private final PropositionalMapping mapping;

	public RefineUnequalSatEncoding(PropositionalMapping mapping) {
		this.mapping = Objects.requireNonNull(mapping);
	}

	@Override
	public void encode(Consumer<Clause> consumer, Interpretation interpretation) {
		Set<Literal> clause = new HashSet<>();
		for (Argument arg : interpretation.satisfied()) {
			clause.add(mapping.getTrue(arg).neg());
		}
		for (Argument arg : interpretation.unsatisfied()) {
			clause.add(mapping.getFalse(arg).neg());
		}
		for (Argument arg : interpretation.undecided()) {
			clause.add(mapping.getTrue(arg));
			clause.add(mapping.getFalse(arg));
		}
		consumer.accept(Clause.of(clause));
	}

}
