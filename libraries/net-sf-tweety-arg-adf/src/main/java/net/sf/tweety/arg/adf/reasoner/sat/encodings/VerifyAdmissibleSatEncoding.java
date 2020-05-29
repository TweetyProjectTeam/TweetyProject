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
import java.util.function.Function;

import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.transform.TseitinTransformer;
import net.sf.tweety.arg.adf.util.CacheMap;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * 
 * @author Mathias Hofer
 *
 */
public class VerifyAdmissibleSatEncoding implements SatEncoding {

	private final Interpretation interpretation;

	private final Proposition toggle;
	
	/**
	 * @param interpretation the interpretation to verify
	 */
	public VerifyAdmissibleSatEncoding(Interpretation interpretation) {
		this(interpretation, null);
	}

	/**
	 * 
	 * @param interpretation the interpretation to verify
	 * @param toggle the toggle which is used to activate the encoding
	 */
	public VerifyAdmissibleSatEncoding(Interpretation interpretation, Proposition toggle) {
		this.interpretation = Objects.requireNonNull(interpretation);
		this.toggle = toggle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncoding#encode(net.sf.
	 * tweety.arg. adf.reasoner.sat.SatEncodingContext)
	 */
	@Override
	public void encode(Consumer<Disjunction> consumer, PropositionalMapping context, AbstractDialecticalFramework adf) {
		Function<Argument, Proposition> vars = new CacheMap<Argument, Proposition>(s -> new Proposition(s.getName()));
		Disjunction accs = new Disjunction();
		addToggle(accs);
		
		TseitinTransformer.Builder builder = TseitinTransformer.builder(vars).setOptimize(true);
		for (Argument s : interpretation.satisfied()) {
			TseitinTransformer transformer = builder.setTopLevelPolarity(-1).build();
			Proposition accName = transformer.collect(adf.getAcceptanceCondition(s), consumer);
			Disjunction clause = new Disjunction();
			clause.add(vars.apply(s));
			addToggle(clause);
			consumer.accept(clause);
			accs.add(new Negation(accName));
		}
		
		for (Argument s : interpretation.unsatisfied()) {
			TseitinTransformer transformer = builder.setTopLevelPolarity(1).build();
			Proposition accName = transformer.collect(adf.getAcceptanceCondition(s), consumer);
			Disjunction clause = new Disjunction();
			clause.add(new Negation(vars.apply(s)));
			addToggle(clause);
			consumer.accept(clause);
			accs.add(accName);
		}
		
		consumer.accept(accs);
	}
	
	private void addToggle(Disjunction disjunction) {
		if (toggle != null) {
			disjunction.add(toggle);
		}
	}

}
