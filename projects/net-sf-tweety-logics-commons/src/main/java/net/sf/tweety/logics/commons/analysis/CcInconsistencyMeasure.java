package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.sf.tweety.commons.Formula;

/**
 * This class implements the inconsistency measure I_CC from 
 * [Said Jabbour and Yue Ma and Badran Raddaoui. Inconsistency Measurement Thanks to MUS Decomposition. AAMAS 2014.]
 * 
 * The measure is implemented using a naive search approach.
 *  
 * @author Matthias Thimm
 *
 * @param <S> The specific type of formulas
 */
public class CcInconsistencyMeasure<S extends Formula> extends BeliefSetInconsistencyMeasure<S> {

	
	/** The MUS enumerator used for the measure. */
	private MusEnumerator<S> enumerator;
	
	/**
	 * Creates a new measure that uses the given MUS enumerator
	 * @param enumerator
	 */
	public CcInconsistencyMeasure(MusEnumerator<S> enumerator){
		this.enumerator = enumerator;
	}
	
	/**
	 * Checks whether the given partition forms a conditional
	 * independent MUS partition according to Def. 20 of [Jabbour et al. AAMAS 2014].
	 * More specifically, given partition={R,K1,...,Kn} tests whether
	 * 1.) every Ki is inconsistent and 2.) whether MI(K1u...uKn)=MI(K1)u...uMI(Kn) 
	 * (without duplicates). Note that condition 3 of Def. 20 is satisfied by 
	 * construction in the main algorithm.
	 * @param partition A set of sets of formulas where K (the input knowledge base)
	 * is a disjoint union of these sets. The first set is interpreted as R 
	 * @return true iff K1...Kn is conditional independent.
	 */
	private boolean isConditionallyIndependent(Collection<Collection<S>> partition){
		boolean isFirst = true;
		Collection<Collection<S>> muses = new HashSet<Collection<S>>();
		Collection<Collection<S>> ind_mus;		
		Collection<S> joint = new HashSet<S>();
		for(Collection<S> c: partition){
			//skip first set
			if(isFirst){				
				isFirst = false;
				continue;
			}
			ind_mus = this.enumerator.minimalInconsistentSubsets(c);
			// check condition 1
			if(ind_mus.isEmpty())
				return false;
			muses.addAll(ind_mus);
			joint.addAll(c);
		}
		//check condition 2
		if(!enumerator.minimalInconsistentSubsets(joint).equals(muses))
			return false;		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> formulas) {
		// special case of consistent set
		if(this.enumerator.isConsistent(formulas))
			return 0d;
		Queue<List<Collection<S>>> q = new LinkedList<List<Collection<S>>>();
		List<Collection<S>> initial = new LinkedList<Collection<S>>();
		// add R
		initial.add(new HashSet<S>());
		// add Ki
		for(S f: formulas){
			Collection<S> k = new HashSet<S>();
			k.add(f);
			initial.add(k);
		}
		q.add(initial);
		while(!q.isEmpty()){
			List<Collection<S>> cand = q.poll();
			if(this.isConditionallyIndependent(cand)){
				return new Double(cand.size()-1);
			}
			List<Collection<S>> new_cand;
			Collection<S> lastK = cand.get(cand.size()-1);
			for(int j = 0; j< cand.size()-1; j++){
				new_cand = new LinkedList<Collection<S>>();
				for(int i = 0; i < cand.size()-1; i++)
					new_cand.add(cand.get(i));
				new_cand.get(j).addAll(lastK);
				q.add(new_cand);				
			}
		}
		// this should not happen
		throw new RuntimeException("Unrecognized error in computing the CC inconsistency measure.");
	}
}
