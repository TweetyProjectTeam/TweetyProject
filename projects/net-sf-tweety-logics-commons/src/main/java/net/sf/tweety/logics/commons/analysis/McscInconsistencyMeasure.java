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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.util.SetTools;

/**
 * This class implements the approach of [Meriem Ammoura, Badran Raddaoui, Yakoub Salhi, Brahim Oukacha.
 * On Measuring Inconsistency Using Maximal Consistent Sets. ECSQARU'15].
 * <br/>
 * This implementation actually uses a different characterization of the measure proposed in the paper
 * above. Instead of using maximal consistent subsets the implementation uses minimal correction sets
 * (note that there is a 1:1 correspondence between the two).
 * 
 * @author Matthias Thimm
 */
public class McscInconsistencyMeasure<S extends Formula> extends BeliefSetInconsistencyMeasure<S> {

	/** The MUs enumerator. */
	private MusEnumerator<S> enumerator;
	
	/**
	 * Creates a new inconsistency measure.
	 * @param enumerator some MUs enumerator
	 */
	public McscInconsistencyMeasure(MusEnumerator<S> enumerator){
		this.enumerator = enumerator;
	}
	
	/** Recursively determines sets of minimal correction sets that have an empty intersection
	 * (= the complements of maximal consistent sets where the union equals the knowledge base;
	 * we call it MD-anticover).
	 * @param md a list of all minimal correction sets
	 * @param idx the current index in the list of minimal correction sets 
	 * @return the set of all MD anticover
	 */
	private Set<Set<Set<S>>> getMdAnticover(List<Set<S>> md, int idx, Set<Set<Set<S>>> candidates){
		// we use a brute force backtracking algorithm for now
		Set<Set<Set<S>>> result = new HashSet<Set<Set<S>>>();
		SetTools<S> st = new SetTools<S>();
		if(idx < md.size()-1){
			Set<Set<Set<S>>> new_candidates = new HashSet<Set<Set<S>>>();
			Set<Set<S>> m;
			for(Set<Set<S>> s: candidates){
				new_candidates.add(s);
				m = new HashSet<Set<S>>();
				m.addAll(s);
				m.add(md.get(idx));
				if(st.hasEmptyIntersection(m))
					result.add(m);
				else new_candidates.add(m);
			}			
			result.addAll(this.getMdAnticover(md, idx+1, new_candidates));
		}else{
			for(Set<Set<S>> s: candidates){
				s.add(md.get(idx));
				if(st.hasEmptyIntersection(s))
					result.add(s);
			}			
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> formulas) {
		List<Set<S>> md = new ArrayList<Set<S>>(this.enumerator.minimalCorrectionSubsets(formulas));
		if(md.isEmpty())
			return 0d;
		Set<Set<Set<S>>> cand = new HashSet<Set<Set<S>>>();
		SetTools<S> st = new SetTools<S>();
		cand.add(new HashSet<Set<S>>());
		Set<Set<Set<S>>> anticover = this.getMdAnticover(md, 0, cand); 
		// consider only anticover which are no super set of another anticover
		// then check the number of elements in its union
		boolean ismin;
		int min_union_size = Integer.MAX_VALUE;
		int size;
		for(Set<Set<S>> ac: anticover){
			ismin = true;
			for(Set<Set<S>> ac2: anticover){
				if(ac != ac2 && ac.containsAll(ac2)){
					ismin = false;
					break;
				}
			}
			if(ismin){
				size = st.getUnion(ac).size();
				if(size < min_union_size)
					min_union_size = size;
			}		
		}		
		return new Double(min_union_size);
	}

}
