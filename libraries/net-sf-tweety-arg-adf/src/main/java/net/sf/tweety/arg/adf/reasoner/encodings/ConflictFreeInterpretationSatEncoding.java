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
import java.util.List;

import net.sf.tweety.arg.adf.semantics.Link;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.transform.TseitinTransformer;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * @author Mathias Hofer
 *
 */
public class ConflictFreeInterpretationSatEncoding implements SatEncoding {

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
		List<Disjunction> encoding = new LinkedList<Disjunction>();
		for (Argument s : adf.getArguments()) {
			AcceptanceCondition acc = adf.getAcceptanceCondition(s);
			
			TseitinTransformer transformer = new TseitinTransformer(r -> context.getLinkRepresentation(adf.link(r, s)), false);
			Proposition accName = transformer.collect(acc, encoding);
			
			// the propositions represent the assignment of s
			Proposition trueRepr = context.getTrueRepresentation(s);
			Proposition falseRepr = context.getFalseRepresentation(s);

			// link the arguments to their acceptance conditions
			Disjunction acSat = new Disjunction(new Negation(trueRepr), accName);
			Disjunction acUnsat = new Disjunction(new Negation(falseRepr), new Negation(accName));
			encoding.add(acSat);
			encoding.add(acUnsat);

			// draw connection between argument and outgoing links
			for (Link relation : adf.linksFrom(s)) {
				Proposition linkRepr = context.getLinkRepresentation(relation);

				encoding.add(new Disjunction(new Negation(trueRepr), linkRepr));
				encoding.add(new Disjunction(new Negation(falseRepr), new Negation(linkRepr)));
			}

			// make sure that we never satisfy s_t and s_f at the same time
			Disjunction eitherTrueOrFalse = new Disjunction(new Negation(trueRepr), new Negation(falseRepr));
			encoding.add(eitherTrueOrFalse);
		}

		return encoding;
	}

}
