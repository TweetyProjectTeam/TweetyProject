/**
 * 
 */
package net.sf.tweety.lp.asp.analysis;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;

import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.tweety.lp.asp.parser.ASPParser;
import net.sf.tweety.lp.asp.parser.InstantiateVisitor;
import net.sf.tweety.lp.asp.solver.Clingo;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.util.AnswerSetList;

/**
 * @author Nils Geilen <geilenn@uni-koblenz.de>
 *
 */
public class ClingoTest {
	
static ASPParser parser;
	
	static InstantiateVisitor visitor;
	
	@BeforeClass
	public static void init() {
		visitor = new InstantiateVisitor();
		parser = new ASPParser(new StringReader(""));
	}

	@Test
	public void test() throws Exception {
		Clingo solver = new Clingo("C:/app/clingo/clingo.exe");

		FileInputStream fistr = new FileInputStream(new File("../../examples/asp/ex1.asp"));
		parser.ReInit(fistr);
		
		Program p = visitor.visit(parser.Program(), null);
		solver.computeModels(p, 1);
		
		AnswerSetList asl = solver.computeModels(p, 1);

		System.out.println(asl.get(1));
		assertTrue(asl.size() == 2);
	}

}
