package net.sf.tweety.arg.aspic.semantics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.aspic.syntax.AspicInferenceRule;
import net.sf.tweety.commons.util.rules.Derivation;

public class AspicArgumentationSystem {
	
	Collection<AspicInferenceRule> rules = new ArrayList<>();
	Collection<AspicArgument> args = new ArrayList<>();
	
	public void addRule(AspicInferenceRule rule) {
		rules.add(rule);
	}
	
	public void expand() {
		Set<Derivation<AspicInferenceRule>> ds = Derivation.allDerivations(rules);
		
		for(Derivation<AspicInferenceRule> d:ds)
			args.add(new AspicArgument(d));
			
	}

	public Collection<AspicArgument> getArguments() {
		return args;
	}
	
	

	public Collection<AspicInferenceRule> getRules() {
		return rules;
	}

	@Override
	public String toString() {
		return "ArgumentationSystem [rules=" + rules + ",\n args=" + args + "]";
	}

	
}
