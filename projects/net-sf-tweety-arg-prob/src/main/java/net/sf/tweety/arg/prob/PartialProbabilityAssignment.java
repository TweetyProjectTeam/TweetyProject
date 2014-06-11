package net.sf.tweety.arg.prob;

import java.util.HashMap;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungSignature;
import net.sf.tweety.arg.prob.semantics.ProbabilisticExtension;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.math.probability.Probability;

/**
 * A partial probability assignment for abstract argumentation theories.
 * @author Matthias Thimm
 */
public class PartialProbabilityAssignment extends HashMap<Argument,Probability> implements BeliefBase{

	/** For serialization.*/
	private static final long serialVersionUID = 7051185602937753358L;

	/**
	 * Checks whether the given probabilistic extension is compliant with this
	 * partial probability assignment, i.e. whether the probabilities of the 
	 * arguments coincide.
	 * @param pext some probabilistic extension.
	 * @return "true" iff the given probabilistic extension is compliant.
	 */
	public boolean isCompliant(ProbabilisticExtension pext){
		for(Argument a: this.keySet())
			if(pext.get(a).getValue() < this.get(a).getValue() - Probability.PRECISION ||
					pext.get(a).getValue() > this.get(a).getValue() + Probability.PRECISION)
				return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.BeliefBase#getSignature()
	 */
	@Override
	public Signature getSignature() {
		DungSignature sig = new DungSignature();
		for(Argument a: this.keySet())
			sig.add(a);
		return sig;
	}
}
