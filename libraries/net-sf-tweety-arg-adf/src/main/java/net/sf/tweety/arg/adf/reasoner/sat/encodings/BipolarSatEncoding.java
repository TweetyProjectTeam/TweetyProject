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

import net.sf.tweety.arg.adf.semantics.link.Link;
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
	public void encode(Consumer<Disjunction> consumer, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		for (Argument r : adf.getArguments()) {
			Proposition rTrue = mapping.getTrue(r);
			Proposition rFalse = mapping.getFalse(r);
			for (Link l : adf.linksTo(r)) {				
				if (l.getType().isAttacking()) {
					// first implication
					Disjunction clause1 = new Disjunction();
					clause1.add(new Negation(rTrue));
					clause1.add(mapping.getFalse(l.getFrom()));
					clause1.add(mapping.getLink(l));
					consumer.accept(clause1);

					// second implication
					Disjunction clause2 = new Disjunction();
					clause2.add(new Negation(rFalse));
					clause2.add(mapping.getTrue(l.getFrom()));
					clause2.add(new Negation(mapping.getLink(l)));
					consumer.accept(clause2);
				}  else if (l.getType().isSupporting()) {
					// first implication
					Disjunction clause1 = new Disjunction();
					clause1.add(new Negation(rTrue));
					clause1.add(mapping.getTrue(l.getFrom()));
					clause1.add(new Negation(mapping.getLink(l)));
					consumer.accept(clause1);

					// second implication
					Disjunction clause2 = new Disjunction();
					clause2.add(new Negation(rFalse));
					clause2.add(mapping.getFalse(l.getFrom()));
					clause2.add(mapping.getLink(l));
					consumer.accept(clause2);
				}
			}
		}
	}

}
