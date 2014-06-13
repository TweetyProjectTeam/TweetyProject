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
package net.sf.tweety.argumentation.parameterisedhierarchy;

import static org.junit.Assert.*;

import net.sf.tweety.arg.lp.ArgumentationKnowledgeBase;
import net.sf.tweety.arg.lp.semantics.AttackRelation;
import net.sf.tweety.arg.lp.semantics.attack.Attack;
import net.sf.tweety.arg.lp.semantics.attack.AttackStrategy;
import net.sf.tweety.arg.lp.semantics.attack.Defeat;
import net.sf.tweety.arg.lp.semantics.attack.Rebut;
import net.sf.tweety.arg.lp.semantics.attack.StrongAttack;
import net.sf.tweety.arg.lp.semantics.attack.Undercut;
import net.sf.tweety.arg.lp.syntax.Argument;
import net.sf.tweety.lp.asp.syntax.*;

import org.junit.Test;

public class AttackTest {

	@Test
	public void testUndercut() {
		AttackStrategy strategy = Undercut.getInstance();
		
		Rule r1 = new Rule( new DLPAtom("b") );
		Rule r2 = new Rule( new DLPAtom("a"), new DLPNot( new DLPAtom("b") ) ); 
		Program p = new Program();
		p.add(r1); p.add(r2);
		
		Argument A = new Argument(r1);
		Argument B = new Argument(r2);
		assertTrue(strategy.attacks(A, B));
		
		ArgumentationKnowledgeBase kb = new ArgumentationKnowledgeBase(p);
		AttackRelation notion = new AttackRelation(kb, strategy);
		assertTrue(notion.attacks(A, B));
		assertTrue( notion.getAttackingArguments(B).contains(A) );
	}
	
	@Test
	public void testRebut() {
		AttackStrategy strategy = Rebut.getInstance();
		
		Rule r1 = new Rule( new DLPAtom("b") );
		Rule r2 = new Rule( new DLPNeg( new DLPAtom("b") ) ); 
		Program p = new Program();
		p.add(r1); p.add(r2);
		
		Argument A = new Argument(r1);
		Argument B = new Argument(r2);
		assertTrue( strategy.attacks(A, B));
		assertTrue( strategy.attacks(B, A));
		
		ArgumentationKnowledgeBase kb = new ArgumentationKnowledgeBase(p);
		AttackRelation notion = new AttackRelation(kb, strategy );
		assertTrue( notion.attacks(A, B));
		assertTrue( notion.attacks(B, A));
		assertTrue( notion.getAttackingArguments(B).contains(A) );
		assertTrue( notion.getAttackingArguments(A).contains(B) );
	}
	
	@Test
	public void testAttack() {
		AttackStrategy strategy = Attack.getInstance();
		
		Rule r1 = new Rule( new DLPAtom("b") );
		Rule r2 = new Rule( new DLPNeg( new DLPAtom("b") ) );
		Rule r3 = new Rule( new DLPAtom("a"), new DLPNot( new DLPAtom("b") ) ); 
		Program p = new Program();
		p.add(r1); p.add(r2); p.add(r3);
		
		Argument A = new Argument(r1);
		Argument B = new Argument(r2);
		Argument C = new Argument(r3);
		assertTrue( strategy.attacks(A, B));
		assertTrue( strategy.attacks(B, A));
		assertTrue( strategy.attacks(A, C));
		assertFalse( strategy.attacks(C, A));
		assertFalse( strategy.attacks(B, C));
		assertFalse( strategy.attacks(C, B));
		
		
		ArgumentationKnowledgeBase kb = new ArgumentationKnowledgeBase(p);
		AttackRelation notion = new AttackRelation(kb, strategy );
		assertTrue( notion.attacks(A, B) );
		assertTrue( notion.attacks(A, C) );
		assertTrue( notion.getAttackingArguments(B).contains(A) );
		assertTrue( notion.getAttackingArguments(C).contains(A) );
		assertTrue( notion.getAttackingArguments(A).contains(B) );	
	}

	@Test
	public void testDefeat() {
		AttackStrategy strategy = Defeat.getInstance();
		
		Rule r1 = new Rule( new DLPAtom("a") );
		Rule r2 = new Rule( new DLPAtom("b"), new DLPNot(new DLPAtom("a")) );
		Rule r3 = new Rule( new DLPAtom("c"), new DLPNot(new DLPAtom("d")) );
		Rule r4 = new Rule( new DLPNeg(new DLPAtom("c")), new DLPNot(new DLPAtom("c")) );
		Program p = new Program();
		p.add(r1); p.add(r2); p.add(r3); p.add(r4);
		
		Argument A = new Argument(r1);
		Argument B = new Argument(r2);
		Argument C = new Argument(r3);
		Argument D = new Argument(r4);
		
		assertTrue( strategy.attacks(A,B));
		assertTrue( strategy.attacks(C,D));
		assertFalse( strategy.attacks(D,C));
	}

	@Test
	public void testStrongAttack() {
		AttackStrategy strategy = StrongAttack.getInstance();
		
		Rule r1 = new Rule( new DLPAtom("a") );
		Rule r2 = new Rule( new DLPAtom("b"), new DLPNot(new DLPAtom("a")) );
		Rule r3 = new Rule( new DLPNeg(new DLPAtom("a")), new DLPNot(new DLPAtom("a")) );
		Program p = new Program();
		p.add(r1); p.add(r2); p.add(r3);
		
		Argument A = new Argument(r1);
		Argument B = new Argument(r2);
		Argument C = new Argument(r3);
		
		assertTrue( strategy.attacks(A,B));
		assertTrue( strategy.attacks(A,C));
		assertFalse( strategy.attacks(B,A));
		assertFalse( strategy.attacks(C,A));
	}
	
	@Test
	public void testStrongUndercut() {
		AttackStrategy strategy = StrongAttack.getInstance();

		Rule r1 = new Rule( new DLPAtom("b"), new DLPNot(new DLPAtom("c")) );
		Rule r2 = new Rule( new DLPAtom("a"), new DLPNot(new DLPAtom("b")) );
		Program p = new Program();
		p.add(r1); p.add(r2);
		
		Argument A = new Argument(r1);
		Argument B = new Argument(r2);
		
		assertTrue( strategy.attacks(A,B));
		assertFalse( strategy.attacks(B,A));
	}

}
