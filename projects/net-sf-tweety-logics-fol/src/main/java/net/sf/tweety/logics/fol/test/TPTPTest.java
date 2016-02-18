package net.sf.tweety.logics.fol.test;

import org.junit.Test;

import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.prover.EProver;
import net.sf.tweety.logics.fol.prover.TPTPPrinter;
import net.sf.tweety.logics.fol.syntax.FolFormula;

public class TPTPTest {

	@Test
	public void test() throws Exception {
		FolParser parser = new FolParser();		
		FolBeliefSet b = parser.parseBeliefBaseFromFile("eprover_example.txt");
		TPTPPrinter printer = new TPTPPrinter();
		System.out.println(printer.toTPTP(b));
		
		FolFormula query = (FolFormula)parser.parseFormula("!test(tuffy)");
		System.out.println(printer.makeQuery("query2", query));
		
		EProver e = new EProver("C:/app/E/PROVER/eprover.exe","C:/Users/me/tptp_ws/");
		System.out.print(e.query(b, query));
	}
	
	
}




