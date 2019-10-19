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
package net.sf.tweety.arg.adf.reasoner.strategy.sat;

import java.util.Collection;
import java.util.LinkedList;

import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.logics.pl.syntax.Disjunction;

/**
 * @author Mathias Hofer
 *
 */
public class LargerInterpretationSatEncoding implements SatEncoding {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncoding#encode(net.sf.tweety.arg.
	 * adf.reasoner.sat.SatEncodingContext)
	 */
	@Override
	public Collection<Disjunction> encode(SatEncodingContext context, Interpretation interpretation) {
		Collection<Disjunction> encoding = new LinkedList<Disjunction>();

		for (Argument a : (Iterable<Argument>) interpretation.satisfied()::iterator) {
			Disjunction clause = new Disjunction();
			clause.add(context.getTrueRepresentation(a));
			encoding.add(clause);
		}

		for (Argument a : (Iterable<Argument>) interpretation.unsatisfied()::iterator) {
			Disjunction clause = new Disjunction();
			clause.add(context.getFalseRepresentation(a));
			encoding.add(clause);
		}

		Disjunction undecided = new Disjunction();
		for (Argument a : (Iterable<Argument>) interpretation.undecided()::iterator) {
			undecided.add(context.getTrueRepresentation(a));
			undecided.add(context.getFalseRepresentation(a));
		}
		encoding.add(undecided);
		return encoding;
	}

}
