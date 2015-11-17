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
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.util.SetTools;

/**
 * Abstract implementation for MUes enumerators.
 * 
 * @author Matthias Thimm
 *
 * @param <S> the type of formulas
 */
public abstract class AbstractMusEnumerator<S extends Formula> implements MusEnumerator<S> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MusEnumerator#minimalInconsistentSubsets(java.util.Collection)
	 */
	@Override
	public abstract Collection<Collection<S>> minimalInconsistentSubsets(Collection<S> formulas);

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MusEnumerator#maximalConsistentSubsets(java.util.Collection)
	 */
	@Override
	public Collection<Collection<S>> maximalConsistentSubsets(Collection<S> formulas){
		// we use the duality of minimal inconsistent subsets, minimal correction sets, and maximal consistent
		// subsets to compute the set of maximal consistent subsets
		Collection<Collection<S>> mis = this.minimalInconsistentSubsets(formulas);
		// makes sets out of this
		Set<Set<S>> mi_sets = new HashSet<Set<S>>();
		for(Collection<S> m: mis)
			mi_sets.add(new HashSet<S>(m));
		SetTools<S> settools = new SetTools<S>();
		// get the minimal correction sets, i.e. irreducible hitting sets of minimal inconsistent subsets
		Set<Set<S>> md_sets = settools.irreducibleHittingSets(mi_sets);
		// every maximal consistent subset is the complement of a minimal correction set
		Set<Collection<S>> result = new HashSet<Collection<S>>();;
		Set<S> tmp;
		for(Set<S> ms: md_sets){
			tmp = new HashSet<S>(formulas);
			tmp.removeAll(ms);
			result.add(tmp);
		}			
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MusEnumerator#isConsistent(net.sf.tweety.BeliefSet)
	 */
	@Override
	public boolean isConsistent(BeliefSet<S> beliefSet){
		return this.isConsistent((Collection<S>) beliefSet);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MusEnumerator#isConsistent(net.sf.tweety.Formula)
	 */
	@Override
	public boolean isConsistent(S formula){
		Collection<S> c = new HashSet<S>();
		c.add(formula);
		return this.isConsistent(c);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MusEnumerator#isConsistent(java.util.Collection)
	 */
	@Override
	public boolean isConsistent(Collection<S> formulas){
		return this.minimalInconsistentSubsets(formulas).isEmpty();
	}
}
