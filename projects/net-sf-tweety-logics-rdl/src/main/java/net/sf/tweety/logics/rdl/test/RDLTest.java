package net.sf.tweety.logics.rdl.test;

import java.util.LinkedList;

import net.sf.tweety.logics.fol.ClassicalInference;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.rdl.DefaultTheory;
import net.sf.tweety.logics.rdl.parser.RdlParser;
import net.sf.tweety.logics.rdl.syntax.DefaultRule;

public class RDLTest {

	public static void main(String[] args) throws Exception {
		RdlParser parser = new RdlParser();
		DefaultTheory th = parser.parseBeliefBaseFromFile("example_default_theory.txt");
		System.out.println(th);
	}

}
