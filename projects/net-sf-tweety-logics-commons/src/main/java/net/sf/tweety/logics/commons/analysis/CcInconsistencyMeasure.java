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
	 * @param mises all MIes of the knowledge base 
	 * @return true iff K1...Kn is conditional independent.
	 */
	private boolean isConditionallyIndependent(Collection<Collection<S>> partition, Collection<Collection<S>> mises){
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
			ind_mus = new HashSet<Collection<S>>();
			// check condition 1			
			for(Collection<S> mi: mises){
				if(c.containsAll(mi)){
					ind_mus.add(mi);
				}				
			}				
			if(ind_mus.isEmpty())
				return false;
			muses.addAll(ind_mus);
			joint.addAll(c);
		}
		//check condition 2
		Collection<Collection<S>> joint_mus = new HashSet<Collection<S>>();
		for(Collection<S> mi: mises){
			if(joint.containsAll(mi)){
				joint_mus.add(mi);
			}				
		}		
		if(!joint_mus.equals(muses))
			return false;		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> formulas) {
		Collection<Collection<S>> mises = this.enumerator.minimalInconsistentSubsets(formulas);
		// special case of consistent set		
		if(mises.isEmpty())
			return 0d;
		// as I_CC is additive, compute the measure for each MI component and then add the values
		Collection<Collection<S>> miComponents = this.enumerator.getMiComponents(formulas);
		if(miComponents.size() > 1){
			double result = 0;
			for(Collection<S> sub: miComponents)
				result += this.inconsistencyMeasure(sub);
			return result;
		}		
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
			if(this.isConditionallyIndependent(cand,mises)){
				return new Double(cand.size()-1);
			}
			List<Collection<S>> new_cand;
			for(int i = 0; i < cand.size(); i++)
				for(int j = i+1; j < cand.size(); j++){
					new_cand = new LinkedList<Collection<S>>();
					for(int k = 0; k < cand.size(); k++)
						new_cand.add(new HashSet<S>(cand.get(k)));
					new_cand.get(i).addAll(new_cand.get(j));
					new_cand.remove(j);
					if(!q.contains(new_cand))
						q.add(new_cand);
				}		
		}
		// this should not happen
		throw new RuntimeException("Unrecognized error in computing the CC inconsistency measure.");
	}
}
