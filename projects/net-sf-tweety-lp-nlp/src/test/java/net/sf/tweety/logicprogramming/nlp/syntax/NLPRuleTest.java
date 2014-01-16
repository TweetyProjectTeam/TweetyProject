package net.sf.tweety.logicprogramming.nlp.syntax;

import org.junit.Test;

import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.ForallQuantifiedFormula;
import net.sf.tweety.lp.nlp.error.NestedLogicProgramException;
import net.sf.tweety.lp.nlp.syntax.NLPRule;

public class NLPRuleTest {
	@Test(expected=NestedLogicProgramException.class)
	public void testQuantification() {
		NLPRule rule = new NLPRule();
		FOLAtom base = new FOLAtom(new Predicate("married", 2));
		base.addArgument(new Constant("bob"));
		base.addArgument(new Variable("Y"));
		rule.setConclusion(new ForallQuantifiedFormula(base, new Variable("Y")));
	}
}
