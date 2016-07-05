package net.sf.tweety.arg.aspic.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.arg.aspic.semantics.AspicArgumentationSystem;
import net.sf.tweety.commons.util.DigraphNode;
import net.sf.tweety.graphs.Node;

/**
 * 
 * @author Nils Geilen
 *
 * An argument according to the ASPIC+ specification
 */

public class AspicArgument {
	
	/** The conclusion of the argument's top rule **/
	private AspicFormula conc = null;;
	/** The argument's direct children, whose conclusions fit its prerequisites **/
	private Collection<AspicArgument> directsubs = new ArrayList<>();
	/** The srgument's top rule **/
	private AspicInferenceRule toprule = null;
	
	
	/**
	 * Creates an empty Argument 
	 * @param toprule the argument's TopRule
	 */
	public AspicArgument(AspicInferenceRule toprule) {
		this.toprule = toprule;
		conc = toprule.getConclusion();	
	}
	
	/**
	 * Creates an new argument with and all of its subarguments and adds them to as
	 * @param node contains the TopRule
	 * @param as an AspicArgumentationSystem
	 */
	public AspicArgument(DigraphNode<AspicInferenceRule> node, AspicArgumentationSystem as ) {
		for(DigraphNode<AspicInferenceRule> parentnode : node.getParents())
			directsubs.add(as.addArgument(new AspicArgument(parentnode, as)));
		
		toprule = node.getValue();
		conc = toprule.getConclusion();	
		
	}
	
	/**
	 * @return whether this has a defeasible subrule
	 */
	public boolean isDefeasible() {
		return !getDefRules().isEmpty();
	}
	
/*	public Collection<AspicInferenceRule> getPrems() {
		Collection<AspicInferenceRule> result = new HashSet<>();
		if(toprule.isFact())
			result.add(toprule);
		for(AspicArgument arg : directsubs)
			result.addAll(arg.getPrems());
		return result;
	}*/
	
	/**
	 * @return all ordinary premises
	 */
	public Collection<AspicArgument> getOrdinaryPremises() {
		Collection<AspicArgument> result = new HashSet<>();
		if (toprule.isFact() && toprule.isDefeasible()) {
			result.add(this);
			return result;
		}
		for(AspicArgument a: directsubs)
			result.addAll(a.getOrdinaryPremises());
		return result;
	}
	
	/**
	 * Returns Conc according to the ASPIC+ specification
	 * @return the top rule's conclusion
	 */
	public AspicFormula getConc() {
		return conc;
	}
	
	/**
	 * Change the conclusion
	 * @param conc the new conclusion
	 */
	public void setConc(AspicWord conc) {
		this.conc = conc;
	}
	
	/**
	 * returns the Subs according to the ASPIC+ specification
	 * @return all subarguments including this
	 */
	public Collection<AspicArgument> getAllSubs() {
		Collection<AspicArgument> result = new HashSet<>();
		result.add(this);
		for(AspicArgument a : directsubs)
			result.addAll(a.getAllSubs());
		return result;
	}
	
	/**
	 * @return all arguments in Subs with defeasible top rules
	 */
	public Collection<AspicArgument> getDefSubs() {
		Collection<AspicArgument> result = new HashSet<>();
		if(toprule.isFact())
			return result;
		if(toprule.isDefeasible())
			result.add(this);
		for(AspicArgument arg : directsubs)
			result.addAll(arg.getDefSubs());
		return result;
	}
	
	/**
	 * returns the DefRules according to ASPIC+ specification
	 * @return this argument's defeasible rules
	 */
	public Collection<AspicInferenceRule> getDefRules() {
		Collection<AspicInferenceRule> result = new HashSet<>();
		for(AspicArgument a : getDefSubs())
			result.add(a.toprule);
		return result;
	}
	
	/**
	 * The argument's direct children, whose conclusions fit its prerequisites
	 * @return  the direct subrules
	 */
	public Collection<AspicArgument> getDirectSubs() {
		return directsubs;
	}

	/**
	 * Retruns the TopRule according to ASPIC+ specification
	 * @return the top rule
	 */
	public AspicInferenceRule getTopRule() {
		return toprule;
	}
	
	/**
	 * Changes the TopRule
	 * @param toprule the new TopRule
	 */
	public void setTopRule(AspicInferenceRule toprule) {
		this.toprule = toprule;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + toprule + (directsubs.isEmpty()  ? "":directsubs )+ "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((directsubs == null) ? 0 : directsubs.hashCode());
		result = prime * result + ((toprule == null) ? 0 : toprule.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AspicArgument other = (AspicArgument) obj;
		if (directsubs == null) {
			if (other.directsubs != null)
				return false;
		} else if (!directsubs.equals(other.directsubs))
			return false;
		if (toprule == null) {
			if (other.toprule != null)
				return false;
		} else if (!toprule.equals(other.toprule))
			return false;
		return true;
	}
	
	
	
	

}
