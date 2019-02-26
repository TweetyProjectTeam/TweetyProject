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
package net.sf.tweety.logics.pl.writer;

import net.sf.tweety.commons.Writer;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * This class implements a writer for propositional formulas and belief bases.
 * 
 * @author Anna Gessler
 */
public class PlWriter extends Writer {

	public PlWriter(PlFormula plFormula) {
		super(plFormula);
	}
	
	public PlWriter(PlBeliefSet plBeliefSet) {
		super(plBeliefSet);
	}

	@Override
	public String writeToString() {
		return this.getObject().toString();
	}

}
