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

import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.logics.pl.syntax.Disjunction;

/**
 * 
 * TODO: reconsider how sat encodings are organized, factories? singleton?
 * static? currently it is stateless and therefore we can reuse and share a
 * single instance
 * 
 * @author Mathias Hofer
 *
 */
public interface SatEncoding {

	default Collection<Disjunction> encode(SatEncodingContext context) {
		return encode(context, null);
	}

	/**
	 * Constructs a SAT encoding based on the given context and an optional
	 * interpretation.
	 * 
	 * @param context
	 * @param interpretation
	 * @return a set of disjunctions
	 */
	Collection<Disjunction> encode(SatEncodingContext context, Interpretation interpretation);

}
