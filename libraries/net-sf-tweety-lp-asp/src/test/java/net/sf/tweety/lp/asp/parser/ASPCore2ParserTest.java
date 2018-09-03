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
package net.sf.tweety.lp.asp.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.FunctionalTerm;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.NumberTerm;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.lp.asp.syntax.ASPAtom;
import net.sf.tweety.lp.asp.syntax.ASPLiteral;
import net.sf.tweety.lp.asp.syntax.ASPOperator;
import net.sf.tweety.lp.asp.syntax.ASPRule;
import net.sf.tweety.lp.asp.syntax.Aggregate;
import net.sf.tweety.lp.asp.syntax.AggregateElement;
import net.sf.tweety.lp.asp.syntax.ArithmeticTerm;
import net.sf.tweety.lp.asp.syntax.ComparativeAtom;
import net.sf.tweety.lp.asp.syntax.DefaultNegation;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.syntax.StrictNegation;

/**
 * This class tests all important functions of the ASP-Core-2 Parser in
 * combination with InstantiateVisitor, which is responsible for walking through
 * the parse-tree and generating in-memory classes of the parsed ASP program.
 *
 * TODO: Add choice rules and optimize statements (as they are completed in the
 * parser)
 * 
 * @author Anna Gessler
 * @author Tim Janus
 *
 */
public class ASPCore2ParserTest {

	static ASPCore2Parser parser;
	static InstantiateVisitor visitor;
	public static final int DEFAULT_TIMEOUT = 5000;

	@BeforeClass
	public static void init() {
		visitor = new InstantiateVisitor();
		parser = new ASPCore2Parser(new StringReader(""));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void ProgramTest() throws ParseException {
		String pstr = "motive(harry). \n" + "motive(sally). \n" + "guilty(harry). \n"
				+ "innocent(Suspect) :- motive(Suspect), not guilty(Suspect). \n";
		Program p1 = ASPCore2Parser.parseProgram(pstr);
		assertEquals(p1.getRules().size(), 4);
		assertFalse(p1.hasQuery());

		String pstr2 = "motive(harry). \n" + "motive(sally). \n" + "guilty(harry). \n"
				+ "innocent(Suspect) :- motive(Suspect), not guilty(Suspect). \n guilty(sally)?";
		Program p2 = ASPCore2Parser.parseProgram(pstr2);
		assertTrue(p2.hasQuery());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void SimpleRulesTest() throws ParseException {
		ASPRule fact = ASPCore2Parser.parseRule("motive(harry).");
		ASPRule negFact = ASPCore2Parser.parseRule("-guilty(sally).");
		ASPRule nafConstraint = ASPCore2Parser.parseRule(":- not guilty(sally), guilty(harry).");
		ASPRule simpleRule = ASPCore2Parser.parseRule("innocent(Suspect) :- motive(Suspect), not guilty(Suspect).");

		ASPLiteral l1 = fact.getLiterals().iterator().next();
		Constant c = (Constant) l1.getArguments().iterator().next();
		assertEquals(l1.getName(), "motive");
		assertEquals(c.get(), "harry");
		assertTrue(fact.isFact());
		assertTrue(negFact.isFact());
		assertTrue((negFact.getLiterals().iterator().next()) instanceof StrictNegation);
		assertTrue(nafConstraint.isConstraint());
		assertTrue((nafConstraint.getBody().iterator().next()) instanceof DefaultNegation);
		assertFalse(simpleRule.isFact());
		assertEquals(simpleRule.getHead().size(), 1);
		assertEquals(simpleRule.getBody().size(), 2);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void AggregateTest() throws ParseException {
		parser.ReInit(new StringReader("#sum{ I : brotherOf(harry,X)}"));
		Aggregate a = (Aggregate) parser.Aggregate().jjtAccept(visitor, null);
		assertEquals(a.getFunction(), ASPOperator.AggregateFunction.SUM);
		List<AggregateElement> elems = a.getAggregateElements();
		ASPLiteral lit = elems.get(0).getLiterals().first();
		assertEquals(elems.size(), 1);
		assertEquals(lit.getName(), "brotherOf");
		assertEquals(elems.get(0).getLeft().size(), 1);

		parser.ReInit(new StringReader("X = #min{ I : age(harry) ; I : wealth(harry)} != 20"));
		a = (Aggregate) parser.Aggregate().jjtAccept(visitor, null);
		assertEquals(a.getRightRelation(), ASPOperator.BinaryOperator.NEQ);
		assertEquals(a.getRightRelationTerm(), new NumberTerm(20));
		assertEquals(a.getLeftRelationTerm(), new Variable("X"));
		assertEquals(a.getAggregateElements().size(), 2);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void WeightAtLevelTest() throws ParseException {
		ASPRule r1 = ASPCore2Parser.parseRule(":~ guilty(sally). [4@2,23,X,-2]");
		NumberTerm w = (NumberTerm) r1.getWeight();
		NumberTerm l = (NumberTerm) r1.getLevel();
		List<Term<?>> terms = r1.getConstraintTerms();
		assertEquals(w.get().intValue(), 4);
		assertEquals(l.get().intValue(), 2);
		assertEquals(terms.size(), 3);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void SimpleTermTest() throws ParseException {
		parser.ReInit(new StringReader("42"));
		Term<?> t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof NumberTerm);
		assertEquals(((NumberTerm) t).get().intValue(), 42);

		parser.ReInit(new StringReader("constant"));
		t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof Constant);

		parser.ReInit(new StringReader("functor(constant)"));
		t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof FunctionalTerm);

		parser.ReInit(new StringReader("Variable"));
		t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof Variable);

		parser.ReInit(new StringReader("\"String\""));
		t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof Constant);

		parser.ReInit(new StringReader("_"));
		t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof Variable);

		parser.ReInit(new StringReader("-34"));
		t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof ArithmeticTerm);

		parser.ReInit(new StringReader("-X"));
		t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof ArithmeticTerm);

		parser.ReInit(new StringReader("( -X )"));
		t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof ArithmeticTerm);

		parser.ReInit(new StringReader("(5+5)/6"));
		t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof ArithmeticTerm);
		ArithmeticTerm at = (ArithmeticTerm) t;
		assertEquals(((ArithmeticTerm) at.getLeft()).getOperator(), ASPOperator.ArithmeticOperator.PLUS);
		assertEquals(at.getOperator(), ASPOperator.ArithmeticOperator.DIV);
		assertTrue(at.getRight() instanceof NumberTerm);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void ComplexTermTest() throws ParseException {
		parser.ReInit(new StringReader("(age(peter) - V)*(12-23)"));
		ArithmeticTerm t = (ArithmeticTerm) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof ArithmeticTerm);
		assertEquals(t.getOperator(), ASPOperator.ArithmeticOperator.TIMES);
		assertTrue(((ArithmeticTerm) t.getLeft()).getRight() instanceof Variable);
		assertEquals(((ArithmeticTerm) t.getLeft()).getOperator(), ASPOperator.ArithmeticOperator.MINUS);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void ComparativeTest() throws ParseException {
		parser.ReInit(new StringReader("23=23"));
		ComparativeAtom at = (ComparativeAtom) parser.BuiltinAtom().jjtAccept(visitor, null);
		assertEquals(at.getOperator(), ASPOperator.BinaryOperator.EQ);

		parser.ReInit(new StringReader("2<>23"));
		at = (ComparativeAtom) parser.BuiltinAtom().jjtAccept(visitor, null);
		assertEquals(at.getOperator(), ASPOperator.BinaryOperator.NEQ);

		parser.ReInit(new StringReader("Var!=23"));
		at = (ComparativeAtom) parser.BuiltinAtom().jjtAccept(visitor, null);
		assertEquals(at.getOperator(), ASPOperator.BinaryOperator.NEQ);

		parser.ReInit(new StringReader("2<Var"));
		at = (ComparativeAtom) parser.BuiltinAtom().jjtAccept(visitor, null);
		assertEquals(at.getOperator(), ASPOperator.BinaryOperator.LT);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void LiteralTest() throws ParseException {
		parser.ReInit(new StringReader("a"));
		ASPAtom at = (ASPAtom) parser.ClassicalLiteral().jjtAccept(visitor, null);
		assertEquals(at.getName(), "a");
		parser.ReInit(new StringReader("guilty(sally)"));
		at = (ASPAtom) parser.ClassicalLiteral().jjtAccept(visitor, null);
		assertEquals(at.getName(), "guilty");
		parser.ReInit(new StringReader("-guilty(sally)"));
		StrictNegation neg = (StrictNegation) parser.ClassicalLiteral().jjtAccept(visitor, null);
		assertEquals(neg.getName(), "guilty");
		assertEquals(neg.getAtom().getName(), at.getName());

		parser.ReInit(new StringReader("not -guilty(sally)"));
		DefaultNegation naf_at = (DefaultNegation) parser.NAFLiteral().jjtAccept(visitor, null);
		StrictNegation neg2 = (StrictNegation) naf_at.getLiteral();
		assertEquals(neg.getAtom().getName(), neg2.getAtom().getName());
	}
}
