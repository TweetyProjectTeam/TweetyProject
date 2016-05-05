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
		Collection<Argument> new_args = new ArrayList<>(),
				last_args = new ArrayList<>();
		for(Word premise: kb) {
			Argument a = new Argument();
			a.getPrems().add(premise);
			a.setConc(premise);
			//a.getSubs().add(premise);
			last_args.add(a);
		}
		do {
			System.out.println(last_args);
			for(Argument arg: last_args) {
				for (InferenceRule rule: rules) {
					
				}
			}
			args.addAll(last_args);
			last_args= new_args;
		} while(!new_args.isEmpty());
			
	}

	public Collection<Argument> getArguments() {
		return args;
	}

	@Override
	public String toString() {
		return "ArgumentationSystem [rules=" + rules + ",\n args=" + args + "]";
	}

	
}
