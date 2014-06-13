/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.util.*;

/**
 * This class models the normalized MI inconsistency measure, see [PhD thesis, Thimm].
 * 
 * @author Matthias Thimm
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
	 * @see net.sf.tweety.logics.commons.analysis.MiInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> beliefSet) {
		Double value = super.inconsistencyMeasure(beliefSet);
		if(value == 0) return value;
		double normFactor = MathTools.binomial(beliefSet.size(), new Double(Math.ceil(new Double(beliefSet.size()) / 2)).intValue());
		return value / normFactor;
	}
}
