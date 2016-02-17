package net.sf.tweety.logics.fol.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;

import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.prover.TPTPPrinter;

public class TPTPTest {

	@Test
	public void test() throws Exception {
		FolParser parser = new FolParser();		
		FolBeliefSet b = parser.parseBeliefBaseFromFile("examplebeliefbase.fologic");
		TPTPPrinter printer = new TPTPPrinter();
		System.out.println(printer.toTPTP(b));
		
	
		
	}

}
