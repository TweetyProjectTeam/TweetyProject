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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.pl.postulates;

import java.util.Collection;

import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * The "normalization" postulate for inconsistency measures: The inconsistency
 * value is always in the unit interval [0,1], making it possible
 * to compare inconsistency values for knowledge bases of different sizes.
 * 
 * @author Anna Gessler
 */
public class ImNormalization extends ImPostulate {
	
	/**
	 * Protected constructor so one uses only the single instance ImPostulate.NORMALIZATION
	 */
	protected ImNormalization() {		
	}
	

	@Override
	public String getName() {
		return "Normalization";
	}

	@Override
	public boolean isApplicable(Collection<PlFormula> kb) {
		return true;
	}

	@Override
	public boolean isSatisfied(Collection<PlFormula> kb,
			BeliefSetInconsistencyMeasure<PlFormula> ev) {
		if(!this.isApplicable(kb))
			return true;
		double inconsistency = ev.inconsistencyMeasure(kb);
		return (inconsistency >= 0 && inconsistency <= 1);
	}

}
