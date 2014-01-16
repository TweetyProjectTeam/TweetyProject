package net.sf.tweety.logics.fol.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.tweety.ParserException;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.fol.ClassicalInference;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;

public class FolTest {

	public static void test() throws ParserException, IOException{
		FolParser parser = new FolParser();
		FolBeliefSet bs = new FolBeliefSet();
		FolSignature sig = new FolSignature();
		Sort animal = new Sort("Animal");
		sig.add(animal);
		List<Sort> l = new ArrayList<Sort>();
		l.add(animal);
		l.add(animal);
		sig.add(new Predicate("A",l));
		sig.add(new Constant("b",animal));
		sig.add(new Constant("c",animal));
		parser.setSignature(sig);		
		bs.add((FolFormula)parser.parseFormula("forall X: (forall Y: ((!A(X,Y) || A(Y,X)) && (!A(Y,X) || A(X,Y))))"));
		bs.add((FolFormula)parser.parseFormula("A(b,c)"));
		ClassicalInference infer = new ClassicalInference(bs);
		System.out.println(infer.query(parser.parseFormula("A(c,b)")));
	}
	
	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException{
		//test();System.exit(1);
		FolParser parser = new FolParser();		
		FolBeliefSet b = parser.parseBeliefBaseFromFile("examplebeliefbase.fologic");
		System.out.println(b);
		ClassicalInference infer = new ClassicalInference(b);
		System.out.println(infer.query(parser.parseFormula("Knows(martin,carl)")));
	}
}
