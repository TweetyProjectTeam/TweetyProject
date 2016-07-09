package net.sf.tweety.arg.aspic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

import net.sf.tweety.arg.aspic.semantics.AspicAttack;
import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.aspic.syntax.AspicFormula;
import net.sf.tweety.arg.aspic.syntax.AspicInferenceRule;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.DigraphNode;
import net.sf.tweety.commons.util.rules.DerivationGraph;


public class AspicArgumentationSystem implements BeliefBase {
	
	private Collection<AspicInferenceRule> rules = new ArrayList<>();
	private Collection<AspicArgument> args = new HashSet<>();
	private Comparator<AspicArgument> order ;
	//Graph<AspicArgument> arg_graph = new DefaultGraph<>();
	
	public void addRule(AspicInferenceRule rule) {
		rules.add(rule);
	}
	
	public DungTheory asDungTheory(){
		DungTheory dung_theory = new DungTheory();
		dung_theory.addAll(args);
		dung_theory.addAllAttacks(AspicAttack.determineAttackRelations(args, order));
		return dung_theory;
	}
	
	public AspicArgument addArgument(AspicArgument arg) {
		for(AspicArgument a : args)
			if(a.equals(arg))
				return a;
		args.add(arg);
		return arg;
	}
	
	public void expand() {
		DerivationGraph<AspicFormula, AspicInferenceRule> rule_graph = new DerivationGraph<>();
		rule_graph.allDerivations(rules);
		//rule_graph.printTrees(System.out);
		
		for (DigraphNode<AspicInferenceRule> node : rule_graph) {
			addArgument(new AspicArgument(node, this));
		}
	}
	
	public void setOrder(Comparator<AspicArgument> order) {
		this.order = order;
	}

	public Comparator<AspicArgument> getOrder() {
		return order;
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

	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
