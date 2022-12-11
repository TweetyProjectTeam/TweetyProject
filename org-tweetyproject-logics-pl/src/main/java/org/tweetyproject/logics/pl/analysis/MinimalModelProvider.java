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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.analysis;

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.commons.InterpretationSet;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * comptes all minimal models given a set of models
 * @author Sebastian Franke
 *
 */
public class MinimalModelProvider {
	/**
	 * 
	 * @param f all models
	 * @return the minimal models
	 */
	public Set<InterpretationSet<Proposition,PlBeliefSet,PlFormula>> getMinModels(Set<InterpretationSet<Proposition,PlBeliefSet,PlFormula>> f){
		Set<InterpretationSet<Proposition,PlBeliefSet,PlFormula>> minModels = new HashSet<InterpretationSet<Proposition,PlBeliefSet,PlFormula>>();
		for(InterpretationSet<Proposition,PlBeliefSet,PlFormula> i : f) {
			boolean isMinimal = true;
			for(InterpretationSet<Proposition,PlBeliefSet,PlFormula> j : f) {
				if(i.containsAll(j) && !i.equals(j) && j.size() > 0)
					isMinimal = false;
			}
			if(isMinimal == true) {
				minModels.add(i);
			}
		}
		return minModels;
	}


}
