/**
 * 
 */
package net.sf.tweety.lp.asp.analysis;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sf.tweety.lp.asp.solver.Clingo;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.util.AnswerSetList;

/**
 * @author Nils Geilen <geilenn@uni-koblenz.de>
 *
 */
public class ClingoTest {

	@Test
	public void test() throws Exception {
		Clingo solver = new Clingo("C:/app/clingo/clingo.exe");
		Program p = new Program();
		solver.computeModels(p, 1);
		
		AnswerSetList asl = solver.computeModels("night.", 1);

		assertTrue(asl.size() == 2);
	}

}
