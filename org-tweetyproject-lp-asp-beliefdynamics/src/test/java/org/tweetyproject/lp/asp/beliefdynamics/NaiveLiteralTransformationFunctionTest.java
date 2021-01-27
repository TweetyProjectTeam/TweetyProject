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
package org.tweetyproject.lp.asp.beliefdynamics;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.lp.semantics.attack.AttackStrategy;
import org.tweetyproject.arg.lp.semantics.attack.StrongAttack;
import org.tweetyproject.lp.asp.syntax.Program;
import org.tweetyproject.lp.asp.syntax.ASPAtom;
import org.tweetyproject.lp.asp.syntax.StrictNegation;
import org.tweetyproject.lp.asp.syntax.ASPRule;
import org.tweetyproject.lp.asp.syntax.DefaultNegation;
import org.tweetyproject.lp.asp.beliefdynamics.selectiverevision.ScepticalLiteralTransformationFunction;

import org.junit.Test;

public class NaiveLiteralTransformationFunctionTest {
	ASPAtom a1 = new ASPAtom("a1");
	ASPAtom a2 = new ASPAtom("a2");
	ASPAtom a3 = new ASPAtom("a3");
	ASPAtom a4 = new ASPAtom("a4");
	ASPAtom b = new ASPAtom("b");

	@Test
	public void testFailsWeakMaximality() {
		Program p = new Program();
		p.add(new ASPRule(new StrictNegation(a1), new DefaultNegation(a2)));
		p.add(new ASPRule(new StrictNegation(a2), new DefaultNegation(a1)));
		
		AttackStrategy attack = StrongAttack.getInstance();
		AttackStrategy defense = StrongAttack.getInstance();
		ScepticalLiteralTransformationFunction trans;
		trans = new ScepticalLiteralTransformationFunction(p, attack, defense);
		
		ASPRule r1 = new ASPRule(a1);
		ASPRule r2 = new ASPRule(a2);
		Set<ASPRule> newKB = new HashSet<ASPRule>();
		newKB.add(r1); newKB.add(r2);
		Collection<ASPRule> result = trans.transform(newKB);
		assertFalse(result.contains(r1));
		assertFalse(result.contains(r2));
	}

}
