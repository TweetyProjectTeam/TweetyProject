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
package org.tweetyproject.lp.nlp.syntax;

import org.junit.Test;

import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.ForallQuantifiedFormula;
import org.tweetyproject.lp.nlp.error.NestedLogicProgramException;

public class NLPRuleTest {
	@Test(expected=NestedLogicProgramException.class)
	public void testQuantification() {
		NLPRule rule = new NLPRule();
		FolAtom base = new FolAtom(new Predicate("married", 2));
		base.addArgument(new Constant("bob"));
		base.addArgument(new Variable("Y"));
		rule.setConclusion(new ForallQuantifiedFormula(base, new Variable("Y")));
	}
}
