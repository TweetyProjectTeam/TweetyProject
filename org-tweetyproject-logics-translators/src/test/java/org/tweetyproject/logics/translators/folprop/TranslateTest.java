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
package org.tweetyproject.logics.translators.folprop;

import static org.junit.Assert.assertEquals;
import org.tweetyproject.logics.commons.error.LanguageException;
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.fol.syntax.Conjunction;
import org.tweetyproject.logics.fol.syntax.Disjunction;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.pl.syntax.Proposition;

import org.junit.Test;

/**
 * Tests the FOLPropTranslator
 * @author Tim Janus
 */
public class TranslateTest {

	private static FOLPropTranslator translator = new FOLPropTranslator();
	
	@Test
	public void testPropToFOLAtomTranslation() {
		Proposition prop = new Proposition("test");
		FolAtom atom = translator.toFOL(prop);
		assertEquals(prop.getName(), atom.getName());
		assertEquals(0, atom.getArguments().size());
	}
	
	@Test
	public void testFOLToPropAtomTranslation() {
		FolAtom atom = new FolAtom(new Predicate("test"));
		Proposition prop = translator.toPropositional(atom);
		assertEquals(atom.getName(), prop.getName());
	}
	
	@Test(expected=LanguageException.class)
	public void testFOLToPropAtomTranslationFAILCauseArgs() {
		FolAtom atom = new FolAtom(new Predicate("is_male", 1), new Constant("bob"));
		translator.toPropositional(atom);
	}
	
	@Test
	public void testDisjunctionFOLtoProp() {
		Disjunction dis = new Disjunction();
		dis.add(new FolAtom(new Predicate("a")));
		dis.add(new FolAtom(new Predicate("b")));
		org.tweetyproject.logics.pl.syntax.Disjunction td = translator.toPropositional(dis);
		assertEquals(2, td.size());
		assertEquals(true, td.contains(new Proposition("a")));
		assertEquals(true, td.contains(new Proposition("b")));
	}
	
	@Test
	public void testNestedConjunction() {
		org.tweetyproject.logics.pl.syntax.Conjunction con = new org.tweetyproject.logics.pl.syntax.Conjunction();
		con.add(new Proposition("a"));
		org.tweetyproject.logics.pl.syntax.Disjunction nested = new org.tweetyproject.logics.pl.syntax.Disjunction();
		nested.add(new Proposition("b"));
		nested.add(new Proposition("c"));
		con.add(nested);
		
		Conjunction tc = translator.toFOL(con);
		assertEquals(2, tc.size());
		assertEquals(true, tc.contains(new FolAtom(new Predicate("a"))));
		assertEquals(translator.toFOL(nested), tc.get(1));
	}
}
