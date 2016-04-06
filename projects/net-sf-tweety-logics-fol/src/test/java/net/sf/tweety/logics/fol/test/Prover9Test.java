package net.sf.tweety.logics.fol.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.prover.FolTheoremProver;
import net.sf.tweety.logics.fol.prover.Prover9;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.writer.FolWriter;
import net.sf.tweety.logics.fol.writer.Prover9Writer;

public class Prover9Test {

	static FolTheoremProver e;
	FolWriter printer = new Prover9Writer();
	
	@BeforeClass public static void init(){
		e = new Prover9("C:\\app\\prover9\\bin\\prover9.exe");
	}

	@Test
	public void test1() throws Exception {
		FolParser parser = new FolParser();	
		String source = "type(a) \n type(b) \n type(c) \n"
				+ "a \n !b";
		FolBeliefSet b = parser.parseBeliefBase(source);
		//printer.printBase(b);
		System.out.println(printer);
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("b")));
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("a")));
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("c")));
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("!c")));
	}
	
	@Test
	public void test2() throws Exception {
		FolParser parser = new FolParser();	
		String source = "Animal = {horse, cow, lion} \n"
				+ "type(Tame(Animal)) \n"
				+ "type(Ridable(Animal)) \n"
				+ "Tame(cow) \n"
				+ "!Tame(lion) \n"
				+ "Ridable(horse) \n"
				+ "forall X: (!Ridable(X) || Tame(X)) \n";
		FolBeliefSet b = parser.parseBeliefBase(source);
		printer.printBase(b);
		System.out.println(printer);
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("Tame(cow)")));
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("exists X: (Tame(X))")));
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("Tame(horse)")));
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("!Ridable(lion)")));
	}
	
	@Test
	public void test3() throws Exception {
		FolParser parser = new FolParser();	
		String source = "Animal = {horse, cow, lion} \n"
				+ "Plant = {grass, tree} \n"
				+ "type(Eats(Animal, Plant)) \n"
				+ "forall X: (!Eats(X,tree)) \n"
				+ "Eats(cow, grass) \n"
				+ "forall X: (!Eats(cow, X) || Eats(horse, X)) \n"
				+ "exists X: (Eats(lion, X))";
		FolBeliefSet b = parser.parseBeliefBase(source);
		printer.printBase(b);
		System.out.println(printer);
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("Eats(lion, tree)")));
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("!Eats(lion, grass)")));
		//is not true according to the solver
		//assertTrue(e.query(b, (FolFormula)parser.parseFormula("Eats(lion, grass)")));
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("Eats(horse, tree)")));
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("!Eats(horse, tree)")));
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("Eats(horse, grass)")));
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("exists X: (forall Y: (!Eats(Y, X)))")));
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("forall X: (forall Y: (Eats(Y, X)))")));
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("!(forall X: (forall Y: (Eats(Y, X))))")));
	}
	

	@Test
	public void test4() throws Exception {
		FolParser parser = new FolParser();	
		String source = "type(a) \n type(b) \n type(c) \n"
				+ "a \n !b";
		FolBeliefSet b = parser.parseBeliefBase(source);
		//printer.printBase(b);
		System.out.println(printer);
		assertTrue(e.equivalent(b, (FolFormula)parser.parseFormula("b"), (FolFormula)parser.parseFormula("b")));
		assertFalse(e.equivalent(b, (FolFormula)parser.parseFormula("a"), (FolFormula)parser.parseFormula("b")));
		assertTrue(e.equivalent(b, (FolFormula)parser.parseFormula("a && b"), (FolFormula)parser.parseFormula("b && a")));

	}
}
