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
import net.sf.tweety.arg.adf.semantics.link.LinkType;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.pl.Atom;
import net.sf.tweety.arg.adf.syntax.pl.Clause;
import net.sf.tweety.arg.adf.syntax.pl.Negation;

/**
 * @author Mathias Hofer
 *
 */
public class BipolarSatEncoding implements SatEncoding {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncoding#encode(net.sf.
	 * tweety.arg. adf.reasoner.sat.SatEncodingContext)
	 */
	@Override
	public void encode(Consumer<Clause> consumer, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		for (Argument r : adf.getArguments()) {
			Atom rTrue = mapping.getTrue(r);
			Atom rFalse = mapping.getFalse(r);
			for (Link l : adf.linksTo(r)) {
				Atom link = mapping.getLink(l);
				if (l.getType() == LinkType.ATTACKING) {
					consumer.accept(Clause.of(new Negation(rTrue), mapping.getFalse(l.getFrom()), link));
					consumer.accept(Clause.of(new Negation(rFalse), mapping.getTrue(l.getFrom()), new Negation(link)));
				} else if (l.getType() == LinkType.SUPPORTING) {
					consumer.accept(Clause.of(new Negation(rTrue), mapping.getTrue(l.getFrom()), new Negation(link)));
					consumer.accept(Clause.of(new Negation(rFalse), mapping.getFalse(l.getFrom()), link));
				}
			}
		}
	}

}
