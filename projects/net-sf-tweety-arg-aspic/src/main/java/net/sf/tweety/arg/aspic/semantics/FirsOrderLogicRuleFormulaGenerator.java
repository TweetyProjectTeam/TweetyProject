package net.sf.tweety.arg.aspic.semantics;

import net.sf.tweety.arg.aspic.syntax.InferenceRule;
import net.sf.tweety.logics.fol.syntax.FolFormula;

public class FirsOrderLogicRuleFormulaGenerator implements RuleFormulaGenerator<FolFormula> {

	public FirsOrderLogicRuleFormulaGenerator() {
	}
	
	//private static final Predicate RULE_PREDICATE = new Predicate("__rule", 1);

	@Override
	public FolFormula getRuleFormula(InferenceRule<FolFormula> r) {
		//	Constant rcons = new Constant( r.hashCode() );
			//return new FolAtom(RULE_PREDICATE, rcons);
		return null;
	}

}
