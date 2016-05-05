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
	
	

}
