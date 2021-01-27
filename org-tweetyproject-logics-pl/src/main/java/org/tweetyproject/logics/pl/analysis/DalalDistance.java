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
package org.tweetyproject.logics.pl.analysis;

import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlSignature;

/**
 * This class models the dalal distance measure between possible worlds,
 * see e.g. [Grant, Hunter. Distance-based Measures of Inconsistency, ECSQARU'13].
 * It returns the number of propositions two possible possible worlds differ.
 * 
 * @author Matthias Thimm
 *
 */
public class DalalDistance extends PossibleWorldDistance {

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.analysis.PossibleWorldDistance#distance(org.tweetyproject.logics.pl.semantics.PossibleWorld, org.tweetyproject.logics.pl.semantics.PossibleWorld)
	 */
	@Override
	public double distance(PossibleWorld a, PossibleWorld b) {
		int n = 0;
		PlSignature sig = new PlSignature();
		sig.addAll(a);
		sig.addAll(b);
		for(Proposition p: sig){
			if(a.contains(p) && !b.contains(p))
				n++;
			if(b.contains(p) && !a.contains(p))
				n++;
		}
		return n;
	}	
}
