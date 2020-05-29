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
import java.util.function.Function;

import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.transform.TseitinTransformer;
import net.sf.tweety.arg.adf.util.CacheMap;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;

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
	public void encode(Consumer<Disjunction> consumer, PropositionalMapping context, AbstractDialecticalFramework adf) {
		Function<Argument, Proposition> argumentMapping = new CacheMap<>(arg -> new Proposition(arg.getName()));
		TseitinTransformer transformer = TseitinTransformer.builder(argumentMapping).build();
		for (Argument a : adf.getArguments()) {
			Proposition accName = transformer.collect(adf.getAcceptanceCondition(a), consumer);
			Proposition arg = argumentMapping.apply(a);
			consumer.accept(new Disjunction(new Negation(accName), arg));
			consumer.accept(new Disjunction(accName, new Negation(arg)));
			
			// the propositions represent the assignment of a
			Proposition trueRepr = context.getTrue(a);
			Proposition falseRepr = context.getFalse(a);

			// link arg to trues and falses representation, s.t. it stays
			// compatible with the other (three-valued) encodings
			consumer.accept(new Disjunction(new Negation(arg), trueRepr));
			consumer.accept(new Disjunction(arg, falseRepr));
			consumer.accept(new Disjunction(new Negation(trueRepr), new Negation(falseRepr)));
		}
	}

}
