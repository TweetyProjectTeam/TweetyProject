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
package org.tweetyproject.logics.pcl.analysis;

import java.util.Collection;

import org.tweetyproject.logics.pcl.syntax.ProbabilisticConditional;
import org.tweetyproject.math.opt.rootFinder.OptimizationRootFinder;

/**
 * This class models the normalized distance minimization inconsistency measure, see [PhD thesis, Thimm].
 *
 * @author Matthias Thimm
 */
public class NormalizedDistanceMinimizationInconsistencyMeasure extends DistanceMinimizationInconsistencyMeasure {

	/**
	 * Constructor
	 * @param rootFinder the rootFinder
	 */
	public NormalizedDistanceMinimizationInconsistencyMeasure(OptimizationRootFinder rootFinder) {
		super(rootFinder);
	}

	/**
	 * Constructor
	 * @param rootFinder the rootFinder
	 * @param p the p value
	 */
	public NormalizedDistanceMinimizationInconsistencyMeasure(OptimizationRootFinder rootFinder, int p) {
		super(rootFinder,p);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pcl.analysis.DistanceMinimizationInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<ProbabilisticConditional> formulas)  {
		if(formulas.size() == 0) return 0d;
		return super.inconsistencyMeasure(formulas) / formulas.size();
	}

}
