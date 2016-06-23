package net.sf.tweety.arg.aspic.semantics;

import java.util.ArrayList;
import java.util.Collection;

import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.aspic.syntax.AspicFormula;
import net.sf.tweety.arg.aspic.syntax.AspicInferenceRule;
import net.sf.tweety.commons.util.rules.DerivationGraph;
import net.sf.tweety.graphs.DefaultGraph;
import net.sf.tweety.graphs.Graph;


public class AspicArgumentationSystem {
	
	Collection<AspicInferenceRule> rules = new ArrayList<>();
	Collection<AspicArgument> args = new ArrayList<>();
	//Graph<AspicArgument> arg_graph = new DefaultGraph<>();
	
	public void addRule(AspicInferenceRule rule) {
		rules.add(rule);
	}
	
	public void expand() {
		DerivationGraph<AspicFormula, AspicInferenceRule> rule_graph = new DerivationGraph<>();
		rule_graph.allDerivations(rules);
		rule_graph.printTrees(System.out);
		
		
	}

	
	
	

	public Collection<AspicInferenceRule> getRules() {
		return rules;
	}

	@Override
	public String toString() {
		return "ArgumentationSystem [rules=" + rules + ",\n args=" + args + "]";
	}

	
}
