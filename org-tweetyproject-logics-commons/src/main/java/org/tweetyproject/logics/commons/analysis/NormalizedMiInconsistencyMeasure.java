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
package org.tweetyproject.logics.commons.analysis;

import java.util.Collection;

import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.util.*;

/**
 * This class models the normalized MI inconsistency measure, see [PhD thesis, Thimm].
 * 
 * @author Matthias Thimm
 * @param <S> the type of formulas
 */
public class NormalizedMiInconsistencyMeasure<S extends Formula> extends MiInconsistencyMeasure<S> {

	/**
	 * Creates a new inconsistency measure with the given consistency tester
	 * @param enumerator some MUs enumerator.
	 */
	public NormalizedMiInconsistencyMeasure(MusEnumerator<S> enumerator) {
		super(enumerator);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.MiInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> beliefSet) {
		Double value = super.inconsistencyMeasure(beliefSet);
		if(value == 0) return value;
		@SuppressWarnings("deprecation")
		double normFactor = MathTools.binomial(beliefSet.size(), new Double(Math.ceil(new Double(beliefSet.size()) / 2)).intValue());
		return value / normFactor;
	}
}
