package net.sf.tweety.arg.aba.syntax;

import java.util.Collection;
import java.util.LinkedList;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.util.rules.Rule;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

public class Deduction <T extends Invertable> extends Argument {
	
	ABARule<T> rule;
	Collection<Deduction<T>> subs;
		

	public Deduction(String name) {
		super(name);
	}
	
	public T getConclusion() {
		return rule.getConclusion();
	}
	
	public Collection<T> getAssumptions() {
		Collection<T> result = new LinkedList<>();
		if (rule.isAssumption())
			result.add(rule.getConclusion());
		else for (Deduction<T> sub : subs)
			result.addAll(sub.getAssumptions());
		return result;
	}
	
	public Collection<ABARule<T>> getRules() {
		Collection<ABARule<T>> result = new LinkedList<>();
		if (! rule.isAssumption())
			result.add(rule);
		for (Deduction<T> sub : subs)
			result.addAll(sub.getRules());
		return result;
	}

	

}
