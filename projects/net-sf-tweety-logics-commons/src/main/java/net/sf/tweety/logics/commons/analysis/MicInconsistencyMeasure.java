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
package net.sf.tweety.logics.commons.analysis;

import java.util.*;

import net.sf.tweety.commons.*;

/**
 * This class models the MI^C inconsistency measure.
 * 
 * @author Matthias Thimm
 */
public class MicInconsistencyMeasure<S extends Formula> extends BeliefSetInconsistencyMeasure<S> {

	/** The MUs enumerator. */
	private MusEnumerator<S> enumerator;
	
	/**
	 * Creates a new drastic inconsistency measure.
	 * @param enumerator some MUs enumerator
	 */
	public MicInconsistencyMeasure(MusEnumerator<S> enumerator){
		this.enumerator = enumerator;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> formulas) {
		double value = 0; 
		for(Collection<S> minInconSet: this.enumerator.minimalInconsistentSubsets(formulas)){			
			value += ( 1 / new Double(minInconSet.size()) );
		}
		return value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "mic";
	}
}
