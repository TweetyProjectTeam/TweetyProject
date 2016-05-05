package net.sf.tweety.arg.aspic.semantics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import net.sf.tweety.arg.aspic.syntax.Argument;
import net.sf.tweety.arg.aspic.syntax.InferenceRule;
import net.sf.tweety.arg.aspic.syntax.Word;

public class ArgumentationSystem {
	
	Collection<InferenceRule> rules = new ArrayList<>();
	Collection<Argument> args = new ArrayList<>();
	
	public void addRule(InferenceRule rule) {
		rules.add(rule);
	}
	
	public void expand(Set<Word> kb) {
		for(Word premise: kb) {
			Argument a = new Argument();
			a.getPrems().add(premise);
			a.setConc(premise);
			//a.getSubs().add(premise);
			args.add(a);
		}
		do {
			for (InferenceRule rule: rules) {
					
			}

		} while(true);
			
	}

	public Collection<Argument> getArguments() {
		return args;
	}

	@Override
	public String toString() {
		return "ArgumentationSystem [rules=" + rules + ",\n args=" + args + "]";
	}

	
}
