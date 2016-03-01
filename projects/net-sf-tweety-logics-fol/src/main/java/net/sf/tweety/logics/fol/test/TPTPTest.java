package net.sf.tweety.logics.fol.test;

import org.junit.Test;

import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.prover.EProver;
import net.sf.tweety.logics.fol.prover.TPTPPrinter;
import net.sf.tweety.logics.fol.syntax.FolFormula;

/**
 * JUnitTest to test Eprover implemnetation
 * @author Nls Geilen
 *
 */

public class TPTPTest {

	@Test
	public void test() throws Exception {
		System.out.println(System.getProperty("os.name").matches("Lin.*"));
		
		FolParser parser = new FolParser();		
		FolBeliefSet b = parser.parseBeliefBaseFromFile("eprover_example.txt");
		TPTPPrinter printer = new TPTPPrinter();
		System.out.println(printer.toTPTP(b));
		
		FolFormula query = (FolFormula)parser.parseFormula("test(tuffy)");
		System.out.println(printer.makeQuery("query2", query));
		
		//EProver e = new EProver("C:/app/E/PROVER/eprover.exe","C:/Users/me/tptp_ws/");
		
		EProver e = new EProver("/home/nils/app/E/PROVER/eprover");

		System.out.print(e.query(b, query));
	}
	
	
}




