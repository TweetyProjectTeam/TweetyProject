package net.sf.tweety.argumentation.parameterisedhierarchy;

import static org.junit.Assert.*;

import net.sf.tweety.arg.lp.ArgumentationKnowledgeBase;
import net.sf.tweety.arg.lp.ArgumentationReasoner;
import net.sf.tweety.arg.lp.LiteralReasoner;
import net.sf.tweety.arg.lp.semantics.attack.Attack;
import net.sf.tweety.arg.lp.semantics.attack.Undercut;
import net.sf.tweety.arg.lp.syntax.Argument;
import net.sf.tweety.lp.asp.syntax.*;

import org.junit.Before;
import org.junit.Test;

public class ArgumentationReasonerTest {
	
	// Literals
	DLPAtom p = new DLPAtom("p");
	DLPAtom q = new DLPAtom("q");
	DLPAtom r = new DLPAtom("r");
	DLPAtom s = new DLPAtom("s");
	DLPAtom m = new DLPAtom("m");
	DLPAtom n = new DLPAtom("n");
	DLPNeg n_q = new DLPNeg(q);
	DLPNeg n_s = new DLPNeg(s);
	
	// Rules
	Rule r1 = new Rule(p, new DLPNot(q));
	Rule r2 = new Rule(q, new DLPNot(p));
	Rule r3 = new Rule(n_q, new DLPNot(r));
	Rule r4 = new Rule(r, new DLPNot(s));
	Rule r5 = new Rule(n_s, new DLPNot(s));
	Rule r6 = new Rule(s);
	Rule r7 = new Rule(m, new DLPNot(n));
	Rule r8 = new Rule(n, new DLPNot(m));
	
	// Arguments
	Argument a1 = new Argument(r1);
	Argument a2 = new Argument(r2);
	Argument a3 = new Argument(r3);
	Argument a4 = new Argument(r4);
	Argument a5 = new Argument(r5);
	Argument a6 = new Argument(r6);
	Argument a7 = new Argument(r7);
	Argument a8 = new Argument(r8);
	
	Program prog;
	ArgumentationKnowledgeBase kb;
	
	@Before
	public void initKB() {
		// setup  test program
		prog = new Program();
		prog.add( r1 );
		prog.add( r2 );
		prog.add( r3 );
		prog.add( r4 );
		prog.add( r5 );
		prog.add( r6 );
		prog.add( r7 );
		prog.add( r8 );
		kb = new ArgumentationKnowledgeBase(prog);
	}
	
	@Test
	public void testArgumentationKnowledgeBase() {
		assertTrue(kb.getArguments().contains(a1));
		assertTrue(kb.getArguments().contains(a2));
		assertTrue(kb.getArguments().contains(a3));
		assertTrue(kb.getArguments().contains(a4));
		assertTrue(kb.getArguments().contains(a5));
		assertTrue(kb.getArguments().contains(a6));
		assertTrue(kb.getArguments().contains(a7));
		assertTrue(kb.getArguments().contains(a8));
		assertFalse(kb.getArguments().contains(new Argument(new Rule())));
	}
	
	@Test
	public void testArgumentationReasoner() {
		ArgumentationReasoner reasoner = new ArgumentationReasoner(kb, Undercut.getInstance(), Attack.getInstance());
		
		assertTrue(reasoner.isJustified(a1));
		assertTrue(reasoner.isJustified(a3));
		assertTrue(reasoner.isJustified(a6));
		assertFalse(reasoner.isOverruled(a1));
		assertFalse(reasoner.isOverruled(a3));
		assertFalse(reasoner.isOverruled(a6));
		
		assertTrue(reasoner.isOverruled(a2));
		assertTrue(reasoner.isOverruled(a4));
		assertTrue(reasoner.isOverruled(a5));
		assertFalse(reasoner.isJustified(a2));
		assertFalse(reasoner.isJustified(a4));
		assertFalse(reasoner.isJustified(a5));
		
		assertTrue(reasoner.isDefensible(a7));
		assertTrue(reasoner.isDefensible(a8));
	}
	
	@Test
	public void testLiteralReasoner() {
		LiteralReasoner reasoner = new LiteralReasoner(kb, Undercut.getInstance(), Attack.getInstance());
		
		assertTrue(reasoner.query(p).getAnswerBoolean());
		assertTrue(reasoner.query(n_q).getAnswerBoolean());
		assertTrue(reasoner.query(s).getAnswerBoolean());
		assertFalse(reasoner.query(q).getAnswerBoolean());
		assertFalse(reasoner.query(r).getAnswerBoolean());
		assertFalse(reasoner.query(n_s).getAnswerBoolean());
		assertFalse(reasoner.query(n).getAnswerBoolean());
		assertFalse(reasoner.query(m).getAnswerBoolean());
	}
	
}
