package net.sf.tweety.arg.aspic.semantics;

import net.sf.tweety.arg.aspic.syntax.InferenceRule;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

public class PropositionalFormulaGenerator implements RuleFormulaGenerator<PropositionalFormula> {

	public PropositionalFormulaGenerator() {
	}

	@Override
	public PropositionalFormula getRuleFormula(InferenceRule<PropositionalFormula> r) {
		if(r.getName() == null)
			return new Proposition("rule_" + r.hashCode());
		else
			return new Proposition(r.getName());
	}

}
