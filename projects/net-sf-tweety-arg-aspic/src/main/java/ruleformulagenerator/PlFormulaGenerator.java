package ruleformulagenerator;

import net.sf.tweety.arg.aspic.syntax.InferenceRule;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * @author Nils Geilen
 * 
 * Implements <code>RuleFormulaGenerator</code> for propositional logic.
 * If a rule has been given a name, it is employed as an identifier.
 */
public class PlFormulaGenerator extends RuleFormulaGenerator<PropositionalFormula> {

	/* (non-Javadoc)
	 * @see ruleformulagenerator.RuleFormulaGenerator#getRuleFormula(net.sf.tweety.arg.aspic.syntax.InferenceRule)
	 */
	@Override
	public PropositionalFormula getRuleFormula(InferenceRule<PropositionalFormula> r) {
		if(r.getName() == null)
			return new Proposition("rule_" + r.hashCode());
		else
			return new Proposition(r.getName());
	}

}
