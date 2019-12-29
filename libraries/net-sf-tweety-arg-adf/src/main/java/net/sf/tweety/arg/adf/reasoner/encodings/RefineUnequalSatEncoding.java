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
import java.util.Collections;
import java.util.stream.Stream;

import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;

/**
 * @author Mathias Hofer
 *
 */
public class RefineUnequalSatEncoding implements SatEncoding {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncoding#encode(net.sf.tweety.arg.
	 * adf.reasoner.sat.SatEncodingContext)
	 */
	@Override
	public Collection<Disjunction> encode(SatEncodingContext context, Interpretation interpretation) {
		Disjunction encoding = new Disjunction();
		interpretation.satisfied().map(context::getTrueRepresentation).map(Negation::new).forEach(encoding::add);
		interpretation.unsatisfied().map(context::getFalseRepresentation).map(Negation::new).forEach(encoding::add);
		interpretation.undecided()
				.flatMap(x -> Stream.of(context.getTrueRepresentation(x), context.getFalseRepresentation(x)))
				.forEach(encoding::add);
		return Collections.singleton(encoding);
	}

}
