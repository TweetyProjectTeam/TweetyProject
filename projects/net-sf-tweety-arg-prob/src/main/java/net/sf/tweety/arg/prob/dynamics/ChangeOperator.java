package net.sf.tweety.arg.prob.dynamics;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.prob.PartialProbabilityAssignment;
import net.sf.tweety.arg.prob.semantics.ProbabilisticExtension;

/**
 * This interface provides common methods for change operators
 * for probabilistic argumentation.
 * @author Matthias Thimm
 */
public interface ChangeOperator {

	/**
	 * Given a partial probability assignment ppa and an argumentation theory, compute
	 * the closest probabilistic extension that is adequate for observing the theory in 
	 * the state ppa.
	 * @param ppa some partial probability assignment.
	 * @param theory some theory.
	 * @return the closest probabilistic extension that is adequate for observing the theory in 
	 * the state ppa.
	 */
	public ProbabilisticExtension change(PartialProbabilityAssignment ppa, DungTheory theory);
	
	/**
	 * Given a probabilistic extension and an argumentation theory, compute
	 * the closest probabilistic extension that is adequate for observing the theory in 
	 * the state p.
	 * @param p some probabilistic extension.
	 * @param theory some theory.
	 * @return the closest probabilistic extension that is adequate for observing the theory in 
	 * the state p.
	 */
	public ProbabilisticExtension change(ProbabilisticExtension p, DungTheory theory);
}
