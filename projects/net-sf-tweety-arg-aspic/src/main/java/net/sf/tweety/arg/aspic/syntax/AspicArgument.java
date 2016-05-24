package net.sf.tweety.arg.aspic.syntax;

import java.util.ArrayList;
import java.util.Collection;

public class AspicArgument {
	
	Collection<AspicInferenceRule> prems = new ArrayList<>();
	AspicWord conc = null;
	Collection<AspicArgument> subs = new ArrayList<>();
	Collection<AspicInferenceRule> defrules = new ArrayList<>();
	AspicInferenceRule toprule = null;
	
	public AspicArgument(Collection<AspicInferenceRule> derivation) {
		System.out.println(derivation);
		for(AspicInferenceRule rule:derivation) {
			if(toprule == null) {
				toprule = rule;
				conc = rule.getConclusion();
			}
			if(rule.isFact()) {
				prems.add(rule);
			} else if (rule.isDefeasible()) {
				defrules.add(rule);
			}
		}
	}
	
	public Collection<AspicInferenceRule> getPrems() {
		return prems;
	}
	public AspicWord getConc() {
		return conc;
	}
	public void setConc(AspicWord conc) {
		this.conc = conc;
	}
	public Collection<AspicArgument> getSubs() {
		return subs;
	}
	public Collection<AspicInferenceRule> getDefRules() {
		return defrules;
	}
	public AspicInferenceRule getTopRule() {
		return toprule;
	}
	public void setTopRule(AspicInferenceRule toprule) {
		this.toprule = toprule;
	}
	@Override
	public String toString() {
		return "Argument [prems=" + prems + ", conc=" + conc + ", subs=" + subs + ", defrules=" + defrules
				+ ", toprule=" + toprule + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result;
		if (subs.isEmpty())
			result += conc.hashCode();
		else for(AspicArgument a:subs)
			result += a.hashCode();
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
		
		if(subs.isEmpty()){
			return conc.equals(other.conc);
		} 
			for(AspicArgument a: subs)
				if(!other.subs.contains(a))
					return false;
			return true;
		
	}
	
	

}
