package net.sf.tweety.arg.aspic.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.arg.aspic.semantics.AspicArgumentationSystem;
import net.sf.tweety.commons.util.DigraphNode;
import net.sf.tweety.graphs.Node;

public class AspicArgument implements Node {
	
	private AspicFormula conc = null;;
	private Collection<AspicArgument> directsubs = new ArrayList<>();
	private AspicInferenceRule toprule = null;
	
	public AspicArgument(AspicInferenceRule toprule) {
		this.toprule = toprule;
		conc = toprule.getConclusion();	
	}
	
	public AspicArgument(DigraphNode<AspicInferenceRule> node, AspicArgumentationSystem as ) {
		for(DigraphNode<AspicInferenceRule> parentnode : node.getParents())
			directsubs.add(as.addArgument(new AspicArgument(parentnode, as)));
		
		toprule = node.getValue();
		conc = toprule.getConclusion();	
		
	}
	
	
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
	
	public AspicFormula getConc() {
		return conc;
	}
	
	public void setConc(AspicWord conc) {
		this.conc = conc;
	}
	
	public Collection<AspicArgument> getAllSubs() {
		Collection<AspicArgument> result = new HashSet<>();
		result.add(this);
		for(AspicArgument a : directsubs)
			result.addAll(a.getAllSubs());
		return result;
	}
	
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
	
	public Collection<AspicInferenceRule> getDefRules() {
		Collection<AspicInferenceRule> result = new HashSet<>();
		for(AspicArgument a : getDefSubs())
			result.add(a.toprule);
		return result;
	}
	
	public Collection<AspicArgument> getDirectSubs() {
		return directsubs;
	}

	public AspicInferenceRule getTopRule() {
		return toprule;
	}
	
	public void setTopRule(AspicInferenceRule toprule) {
		this.toprule = toprule;
	}

	@Override
	public String toString() {
		return "[" + toprule + (directsubs.isEmpty()  ? "":directsubs )+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((directsubs == null) ? 0 : directsubs.hashCode());
		result = prime * result + ((toprule == null) ? 0 : toprule.hashCode());
		return result;
	}

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
