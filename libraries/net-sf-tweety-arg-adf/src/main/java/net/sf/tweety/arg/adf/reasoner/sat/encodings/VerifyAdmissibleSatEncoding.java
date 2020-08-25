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
import java.util.function.Function;

import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.pl.Atom;
import net.sf.tweety.arg.adf.syntax.pl.Clause;
import net.sf.tweety.arg.adf.syntax.pl.Literal;
import net.sf.tweety.arg.adf.syntax.pl.Negation;
import net.sf.tweety.arg.adf.transform.TseitinTransformer;
import net.sf.tweety.arg.adf.util.CacheMap;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncoding#encode(net.sf.
	 * tweety.arg. adf.reasoner.sat.SatEncodingContext)
	 */
	@Override
	public void encode(Consumer<Clause> consumer, PropositionalMapping context, AbstractDialecticalFramework adf) {
		Set<Literal> accs = new HashSet<>();
		
		Function<Argument, Atom> fn = new CacheMap<>(arg -> Atom.of(arg.getName()));
				
		TseitinTransformer negativeTransformer = TseitinTransformer.ofNegativePolarity(fn, true);
		for (Argument s : interpretation.satisfied()) {
			Atom accName = negativeTransformer.collect(adf.getAcceptanceCondition(s), consumer);
			consumer.accept(Clause.of(fn.apply(s)));
			accs.add(new Negation(accName));
		}
		
		TseitinTransformer positiveTransformer = TseitinTransformer.ofPositivePolarity(fn, true);
		for (Argument s : interpretation.unsatisfied()) {
			Atom accName = positiveTransformer.collect(adf.getAcceptanceCondition(s), consumer);
			consumer.accept(Clause.of(new Negation(fn.apply(s))));
			accs.add(accName);
		}

		consumer.accept(Clause.of(accs));
	}
	
	public void encode(Consumer<Clause> consumer, PropositionalMapping context, AbstractDialecticalFramework adf, Atom toggle) {
		Set<Literal> accs = new HashSet<>();
		accs.add(toggle);
		
		Function<Argument, Atom> fn = new CacheMap<>(arg -> Atom.of(arg.getName()));
				
		TseitinTransformer negativeTransformer = TseitinTransformer.ofNegativePolarity(fn, true);
		for (Argument s : interpretation.satisfied()) {
			Atom accName = negativeTransformer.collect(adf.getAcceptanceCondition(s), consumer);
			consumer.accept(Clause.of(fn.apply(s)));
			accs.add(new Negation(accName));
		}
		
		TseitinTransformer positiveTransformer = TseitinTransformer.ofPositivePolarity(fn, true);
		for (Argument s : interpretation.unsatisfied()) {
			Atom accName = positiveTransformer.collect(adf.getAcceptanceCondition(s), consumer);
			consumer.accept(Clause.of(new Negation(fn.apply(s))));
			accs.add(accName);
		}

		consumer.accept(Clause.of(accs));
	}

}
