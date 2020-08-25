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

import java.util.function.Consumer;

import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.pl.Atom;
import net.sf.tweety.arg.adf.syntax.pl.Clause;
import net.sf.tweety.arg.adf.syntax.pl.Negation;
import net.sf.tweety.arg.adf.transform.TseitinTransformer;

/**
 * @author Mathias Hofer
 *
 */
public class TwoValuedModelSatEncoding implements SatEncoding {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncoding#encode(net.sf.tweety.arg.
	 * adf.reasoner.sat.SatEncodingContext)
	 */
	@Override
	public void encode(Consumer<Clause> consumer, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(mapping::getTrue, false);
		for (Argument arg : adf.getArguments()) {			
			Atom accName = transformer.collect(adf.getAcceptanceCondition(arg), consumer);
			
			// arg = true iff the acceptance condition holds
			consumer.accept(Clause.of(new Negation(accName), mapping.getTrue(arg)));
			consumer.accept(Clause.of(accName, new Negation(mapping.getTrue(arg))));

			// arg != true implies arg = false
			consumer.accept(Clause.of(mapping.getTrue(arg), mapping.getFalse(arg)));
		}
	}

}
