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
package net.sf.tweety.arg.adf.reasoner.sat.encodings;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.pl.Clause;
import net.sf.tweety.arg.adf.syntax.pl.Literal;
import net.sf.tweety.arg.adf.syntax.pl.Negation;

/**
 * @author Mathias Hofer
 *
 */
public class RefineUnequalSatEncoding implements SatEncoding {
	
	private final Interpretation interpretation;

	public RefineUnequalSatEncoding(Interpretation interpretation) {
		this.interpretation = Objects.requireNonNull(interpretation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncoding#encode(net.sf.tweety.arg.
	 * adf.reasoner.sat.SatEncodingContext)
	 */
	@Override
	public void encode(Consumer<Clause> consumer, PropositionalMapping context, AbstractDialecticalFramework adf) {
		Set<Literal> clause = new HashSet<>();
		for (Argument arg : interpretation.satisfied()) {
			clause.add(new Negation(context.getTrue(arg)));
		}
		for (Argument arg : interpretation.unsatisfied()) {
			clause.add(new Negation(context.getFalse(arg)));
		}
		for (Argument arg : interpretation.undecided()) {
			clause.add(context.getTrue(arg));
			clause.add(context.getFalse(arg));
		}
		consumer.accept(Clause.of(clause));
	}

}
