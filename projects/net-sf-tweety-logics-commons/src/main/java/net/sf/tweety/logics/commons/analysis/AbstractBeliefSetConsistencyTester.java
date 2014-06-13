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

import java.util.*;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.Formula;

/**
 * Classes extending this abstract class are capable of testing
 * whether a given belief set is consistent. 
 * 
 * @author Matthias Thimm
 */
public abstract class AbstractBeliefSetConsistencyTester<S extends Formula> implements BeliefSetConsistencyTester<S> {
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.ConsistencyTester#isConsistent(net.sf.tweety.BeliefBase)
	 */
	@Override
	public boolean isConsistent(BeliefSet<S> beliefSet){
		return this.isConsistent((Collection<S>) beliefSet);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent(net.sf.tweety.Formula)
	 */
	@Override
	public boolean isConsistent(S formula){
		Collection<S> c = new HashSet<S>();
		c.add(formula);
		return this.isConsistent(c);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent(java.util.Collection)
	 */
	public abstract boolean isConsistent(Collection<S> formulas);
}