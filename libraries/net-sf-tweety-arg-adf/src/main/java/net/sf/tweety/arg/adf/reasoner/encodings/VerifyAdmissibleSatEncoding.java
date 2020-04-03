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
package net.sf.tweety.arg.adf.reasoner.encodings;

import java.util.Collection;
import java.util.LinkedList;

import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.transform.TseitinTransformer;
import net.sf.tweety.arg.adf.util.Cache;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * @author Mathias Hofer
 *
 */
public class VerifyAdmissibleSatEncoding implements SatEncoding {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncoding#encode(net.sf.tweety.arg.
	 * adf.reasoner.sat.SatEncodingContext)
	 */
	@Override
	public Collection<Disjunction> encode(SatEncodingContext context, Interpretation interpretation) {
		AbstractDialecticalFramework adf = context.getAbstractDialecticalFramework();
		
		Cache<Argument, Proposition> vars = new Cache<Argument, Proposition>(s -> new Proposition(s.getName()));
		Collection<Disjunction> encoding = new LinkedList<Disjunction>();
		Disjunction accs = new Disjunction();
		TseitinTransformer transformer = new TseitinTransformer(vars, false);
		for (Argument s : adf.getArguments()) {
			Proposition accName = transformer.collect(adf.getAcceptanceCondition(s), encoding);
			if (interpretation.satisfied(s)) {
				Disjunction clause = new Disjunction();
				clause.add(vars.apply(s));
				encoding.add(clause);
				accs.add(new Negation(accName));
			} else if (interpretation.unsatisfied(s)) {
				Disjunction clause = new Disjunction();
				clause.add(new Negation(vars.apply(s)));
				encoding.add(clause);
				accs.add(accName);
			}
		}
		encoding.add(accs);
		return encoding;
	}

}
