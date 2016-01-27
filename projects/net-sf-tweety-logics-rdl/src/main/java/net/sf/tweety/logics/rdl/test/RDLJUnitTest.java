package net.sf.tweety.logics.rdl.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sf.tweety.logics.rdl.NaiveDefaultReasoner;
import net.sf.tweety.logics.rdl.parser.RdlParser;

/**
 * 
 * @author Nils Geilen
 *
 */

public class RDLJUnitTest {
	
	@Test
	public void test1() throws Exception{
		String bsp = "Animal = {tweety, penguin} \n"
				+ "type(Flies(Animal)) \n type(Bird(Animal)) \n "
				+ "Bird(tweety) \n Bird(penguin) \n !Flies(penguin) \n "
				+" Bird(X)::Flies(X)/Flies(X)";

		RdlParser parser = new RdlParser();
		NaiveDefaultReasoner ndr = new NaiveDefaultReasoner(parser.parseBeliefBase(bsp));
		assertTrue(ndr.query(parser.parseFormula("Flies(tweety)")).getAnswerBoolean());
		assertFalse(ndr.query(parser.parseFormula("Flies(penguin)")).getAnswerBoolean());
		assertFalse(ndr.query(parser.parseFormula("!Flies(tweety)")).getAnswerBoolean());
		assertTrue(ndr.query(parser.parseFormula("!Flies(penguin)")).getAnswerBoolean());
		assertTrue(ndr.getAllExtensions().size()==1);
	}
	
	@Test
	public void test2() throws Exception{
		String bsp = "type(a) \n type(b) \n type(c) \n "
				+ "a \n "
				+" a::!b/!b \n ::c/b";

		RdlParser parser = new RdlParser();
		NaiveDefaultReasoner ndr = new NaiveDefaultReasoner(parser.parseBeliefBase(bsp));
		assertTrue(ndr.query(parser.parseFormula("a")).getAnswerBoolean());
		assertTrue(ndr.query(parser.parseFormula("b")).getAnswerBoolean());
		assertFalse(ndr.query(parser.parseFormula("!b")).getAnswerBoolean());
		assertTrue(ndr.getAllExtensions().size()==1);
	}
	
	@Test
	public void test3() throws Exception{
		String bsp = "type(a) \n type(b) \n type(c) \n "
				+ " \n "
				+" ::a/a \n ::b/!b";

		RdlParser parser = new RdlParser();
		NaiveDefaultReasoner ndr = new NaiveDefaultReasoner(parser.parseBeliefBase(bsp));
		assertTrue(ndr.getAllExtensions().isEmpty());
	}
	
	@Test
	public void test4() throws Exception{
		String bsp = "type(a) "
				+ " \n "
				+" ::a/a \n ::!a/!a";

		RdlParser parser = new RdlParser();
		NaiveDefaultReasoner ndr = new NaiveDefaultReasoner(parser.parseBeliefBase(bsp));
		assertTrue(ndr.getAllExtensions().size() == 2);
	}
	
	
	@Test
	public void test5() throws Exception{
		String bsp = "type(a) \n type(b) \n type(c) \n type(d) \n"
				+ "\n "
				+" ::!b;!d/a \n ::!b;!d/c \n ::!a;!c/d \n a::!c/b \n";

		RdlParser parser = new RdlParser();
		NaiveDefaultReasoner ndr = new NaiveDefaultReasoner(parser.parseBeliefBase(bsp));
		assertTrue(ndr.query(parser.parseFormula("a")).getAnswerBoolean());
		assertTrue(ndr.query(parser.parseFormula("c")).getAnswerBoolean());
		assertTrue(ndr.query(parser.parseFormula("d")).getAnswerBoolean());
		assertFalse(ndr.query(parser.parseFormula("b")).getAnswerBoolean());
		assertFalse(ndr.query(parser.parseFormula("!b")).getAnswerBoolean());
		assertTrue(ndr.getAllExtensions().size()==2);
	}

}
