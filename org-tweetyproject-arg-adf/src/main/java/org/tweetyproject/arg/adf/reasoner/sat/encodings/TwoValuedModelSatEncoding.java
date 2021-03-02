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

import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.transform.TseitinTransformer;

/**
 * @author Mathias Hofer
 *
 */
public class TwoValuedModelSatEncoding implements SatEncoding {

	@Override
	public void encode(Consumer<Clause> consumer, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(mapping::getTrue, false);
		for (Argument arg : adf.getArguments()) {			
			Literal accName = transformer.collect(adf.getAcceptanceCondition(arg), consumer);
			
			// arg = true iff the acceptance condition holds
			consumer.accept(Clause.of(accName.neg(), mapping.getTrue(arg)));
			consumer.accept(Clause.of(accName, mapping.getTrue(arg).neg()));

			// arg != true implies arg = false
			consumer.accept(Clause.of(mapping.getTrue(arg), mapping.getFalse(arg)));
		}
	}

}
