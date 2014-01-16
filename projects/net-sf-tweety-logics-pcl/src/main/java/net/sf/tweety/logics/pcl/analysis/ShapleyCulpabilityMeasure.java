package net.sf.tweety.logics.pcl.analysis;

import java.util.*;

import net.sf.tweety.InconsistencyMeasure;
import net.sf.tweety.logics.pcl.*;
import net.sf.tweety.logics.pcl.syntax.*;
import net.sf.tweety.util.*;

/**
 * This class implements the probabilistic Shapley culpability measure proposed in [Thimm:2009].
 * 
 * @author Matthias Thimm
 */
public class ShapleyCulpabilityMeasure implements CulpabilityMeasure {

	/**
	 * The inconsistency measure this Shapley culpability measure bases on.
	 */
	private InconsistencyMeasure<PclBeliefSet> inconsistencyMeasure;
	
	/** Stores previously computed culpability values. */
	private Map<Pair<PclBeliefSet,ProbabilisticConditional>,Double> archive;
	
	/**
	 * Creates a new Shapley culpability measure that bases on the given
	 * inconsistency measure.
	 * @param inconsistencyMeasure an inconsistency measure.
	 */
	public ShapleyCulpabilityMeasure(InconsistencyMeasure<PclBeliefSet> inconsistencyMeasure){
		this.inconsistencyMeasure = inconsistencyMeasure;
		this.archive = new HashMap<Pair<PclBeliefSet,ProbabilisticConditional>,Double>();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.probabilisticconditionallogic.analysis.CulpabilityMeasure#culpabilityMeasure(net.sf.tweety.logics.probabilisticconditionallogic.PclBeliefSet, net.sf.tweety.logics.probabilisticconditionallogic.syntax.ProbabilisticConditional)
	 */
	@Override
	public Double culpabilityMeasure(PclBeliefSet beliefSet, ProbabilisticConditional conditional) {
		if(this.archive.containsKey(new Pair<PclBeliefSet,ProbabilisticConditional>(beliefSet,conditional)))
			return this.archive.get(new Pair<PclBeliefSet,ProbabilisticConditional>(beliefSet,conditional)); 
		Set<Pair<PclBeliefSet,PclBeliefSet>> subbases = this.getSubsets(beliefSet, conditional);		
		Double result = new Double(0);
		for(Pair<PclBeliefSet,PclBeliefSet> pair : subbases){
			Double v1,v2;
			v1 = this.inconsistencyMeasure.inconsistencyMeasure(pair.getFirst());
			v2 = this.inconsistencyMeasure.inconsistencyMeasure(pair.getSecond());			
			Double temp =  v1 - v2;
			temp *= MathTools.faculty(pair.getSecond().size());
			temp *= MathTools.faculty(beliefSet.size()-pair.getFirst().size());
			temp /= MathTools.faculty(beliefSet.size());
			result += temp;
		}		
		this.archive.put(new Pair<PclBeliefSet,ProbabilisticConditional>(beliefSet,conditional), result); 
		return result;
	}
	
	/**
	 * Computes all pairs (k,k') of knowledge bases k,k'\subseteq kb, such that k = k' \cup {pc}.
	 * @param kb a knowledge base.
	 * @param pc a probabilistic constraint.
	 * @return a set of pairs of knowledge bases.
	 */
	private Set<Pair<PclBeliefSet,PclBeliefSet>> getSubsets(PclBeliefSet kb, ProbabilisticConditional pc){
		Set<Pair<PclBeliefSet,PclBeliefSet>> result = new HashSet<Pair<PclBeliefSet,PclBeliefSet>>();
		Set<Set<ProbabilisticConditional>> subsets = new SetTools<ProbabilisticConditional>().subsets(kb);
		for(Set<ProbabilisticConditional> subset: subsets)
			if(!subset.contains(pc)){
				Pair<PclBeliefSet,PclBeliefSet> pair = new Pair<PclBeliefSet,PclBeliefSet>();
				PclBeliefSet first = new PclBeliefSet(subset);
				first.add(pc);
				pair.setFirst(first);
				pair.setSecond(new PclBeliefSet(subset));
				result.add(pair);
			}
		return result;
	}

}
