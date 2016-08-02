package net.sf.tweety.arg.aspic.syntax;

import java.util.Collection;

import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

public class DefeasibleInferenceRule<T extends Invertable> extends InferenceRule<T> {
	
	public DefeasibleInferenceRule(){
	}

	public DefeasibleInferenceRule(T conclusion, Collection<T> premise) {
		super(conclusion, premise);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isDefeasible() {
		return true;
	}

}
