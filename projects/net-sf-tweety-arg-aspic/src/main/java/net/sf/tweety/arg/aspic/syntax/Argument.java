package net.sf.tweety.arg.aspic.syntax;

import java.util.ArrayList;
import java.util.Collection;

public class Argument {
	
	Collection<Word> prems = new ArrayList<>();
	Word conc = null;
	Collection<Argument> subs = new ArrayList<>();
	Collection<InferenceRule> defrules = new ArrayList<>();
	InferenceRule toprule = null;
	
	public Collection<Word> getPrems() {
		return prems;
	}
	public Word getConc() {
		return conc;
	}
	public void setConc(Word conc) {
		this.conc = conc;
	}
	public Collection<Argument> getSubs() {
		return subs;
	}
	public Collection<InferenceRule> getDefRules() {
		return defrules;
	}
	public InferenceRule getTopRule() {
		return toprule;
	}
	public void setTopRule(InferenceRule toprule) {
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
		else for(Argument a:subs)
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
		Argument other = (Argument) obj;
		
		if(subs.isEmpty()){
			return conc.equals(other.conc);
		} 
			for(Argument a: subs)
				if(!other.subs.contains(a))
					return false;
			return true;
		
	}
	
	

}
