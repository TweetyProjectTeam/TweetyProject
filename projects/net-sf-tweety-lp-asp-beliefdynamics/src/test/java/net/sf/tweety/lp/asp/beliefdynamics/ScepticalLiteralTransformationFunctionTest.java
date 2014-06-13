/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.lp.asp.beliefdynamics;


import static org.junit.Assert.assertFalse;
import net.sf.tweety.arg.lp.semantics.attack.Attack;
import net.sf.tweety.arg.lp.semantics.attack.AttackStrategy;
import net.sf.tweety.arg.lp.semantics.attack.Defeat;
import net.sf.tweety.arg.lp.semantics.attack.StrongAttack;
import net.sf.tweety.lp.asp.syntax.DLPAtom;
import net.sf.tweety.lp.asp.syntax.DLPNeg;
import net.sf.tweety.lp.asp.syntax.DLPNot;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.syntax.Rule;
import net.sf.tweety.lp.asp.beliefdynamics.selectiverevision.ScepticalLiteralTransformationFunction;

import org.junit.Test;

public class ScepticalLiteralTransformationFunctionTest {
	DLPAtom a1 = new DLPAtom("a1");
	DLPAtom a2 = new DLPAtom("a2");
	DLPAtom a3 = new DLPAtom("a3");
	DLPAtom a4 = new DLPAtom("a4");
	DLPAtom b = new DLPAtom("b");
	

	@Test
	public void testAttackFailsWeakMaximality() {
		Program p = new Program();
		p.add(new Rule(new DLPNeg(a1), new DLPNot(a1)));
		AttackStrategy attack = Attack.getInstance();
		AttackStrategy defense = Defeat.getInstance();
		ScepticalLiteralTransformationFunction trans;
		trans = new ScepticalLiteralTransformationFunction(p, attack, defense);
		
		Rule r1 = new Rule(a1);
		assertFalse(trans.transform(r1).contains(r1));
	}
	
	@Test
	public void testDefeatFailsWeakMaximality() {
		Program p = new Program();
		p.add(new Rule(a1, new DLPNot(a2)));
		p.add(new Rule(a2, new DLPNot(a1)));
		AttackStrategy attack = Defeat.getInstance();
		AttackStrategy defense = Defeat.getInstance();
		ScepticalLiteralTransformationFunction trans;
		trans = new ScepticalLiteralTransformationFunction(p, attack, defense);
		
		Rule r1 = new Rule(new DLPNeg(a2));
		assertFalse(trans.transform(r1).contains(r1));
	}
	
	@Test
	public void testStrongAttackFailsWeakMaximalityForEvenCycles() {
		Program p = new Program();
		p.add(new Rule(a1, new DLPNot(a2)));
		p.add(new Rule(a2, new DLPNot(a3)));
		p.add(new Rule(a3, new DLPNot(a4)));
		p.add(new Rule(a4, new DLPNot(a1)));
		
		AttackStrategy attack = StrongAttack.getInstance();
		AttackStrategy defense = StrongAttack.getInstance();
		ScepticalLiteralTransformationFunction trans;
		trans = new ScepticalLiteralTransformationFunction(p, attack, defense);
		
		Rule r1 = new Rule(new DLPNeg(a1));
		assertFalse(trans.transform(r1).contains(r1));
	}

}
