package net.sf.tweety.arg.aspic.syntax;

import java.util.Collection;

public class Argument {
	
	Collection<String> prems;
	String conc;
	Collection<Argument> subs;
	Collection<InferenceRule> defrules;
	InferenceRule toprule;
	
	public Collection<String> getPremises() {
		return prems;
	}
	public String getConclusion() {
		return conc;
	}
	public Collection<Argument> getSubArguments() {
		return subs;
	}
	public Collection<InferenceRule> getDefRules() {
		return defrules;
	}
	public InferenceRule getTopRule() {
		return toprule;
	}

}
