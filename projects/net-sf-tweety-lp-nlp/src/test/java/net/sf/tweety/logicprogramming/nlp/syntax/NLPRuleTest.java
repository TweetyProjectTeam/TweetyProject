/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
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
