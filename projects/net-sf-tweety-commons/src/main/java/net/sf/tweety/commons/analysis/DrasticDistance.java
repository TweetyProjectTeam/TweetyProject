/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.commons.analysis;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Interpretation;


/**
 * This class models the drastic distance measure between interpretations,
 * see [Grant, Hunter. Distance-based Measures of Inconsistency, ECSQARU'13].
 * It returns 0 if the interpretations are equivalent and 1 otherwise.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The actual type of interpretation
 */
public class DrasticDistance<T extends Interpretation,S extends Formula> implements InterpretationDistance<T,S>{

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.analysis.InterpretationDistance#distance(net.sf.tweety.commons.Interpretation, net.sf.tweety.commons.Interpretation)
	 */
	@Override
	public double distance(T a, T b) {
		return a.equals(b) ? 0 : 1;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.analysis.InterpretationDistance#distance(net.sf.tweety.commons.Formula, net.sf.tweety.commons.Interpretation)
	 */
	@Override
	public double distance(S f, T b) {		
		return b.satisfies(f) ? 0 : 1;
	}

}
