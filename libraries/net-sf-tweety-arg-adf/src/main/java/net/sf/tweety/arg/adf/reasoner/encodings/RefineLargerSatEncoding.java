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

import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.logics.pl.syntax.Disjunction;

/**
 * TODO work out difference to {@link LargerInterpretationSatEncoding} in documentation
 * 
 * @author Mathias Hofer
 *
 */
public class RefineLargerSatEncoding implements SatEncoding {

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
		for (Argument arg : interpretation.satisfied()) {
			encoding.add(context.getFalseRepresentation(arg));
		}
		for (Argument arg : interpretation.unsatisfied()) {
			encoding.add(context.getTrueRepresentation(arg));
		}
		for (Argument arg : interpretation.undecided()) {
			encoding.add(context.getTrueRepresentation(arg));
			encoding.add(context.getFalseRepresentation(arg));
		}
		return Collections.singleton(encoding);
	}

}
