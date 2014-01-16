package net.sf.tweety;

/**
 * Classes implementing this interface are capable of restoring
 * consistency of inconsistent belief bases.
 * 
 * @author Matthias Thimm
 */
public interface BeliefBaseMachineShop {

	/**
	 * Repairs the given belief base, i.e. restores consistency.
	 * @param beliefBase a possibly inconsistent belief base.
	 * @return a consistent belief base that is as close as possible
	 * 	to the given belief base. NOTE: if the given belief base is
	 *  consistent this method is expected to return it unmodified.
	 */
	public BeliefBase repair(BeliefBase beliefBase);
}
