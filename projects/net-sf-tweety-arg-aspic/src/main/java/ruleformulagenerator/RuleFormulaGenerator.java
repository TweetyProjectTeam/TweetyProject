package ruleformulagenerator;

import net.sf.tweety.arg.aspic.AspicArgumentationTheory;
import net.sf.tweety.arg.aspic.syntax.InferenceRule;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

/**
 * @author Nils Geilen
 * 
 * This class transforms a defeasible ASPIC inference rule into a corresponding formula,
 * i.e. a word in the language of the conclusion and the premises of that rule,
 * which can be used in the head of an inference rule.
 * 
 * @param <T>	is the type of the returned formula
 */
public abstract class RuleFormulaGenerator<T extends Invertable> {
	
	/**
	 * Transforms a defeasible ASPIC inference rule into a corresponding formula of type <code>T</code>
	 * @param r	is the inferende rule to be transformed
	 * @return	a formula of type <code>T</code>
	 */
	public abstract T getRuleFormula(InferenceRule<T> r);
	
	/**
	 * Transforms a formula of type <code>T</code> into a corresponding defeasible ASPIC inference rule 
	 * form the knowledge base <code>kb</code>
	 * @param formula	is a formula of type <code>T</code>
	 * @param kb	is the knowledge base the is searched for the rule
	 * @return	the corresponding inference rule
	 */
	public InferenceRule<T> getInferenceRule(T formula, AspicArgumentationTheory<T> kb) {
		for (InferenceRule<T> rule : kb.getRules())
			if (getRuleFormula(rule).equals(formula))
				return rule;
		return null;
	}
}
