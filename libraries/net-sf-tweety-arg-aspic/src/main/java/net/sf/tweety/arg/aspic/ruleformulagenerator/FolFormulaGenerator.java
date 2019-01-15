/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.aspic.ruleformulagenerator;



import java.util.Collections;

import net.sf.tweety.arg.aspic.syntax.DefeasibleInferenceRule;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.fol.syntax.FolAtom;
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
	public FolFormula getRuleFormula(DefeasibleInferenceRule<FolFormula> r)  {		
		Constant rcons = new Constant( r.getIdentifier() , sort);
		return new FolAtom(RULE_PREDICATE, rcons);
	}


}
