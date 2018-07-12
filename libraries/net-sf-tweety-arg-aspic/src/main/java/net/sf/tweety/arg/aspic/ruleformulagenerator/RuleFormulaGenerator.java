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

import net.sf.tweety.arg.aspic.AspicArgumentationTheory;
import net.sf.tweety.arg.aspic.syntax.DefeasibleInferenceRule;
import net.sf.tweety.arg.aspic.syntax.InferenceRule;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

/**
 * @author Nils Geilen
 * 
 *         This class transforms a defeasible ASPIC inference rule into a
 *         corresponding formula, i.e. a word in the language of the conclusion
 *         and the premises of that rule, which can be used in the head of an
 *         inference rule.
 * 
 * @param <T>
 *            is the type of the returned formula
 */
public abstract class RuleFormulaGenerator<T extends Invertable> {

	/**
	 * Transforms a defeasible ASPIC inference rule into a corresponding formula
	 * of type <code>T</code>
	 * 
	 * @param r
	 *            is the inferende rule to be transformed
	 * @return a formula of type <code>T</code>
	 */
	public abstract T getRuleFormula(DefeasibleInferenceRule<T> r);

	/**
	 * Transforms a formula of type <code>T</code> into a corresponding
	 * defeasible ASPIC inference rule form the knowledge base <code>kb</code>
	 * 
	 * @param formula
	 *            is a formula of type <code>T</code>
	 * @param kb
	 *            is the knowledge base the is searched for the rule
	 * @return the corresponding inference rule
	 */
	public DefeasibleInferenceRule<T> getInferenceRule(T formula, AspicArgumentationTheory<T> kb) {
		for (InferenceRule<T> rule : kb)
			if (rule.isDefeasible()) {
				DefeasibleInferenceRule<T> defrule = (DefeasibleInferenceRule<T>) rule;
				if (getRuleFormula(defrule).equals(formula))

					return defrule;
			}
		return null;
	}
}
