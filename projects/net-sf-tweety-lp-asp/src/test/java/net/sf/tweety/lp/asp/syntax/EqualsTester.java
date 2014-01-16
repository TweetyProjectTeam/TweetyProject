package net.sf.tweety.lp.asp.syntax;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.FunctionalTerm;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.NumberTerm;
import net.sf.tweety.logics.commons.syntax.Variable;

import org.junit.Test;

public class EqualsTester {
	@Test
	public void testELPAtom() {
		DLPAtom a1 = new DLPAtom("test", new NumberTerm(1));
		DLPAtom a2 = new DLPAtom("test", new NumberTerm(1), new NumberTerm(2));
		
		assertEquals(false, a1.equals(a2));
		assertEquals(true, a1.equals(new DLPAtom("test", new NumberTerm(1))));
		assertEquals(false, a1.equals(new DLPAtom("test", new NumberTerm(2))));
	}
	
	@Test
	public void testCloneEqualAtom() {
		DLPAtom atom = new DLPAtom("blub", new Variable("X"), new Constant("y"), 
				new FunctionalTerm(new Functor("test"), new NumberTerm(1), new Variable("X"), new Constant("x")));
		assertEquals(true, atom.equals(atom.clone())); 
	}
	
	@Test
	public void testRule() {
		Rule r1 = new Rule();
		r1.setConclusion(new DLPAtom("test"));
		
		Rule r2 = new Rule(new DLPAtom("test"), new DLPAtom("not_released"));
		assertEquals(false, r1.equals(r2));
	}
	
	@Test
	public void testProgram() {
		Program p1 = new Program();
		Program p2 = new Program();
		
		assertEquals(true, p1.equals(p2));
		p1.add(new Rule(new DLPAtom("test")));
		assertEquals(false, p1.equals(p2));
	}
	
	@Test
	public void testAddAll() {
		Rule r1 = new Rule(new DLPAtom("a1"));
		Rule r2 = new Rule(new DLPAtom("a2"));
		Rule r3 = new Rule(new DLPAtom("a3"));
		
		List<Rule> rules = new LinkedList<Rule>();
		rules.add(r1);
		rules.add(r2);
		rules.add(r3);
		Program p2 = new Program();
		p2.addAll(rules);
		
		assertEquals(true, p2.contains(r3));
	}
}
