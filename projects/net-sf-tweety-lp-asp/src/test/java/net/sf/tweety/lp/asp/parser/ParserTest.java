package net.sf.tweety.lp.asp.parser;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.tweety.lp.asp.syntax.*;
import net.sf.tweety.lp.asp.util.AnswerSet;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.FunctionalTerm;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.NumberTerm;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test all important functions of the ASP-Parser in combination with the
 * InstantiateVisitor which is responsible to walk through the parse-tree and
 * generate in-memory classes of the ASP-Program saved in a file or represented
 * as a String.
 * 
 * @author Tim Janus
 */
public class ParserTest {
	
	static ASPParser parser;
	
	static InstantiateVisitor visitor;
	
	@BeforeClass
	public static void init() {
		visitor = new InstantiateVisitor();
		parser = new ASPParser(new StringReader(""));
	}
	
	@Test
	public void testSimpleTermParsing() throws ParseException {	
		parser.ReInit(new StringReader("alice"));
		ASTTerm astTerm = parser.Term();
		Term<?> term = (Term<?>) astTerm.jjtAccept(visitor, null);
		assertEquals(true, term instanceof Constant);
		assertEquals("alice", term.get());
		
		parser.ReInit(new StringReader("100"));
		astTerm = parser.Term();
		term = (Term<?>) astTerm.jjtAccept(visitor, null);
		assertEquals(true, term instanceof NumberTerm);
		assertEquals(100, term.get());
		
		parser.ReInit(new StringReader("X"));
		astTerm = parser.Term();
		term = (Term<?>) astTerm.jjtAccept(visitor, null);
		assertEquals(true, term instanceof Variable);
		assertEquals("X", term.get());
		
		parser.ReInit(new StringReader("_var"));
		astTerm = parser.Term();
		term = (Term<?>) astTerm.jjtAccept(visitor, null);
		assertEquals(true, term instanceof Variable);
		assertEquals("_var", term.get());
	}
	
	@Test
	public void testFunctionalTerm() throws ParseException {
		parser.ReInit(new StringReader("mother_of(claire)"));
		FunctionalTerm ft = visitor.visit(parser.FunctionalTerm(), null);
		assertEquals("mother_of", ft.getFunctor().getName());
		assertEquals(1, ft.getArguments().size());
		assertEquals(new Constant("claire"), ft.getArguments().get(0));
		
		parser.ReInit(new StringReader("child_of(alice,bob)"));
		ft = visitor.visit(parser.FunctionalTerm(), null);
		assertEquals("child_of", ft.getFunctor().getName());
		assertEquals(2, ft.getArguments().size());
		assertEquals(new Constant("alice"), ft.getArguments().get(0));
		assertEquals(new Constant("bob"), ft.getArguments().get(1));
	}
	
	@Test(expected=ParseException.class) 
	public void testUpperCaseFunctionalTerm() throws ParseException {
		parser.ReInit(new StringReader("FatherOf(bob)"));
		visitor.visit(parser.FunctionalTerm(), null);
	}
	
	@Test
	public void testSetTerm() throws ParseException {
		testSet(new HashSet<Term<?>>(), visitor);
		
		Set<Term<?>> set = new HashSet<Term<?>>();
		set.add(new Constant("a"));
		testSet(set, visitor);
		
		set.add(new Constant("b"));
		set.add(new Variable("X"));
		testSet(set, visitor);
	}
	
	private void testSet(Set<Term<?>> set, InstantiateVisitor visitor) throws ParseException {
		String strRep = set.toString();
		strRep = strRep.replace('[', '{').replace(']', '}');
		
		parser.ReInit(new StringReader(strRep)); 
		ASTTerm astTerm = parser.Term();
		Term<?> term = (Term<?>) astTerm.jjtAccept(visitor, null);
		assertEquals(true, term instanceof SetTerm);
		assertEquals(set, term.get());
	}
	
	@Test
	public void testListTermParsing() throws ParseException {
		testList(new ListTermValue(new Constant("a"), new Variable("X")), visitor);
		testList(new ListTermValue(new Variable("X"), new Variable("Y")), visitor);
		
		List<Term<?>> lst = new LinkedList<Term<?>>();
		lst.add(new Constant("a"));
		lst.add(new Constant("b"));
		lst.add(new Constant("c"));
		testList(new ListTermValue(lst), visitor);
		
		lst.clear();
		lst.add(new FunctionalTerm(new Functor("test"), new Constant("bob")));
		lst.add(new Constant("a"));
		testList(new ListTermValue(lst), visitor);
	}
	
	private void testList(ListTermValue base, InstantiateVisitor visitor) throws ParseException {
		String strRep = base.toString();
		
		parser.ReInit(new StringReader(strRep));
		ASTTerm astTerm = parser.Term();
		Term<?> term = (Term<?>)astTerm.jjtAccept(visitor, null);
		assertEquals(true, term instanceof ListTerm);
		ListTerm listTerm = (ListTerm)term;
		assertEquals(base.usesHeadTailSyntax(), listTerm.get().usesHeadTailSyntax());
		assertEquals(base, listTerm.get());
	}
	
	@Test
	public void testArithmetic() throws ParseException {
		String str = "3=2+1";
		parser.ReInit(new StringReader(str));
		Arithmetic arithmetic = (Arithmetic)parser.Arithmetic().jjtAccept(visitor, null);
		assertEquals(new NumberTerm(2), arithmetic.getLeftArgument());
		assertEquals(new NumberTerm(1), arithmetic.getRightArgument());
		assertEquals(new NumberTerm(3), arithmetic.getResult());
		
		str = "+(2,1,3)";
		parser.ReInit(new StringReader(str));
		Arithmetic arithmeticAlt = (Arithmetic)parser.Arithmetic().jjtAccept(visitor, null);
		assertEquals(arithmetic, arithmeticAlt);
		
		str = "3=A/B";
		parser.ReInit(new StringReader(str));
		arithmetic = (Arithmetic)parser.Arithmetic().jjtAccept(visitor, null);
		assertEquals(new Variable("A"), arithmetic.getLeftArgument());
		assertEquals("/", arithmetic.getOperator());
		
		str = "#int(X)";
		parser.ReInit(new StringReader(str));
		arithmetic = (Arithmetic)parser.Arithmetic().jjtAccept(visitor, null);
		assertEquals("#int", arithmetic.getOperator());
		assertEquals(new Variable("X"), arithmetic.getLeftArgument());
		assertEquals(null, arithmetic.getRightArgument());
		
		str = "#succ(X,Y)";
		parser.ReInit(new StringReader(str));
		arithmetic = (Arithmetic)parser.Arithmetic().jjtAccept(visitor, null);
		assertEquals(2, arithmetic.getTerms().size());
	}
	
	@Test
	public void testComparative() throws ParseException {
		String str = "1<=4";
		parser.ReInit(new StringReader(str));
		Comparative comparative = visitor.visit(parser.Comparative(), null);
		
		str = "<=(1,4)";
		parser.ReInit(new StringReader(str));
		Comparative comparativeAlt = visitor.visit(parser.Comparative(), null);
		assertEquals(comparative, comparativeAlt);
		
		str = "a > A";
		parser.ReInit(new StringReader(str));
		comparative = visitor.visit(parser.Comparative(), null);
		assertEquals(new Constant("a"), comparative.getLefthand());
		assertEquals(new Variable("A"), comparative.getRighthand());
		assertEquals(">", comparative.getOperator());
	}
	
	@Test
	public void testSymbolicSet() throws ParseException {
		String str = "{X: a, b(Y), X<Y}";
		parser.ReInit(new StringReader(str));
		SymbolicSet ss = (SymbolicSet)parser.SymbolicSet().jjtAccept(visitor, null);
		
		assertEquals(true, ss.getVariables().contains(new Variable("X")));
		assertEquals(1, ss.getVariables().size());
		assertEquals(3, ss.getConjunction().size());
	}
	
	@Test
	public void testAggregate() throws ParseException {
		String str = "0 <= #count{X,Y : a(X,Z,k),b(1,Z,Y)} <= 3";
		parser.ReInit(new StringReader(str));
		Aggregate ag = (Aggregate)parser.Aggregate().jjtAccept(visitor, null);
		assertEquals("#count", ag.getFunctor());
		assertEquals("<=", ag.getLeftOperator());
		assertEquals("<=", ag.getRightOperator());
		assertEquals(new NumberTerm(0), ag.getLeftGuard());
		assertEquals(new NumberTerm(3), ag.getRightGuard());
		
		str = "2 < #sum{V : d(V,Z)}";
		parser.ReInit(new StringReader(str));
		ag = (Aggregate)parser.Aggregate().jjtAccept(visitor, null);
		assertEquals(null, ag.getRightGuard());
		assertEquals(null, ag.getRightOperator());
		assertEquals(new NumberTerm(2), ag.getLeftGuard());
		assertEquals("<", ag.getLeftOperator());
		
		str = "#min{S : c(S)} = W";
		parser.ReInit(new StringReader(str));
		ag = (Aggregate)parser.Aggregate().jjtAccept(visitor, null);
		assertEquals(null, ag.getLeftGuard());
		assertEquals(null, ag.getLeftOperator());
		assertEquals(new Variable("W"), ag.getRightGuard());
		assertEquals("=", ag.getRightOperator());
	}
	
	@Test(expected=ParseException.class)
	public void invalidAggregateRange() throws ParseException {
		String str = "5 <= #count{X,Y : a(X,Z,k),b(1,Z,Y)} >= 3";
		parser.ReInit(new StringReader(str));
		// shall throw the exception:
		parser.Aggregate().jjtAccept(visitor, null);
	}
	
	@Test
	public void testLiteralParsing() throws ParseException {
		String str = "married(bob, alice)";
		parser.ReInit(new StringReader(str));
		DLPLiteral atom = (DLPLiteral)parser.Atom().jjtAccept(visitor, null);
		assertEquals("married", atom.getName());
		assertEquals(2, atom.getArguments().size());
		assertEquals(new Constant("bob"), atom.getArguments().get(0));
		assertEquals(new Constant("alice"), atom.getArguments().get(1));
		assertEquals(true, atom instanceof DLPAtom);
		
		str = "-guilty(coder)";
		parser.ReInit(new StringReader(str));
		DLPLiteral negation = (DLPLiteral) parser.Atom().jjtAccept(visitor, null);
		assertEquals(true, negation instanceof DLPNeg);
		
		
		// This test if the recursive call of TermLst works (there was a bug)
		str = "gender(w, parent_of(bob))";
		parser.ReInit(new StringReader(str));
		atom = visitor.visit(parser.Atom(), null);
		assertEquals(2, atom.getArguments().size());
		assertEquals(new Constant("w"), atom.getArguments().get(0));
		FunctionalTerm ft = ((FunctionalTerm)atom.getArguments().get(1));
		assertEquals("parent_of", ft.getFunctor().getName());
		assertEquals(new Constant("bob"), ft.getArguments().get(0));
	}
	
	@Test
	public void testRuleParsing() throws ParseException {
		// rule with default negation
		String str = "a :- not b.";
		parser.ReInit(new StringReader(str));
		Rule rule = (Rule)parser.Rule().jjtAccept(visitor, null);
		assertEquals(false, rule.isFact());
		assertEquals(false, rule.isConstraint());
		assertEquals(true, rule.isGround());
		assertEquals(true, rule.getConclusion().contains(new DLPAtom("a")));
		assertEquals(true, rule.getPremise().contains(new DLPNot(new DLPAtom("b"))));
		
		// constraint
		str = " :- node(X,r), node(Y,r), X!=Y.";
		parser.ReInit(new StringReader(str));
		rule = (Rule)parser.Rule().jjtAccept(visitor, null);
		assertEquals(true, rule.isConstraint());
		
		// fact
		str = "a.";
		parser.ReInit(new StringReader(str));
		rule = (Rule)parser.Rule().jjtAccept(visitor, null);
		assertEquals(true, rule.isFact());
		
		// disjunctive fact
		str = "a ; b.";
		parser.ReInit(new StringReader(str));
		rule = (Rule)parser.Rule().jjtAccept(visitor, null);
		assertEquals(true, rule.isFact());
		assertEquals(2, rule.getConclusion().size());
		
		// complex rule
		str = " q(Z) :- 2 < #sum{V : d(V,Z)}, c(Z).";
		parser.ReInit(new StringReader(str));
		rule = (Rule)parser.Rule().jjtAccept(visitor, null);
		assertEquals(2, rule.getPremise().size());
		assertEquals(true, rule.getPremise().contains(new DLPAtom("c", new Variable("Z"))));
		Set<Variable> openVars = new HashSet<Variable>();
		Set<DLPElement> conjunctino = new HashSet<DLPElement>();
		openVars.add(new Variable("V"));
		conjunctino.add(new DLPAtom("d", new Variable("V"), new Variable("Z")));
		SymbolicSet ss = new SymbolicSet(openVars, conjunctino);
		Aggregate temp = new Aggregate(new NumberTerm(2), "<", "#sum", ss);
		assertEquals(true, rule.getPremise().contains(temp));
	}
	
	@Test
	public void testAnswerSetParsing() throws ParseException {
		String str1 = "{a}";
		parser.ReInit(new StringReader(str1));
		AnswerSet as = visitor.visit(parser.AnswerSet(), null);
		assertEquals(1, as.size());
		assertEquals(true, as.contains(new DLPAtom("a")));
		
		String str2 = "{a,b,c}";
		parser.ReInit(new StringReader(str2));
		as = visitor.visit(parser.AnswerSet(), null);
		assertEquals(3, as.size());
		assertEquals(true, as.contains(new DLPAtom("b")));
		
		String str3 = "{}";
		parser.ReInit(new StringReader(str3));
		as = visitor.visit(parser.AnswerSet(), null);
		assertEquals(0, as.size());
	}
	
	@Test
	public void testProgramParsing() throws ParseException {
		String str = "%info about rule\na.";
		parser.ReInit(new StringReader(str));
		
		Program pr = visitor.visit(parser.Program(), null);
		assertEquals(1, pr.size());
	}
}