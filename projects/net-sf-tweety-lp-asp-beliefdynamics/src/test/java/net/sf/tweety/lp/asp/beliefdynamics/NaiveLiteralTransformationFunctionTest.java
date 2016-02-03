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
package net.sf.tweety.lp.asp.beliefdynamics;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.arg.lp.semantics.attack.AttackStrategy;
import net.sf.tweety.arg.lp.semantics.attack.StrongAttack;
import net.sf.tweety.lp.asp.syntax.DLPAtom;
import net.sf.tweety.lp.asp.syntax.DLPNeg;
import net.sf.tweety.lp.asp.syntax.DLPNot;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.syntax.Rule;
import net.sf.tweety.lp.asp.beliefdynamics.selectiverevision.ScepticalLiteralTransformationFunction;

import org.junit.Test;

public class NaiveLiteralTransformationFunctionTest {
	DLPAtom a1 = new DLPAtom("a1");
	DLPAtom a2 = new DLPAtom("a2");
	DLPAtom a3 = new DLPAtom("a3");
	DLPAtom a4 = new DLPAtom("a4");
	DLPAtom b = new DLPAtom("b");

	@Test
	public void testFailsWeakMaximality() {
		Program p = new Program();
		p.add(new Rule(new DLPNeg(a1), new DLPNot(a2)));
		p.add(new Rule(new DLPNeg(a2), new DLPNot(a1)));
		
		AttackStrategy attack = StrongAttack.getInstance();
		AttackStrategy defense = StrongAttack.getInstance();
		ScepticalLiteralTransformationFunction trans;
		trans = new ScepticalLiteralTransformationFunction(p, attack, defense);
		
		Rule r1 = new Rule(a1);
		Rule r2 = new Rule(a2);
		Set<Rule> newKB = new HashSet<Rule>();
		newKB.add(r1); newKB.add(r2);
		Collection<Rule> result = trans.transform(newKB);
		assertFalse(result.contains(r1));
		assertFalse(result.contains(r2));
	}

}
