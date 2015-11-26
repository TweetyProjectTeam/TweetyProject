package net.sf.tweety.logics.pl.analysis;

import java.util.Collection;

import net.sf.tweety.logics.commons.analysis.AbstractMusEnumerator;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pl.sat.PlMusEnumerator;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * This class implements the "MUS-variable based inconsistency measure" proposed in 
 * [Xiao,Ma. Inconsistency Measurement based on Variables in Minimal Unsatisfiable Subsets. ECAI2012.]
 * This inconsistency measure is defined as the ratio of the number of propositions appearing in 
 * any minimal inconsistent subsets and the total number of propositions. 
 * 
 * @author Matthias Thimm
 *
 */
public class MusVarInconsistencyMeasure extends BeliefSetInconsistencyMeasure<PropositionalFormula>{

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<PropositionalFormula> formulas) {
		// check empty set of formulas
		if(formulas.isEmpty())
			return 0d;		
		AbstractMusEnumerator<PropositionalFormula> musEnum = PlMusEnumerator.getDefaultEnumerator();
		Collection<Collection<PropositionalFormula>> muses = musEnum.minimalInconsistentSubsets(formulas);
		PropositionalSignature allSig = new PropositionalSignature();
		PropositionalSignature musSig = new PropositionalSignature();
		for(PropositionalFormula f: formulas)
			allSig.add(f.getSignature());
		for(Collection<PropositionalFormula> mus: muses)
			for(PropositionalFormula f: mus)
				musSig.add(f.getSignature());
		return new Double(musSig.size())/new Double(allSig.size());
	}
}
