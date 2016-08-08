package net.sf.tweety.arg.aspic.ruleformulagenerator;



import java.util.Collections;

import net.sf.tweety.arg.aspic.syntax.InferenceRule;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;

/**
 * @author Nils Geilen
 * 
 * Implements <code>RuleFormulaGenerator</code> for first order logic.
 * If a rule has been given a name, it is employed as an identifier.
 */
public class FolFormulaGenerator extends RuleFormulaGenerator<FolFormula> {
	
	
	/**
	 * Constants needed for atom creation 
	 */
	final static Sort sort = new Sort("Rule");
	final static Predicate RULE_PREDICATE = new Predicate("__rule", Collections.singletonList(sort) );
	

	/* (non-Javadoc)
	 * @see ruleformulagenerator.RuleFormulaGenerator#getRuleFormula(net.sf.tweety.arg.aspic.syntax.InferenceRule)
	 */
	@Override
	public FolFormula getRuleFormula(InferenceRule<FolFormula> r)  {		
		Constant rcons = new Constant( r.getIdentifier() , sort);
		return new FOLAtom(RULE_PREDICATE, rcons);
	}


}
