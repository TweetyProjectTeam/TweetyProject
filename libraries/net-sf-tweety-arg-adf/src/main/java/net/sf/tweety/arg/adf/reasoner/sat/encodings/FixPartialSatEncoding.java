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

import java.util.Objects;
import java.util.function.Consumer;

import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * Fixes the already assigned true/false values.
 * 
 * @author Mathias Hofer
 *
 */
public class FixPartialSatEncoding implements SatEncoding {
	
	private final Interpretation interpretation;
	
	private final Proposition toggle;
		
	/**
	 * @param interpretation the interpretation which is used to fix values
	 */
	public FixPartialSatEncoding(Interpretation interpretation) {
		this(interpretation, null);
	}

	/**
	 * @param interpretation the interpretation which is used to fix values
	 * @param toggle the toggle to activate the encoding
	 */
	public FixPartialSatEncoding(Interpretation interpretation, Proposition toggle) {
		this.interpretation = Objects.requireNonNull(interpretation);
		this.toggle = toggle;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncoding#encode(net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncodingContext)
	 */
	@Override
	public void encode(Consumer<Disjunction> consumer, PropositionalMapping context, AbstractDialecticalFramework adf) {
		for (Argument a : interpretation.satisfied()) {
			Disjunction clause = new Disjunction();
			clause.add(context.getTrue(a));
			addToggle(clause);
			consumer.accept(clause);
		}

		for (Argument a : interpretation.unsatisfied()) {
			Disjunction clause = new Disjunction();
			clause.add(context.getFalse(a));
			addToggle(clause);
			consumer.accept(clause);
		}
	}
	
	private void addToggle(Disjunction disjunction) {
		if (toggle != null) {
			disjunction.add(toggle);
		}
	}

}
