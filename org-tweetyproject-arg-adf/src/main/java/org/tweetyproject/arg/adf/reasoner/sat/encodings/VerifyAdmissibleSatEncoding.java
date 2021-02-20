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
import java.util.function.Function;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.transform.TseitinTransformer;
import org.tweetyproject.arg.adf.util.CacheMap;

/**
 * 
 * @author Mathias Hofer
 *
 */
public class VerifyAdmissibleSatEncoding implements SatEncoding {

	private final Interpretation interpretation;
	
	/**
	 * 
	 * @param interpretation the interpretation to verify
	 */
	public VerifyAdmissibleSatEncoding(Interpretation interpretation) {
		this.interpretation = Objects.requireNonNull(interpretation);
	}

	@Override
	public void encode(Consumer<Clause> consumer, AbstractDialecticalFramework adf, PropositionalMapping context) {
		encode(interpretation, consumer, context, adf);
	}
	
	public static void encode(Interpretation interpretation, Consumer<Clause> consumer, PropositionalMapping context, AbstractDialecticalFramework adf) {
		Set<Literal> accs = new HashSet<>();
		
		Function<Argument, Literal> fn = new CacheMap<>(arg -> Literal.create(arg.getName()));
				
		TseitinTransformer negativeTransformer = TseitinTransformer.ofNegativePolarity(fn, true);
		for (Argument s : interpretation.satisfied()) {
			Literal accName = negativeTransformer.collect(adf.getAcceptanceCondition(s), consumer);
			consumer.accept(Clause.of(fn.apply(s)));
			accs.add(accName.neg());
		}
		
		TseitinTransformer positiveTransformer = TseitinTransformer.ofPositivePolarity(fn, true);
		for (Argument s : interpretation.unsatisfied()) {
			Literal accName = positiveTransformer.collect(adf.getAcceptanceCondition(s), consumer);
			consumer.accept(Clause.of(fn.apply(s).neg()));
			accs.add(accName);
		}

		consumer.accept(Clause.of(accs));
	}

}
