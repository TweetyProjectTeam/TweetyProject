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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.writer;

import org.tweetyproject.commons.Writer;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This class implements a writer for propositional formulas and belief bases. It prints
 * formulas and belief bases in the TweetyProject input format.
 * 
 * @author Anna Gessler
 */
public class PlWriter extends Writer {
	/**
	 * Creates a new PlWriter for the given formula.
	 */
	public PlWriter(PlFormula plFormula) {
		super(plFormula);
	}
	
	/**
	 * Creates a new PlWriter for the given belief set.
	 */
	public PlWriter(PlBeliefSet plBeliefSet) {
		super(plBeliefSet);
	}

	/**
	 * Creates a new empty PlWriter.
	 */
	public PlWriter() {
	}
	
	@Override
	public String writeToString() {
		if (input == null)
			return "";
		else if (input instanceof PlBeliefSet) {
			String result = "";
			for (PlFormula f : ((PlBeliefSet) input))
				 result += f.toString() + "\n";
			return result;
		}
		else return input.toString();
	}
	
}
