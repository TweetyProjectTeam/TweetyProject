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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.ml.writer;

import org.tweetyproject.commons.Writer;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.ml.syntax.MlBeliefSet;


/**
 * The `MlWriter` class is responsible for writing  formulas and belief sets to a specific format.
 *
 *
 * <p><b>Author:</b> [Your Name]</p>
 */

public class MlWriter extends Writer {

	/**
     * Initializes a new `MlWriter` instance.
     *
     *
     * @param formula the `RelationalFormula` object representing formula
     *                that this writer will serialize and output.
     */

	public MlWriter(RelationalFormula formula) {
		super(formula);
	}



    /**
     * Initializes a new `MlWriter` instance for writing a belief set.
     *
     * @param beliefSet the `MlBeliefSet` object representing the set of formulas
     *                  that this writer will serialize and output.
     */
	public MlWriter(MlBeliefSet beliefSet) {
		super(beliefSet);
	}

	@Override
	public String writeToString() {
		if (input == null)
			return "";
		else if (input instanceof MlBeliefSet) {
			String result = "";
			for (RelationalFormula f : ((MlBeliefSet) input))
				result += f.toString() + "\n";
			return result;
		} else
			return input.toString();
	}

}
