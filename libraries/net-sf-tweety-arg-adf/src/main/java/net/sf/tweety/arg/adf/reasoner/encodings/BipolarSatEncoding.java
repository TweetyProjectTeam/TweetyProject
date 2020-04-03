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

import net.sf.tweety.arg.adf.semantics.Link;
import net.sf.tweety.arg.adf.semantics.LinkType;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * @author Mathias Hofer
 *
 */
public class BipolarSatEncoding implements SatEncoding {

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
		Collection<Disjunction> encoding = new LinkedList<Disjunction>();
		for (Argument r : adf.getArguments()) {
			Proposition rTrue = context.getTrueRepresentation(r);
			Proposition rFalse = context.getFalseRepresentation(r);
			for (Link l : adf.linksTo(r)) {
				// TODO what about redundant?
				if (l.getLinkType() == LinkType.ATTACKING) {
					// first implication
					Disjunction clause1 = new Disjunction();
					clause1.add(new Negation(rTrue));
					clause1.add(context.getFalseRepresentation(l.getFrom()));
					clause1.add(context.getLinkRepresentation(l));
					encoding.add(clause1);

					// second implication
					Disjunction clause2 = new Disjunction();
					clause2.add(new Negation(rFalse));
					clause2.add(context.getTrueRepresentation(l.getFrom()));
					clause2.add(new Negation(context.getLinkRepresentation(l)));
					encoding.add(clause2);
				} else if (l.getLinkType() == LinkType.SUPPORTING) {
					// first implication
					Disjunction clause1 = new Disjunction();
					clause1.add(new Negation(rTrue));
					clause1.add(context.getTrueRepresentation(l.getFrom()));
					clause1.add(new Negation(context.getLinkRepresentation(l)));
					encoding.add(clause1);

					// second implication
					Disjunction clause2 = new Disjunction();
					clause2.add(new Negation(rFalse));
					clause2.add(context.getFalseRepresentation(l.getFrom()));
					clause2.add(context.getLinkRepresentation(l));
					encoding.add(clause2);
				}
			}
		}
		return encoding;
	}

}
