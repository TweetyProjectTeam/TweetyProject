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

import java.util.function.Consumer;

import org.tweetyproject.arg.adf.semantics.link.Link;
import org.tweetyproject.arg.adf.semantics.link.LinkType;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * @author Mathias Hofer
 *
 */
public class BipolarSatEncoding implements SatEncoding {

	@Override
	public void encode(Consumer<Clause> consumer, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		for (Argument r : adf.getArguments()) {
			Literal rTrue = mapping.getTrue(r);
			Literal rFalse = mapping.getFalse(r);
			for (Link l : adf.linksTo(r)) {
				Literal link = mapping.getLink(l);
				if (l.getType() == LinkType.ATTACKING) {
					consumer.accept(Clause.of(rTrue.neg(), mapping.getFalse(l.getFrom()), link));
					consumer.accept(Clause.of(rFalse.neg(), mapping.getTrue(l.getFrom()), link.neg()));
				} else if (l.getType() == LinkType.SUPPORTING) {
					consumer.accept(Clause.of(rTrue.neg(), mapping.getTrue(l.getFrom()), link.neg()));
					consumer.accept(Clause.of(rFalse.neg(), mapping.getFalse(l.getFrom()), link));
				}
			}
		}
	}

}
