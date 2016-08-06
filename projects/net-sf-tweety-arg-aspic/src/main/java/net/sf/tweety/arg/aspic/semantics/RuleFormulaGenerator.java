package net.sf.tweety.arg.aspic.semantics;

import net.sf.tweety.arg.aspic.syntax.InferenceRule;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

public interface RuleFormulaGenerator<T extends Invertable> {
	public T getRuleFormula(InferenceRule<T> r);
}
