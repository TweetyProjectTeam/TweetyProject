package net.sf.tweety.arg.aspic.syntax;

import java.util.Collection;

import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

public class StrictInferenceRule<T extends Invertable> extends InferenceRule<T> {
	
	public StrictInferenceRule(){	
	}
	
	public StrictInferenceRule(T conclusion, Collection<T> premise) {
		super(conclusion, premise);
	}

	@Override
	public boolean isDefeasible() {
		return false;
	}

}
