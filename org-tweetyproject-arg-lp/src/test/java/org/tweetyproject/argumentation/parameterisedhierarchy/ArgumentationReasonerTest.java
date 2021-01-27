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
package org.tweetyproject.argumentation.parameterisedhierarchy;

import static org.junit.Assert.*;

import org.tweetyproject.arg.lp.reasoner.ArgumentationReasoner;
import org.tweetyproject.arg.lp.reasoner.LiteralReasoner;
import org.tweetyproject.arg.lp.semantics.attack.Attack;
import org.tweetyproject.arg.lp.semantics.attack.Undercut;
import org.tweetyproject.arg.lp.syntax.Argument;
import org.tweetyproject.arg.lp.syntax.ArgumentationKnowledgeBase;
import org.tweetyproject.lp.asp.syntax.*;

import org.junit.Before;
import org.junit.Test;

public class ArgumentationReasonerTest {
	
	// Literals
	ASPAtom p = new ASPAtom("p");
	ASPAtom q = new ASPAtom("q");
	ASPAtom r = new ASPAtom("r");
	ASPAtom s = new ASPAtom("s");
	ASPAtom m = new ASPAtom("m");
	ASPAtom n = new ASPAtom("n");
	StrictNegation n_q = new StrictNegation(q);
	StrictNegation n_s = new StrictNegation(s);
	
	// Rules
	ASPRule r1 = new ASPRule(p, new DefaultNegation(q));
	ASPRule r2 = new ASPRule(q, new DefaultNegation(p));
	ASPRule r3 = new ASPRule(n_q, new DefaultNegation(r));
	ASPRule r4 = new ASPRule(r, new DefaultNegation(s));
	ASPRule r5 = new ASPRule(n_s, new DefaultNegation(s));
	ASPRule r6 = new ASPRule(s);
	ASPRule r7 = new ASPRule(m, new DefaultNegation(n));
	ASPRule r8 = new ASPRule(n, new DefaultNegation(m));
	
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
		assertFalse(kb.getArguments().contains(new Argument(new ASPRule())));
	}
	
	@Test
	public void testArgumentationReasoner() {
		ArgumentationReasoner reasoner = new ArgumentationReasoner(Undercut.getInstance(), Attack.getInstance());
		
		assertTrue(reasoner.query(kb,a1));
		assertTrue(reasoner.query(kb,a3));
		assertTrue(reasoner.query(kb,a6));
		assertFalse(reasoner.isOverruled(kb,a1));
		assertFalse(reasoner.isOverruled(kb,a3));
		assertFalse(reasoner.isOverruled(kb,a6));
		
		assertTrue(reasoner.isOverruled(kb,a2));
		assertTrue(reasoner.isOverruled(kb,a4));
		assertTrue(reasoner.isOverruled(kb,a5));
		assertFalse(reasoner.query(kb,a2));
		assertFalse(reasoner.query(kb,a4));
		assertFalse(reasoner.query(kb,a5));
		
		assertTrue(reasoner.isDefensible(kb,a7));
		assertTrue(reasoner.isDefensible(kb,a8));
	}
	
	@Test
	public void testLiteralReasoner() {
		LiteralReasoner reasoner = new LiteralReasoner(Undercut.getInstance(), Attack.getInstance());
		
		assertTrue(reasoner.query(kb,p));
		assertTrue(reasoner.query(kb,n_q));
		assertTrue(reasoner.query(kb,s));
		assertFalse(reasoner.query(kb,q));
		assertFalse(reasoner.query(kb,r));
		assertFalse(reasoner.query(kb,n_s));
		assertFalse(reasoner.query(kb,n));
		assertFalse(reasoner.query(kb,m));
	}
	
}
