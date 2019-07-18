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
package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.util.*;

/**
 * This class models the normalized MI^C inconsistency measure, see [PhD thesis, Thimm].
 * 
 * @author Matthias Thimm
 * @param <S> the type of formulas
 */
public class NormalizedMicInconsistencyMeasure<S extends Formula> extends MicInconsistencyMeasure<S> {

	/**
	 * Creates a new inconsistency measure with the given consistency tester
	 * @param enumerator some MUs enumerator
	 */
	public NormalizedMicInconsistencyMeasure(MusEnumerator<S> enumerator) {
		super(enumerator);
	}


	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MicInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> beliefSet) {
		double value = super.inconsistencyMeasure(beliefSet);
		if(value == 0) return value;
		@SuppressWarnings("deprecation")
		double normFactor = ((double)MathTools.binomial(beliefSet.size(), new Double(Math.ceil(((double)beliefSet.size()) / 2)).intValue())) / 2;
		return value/normFactor;
	}
}
