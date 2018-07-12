package net.sf.tweety.logics.ml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.ml.parser.ModalParser;
import net.sf.tweety.logics.ml.reasoner.MleanCoPReasoner;

/**
 * JUnit Test class for MleanCoP.
 * 
 * @author Anna Gessler
 */
public class MleanCoPTest {

	ModalParser parser;
	MleanCoPReasoner prover;
	public static final int DEFAULT_TIMEOUT = 10000;
	
	@Before 
	public void init() throws ParserException, IOException {
		parser = new ModalParser();
		ModalBeliefSet b = parser.parseBeliefBase("Test={test} \n type(p) \n type(q(Test)) \n p \n q(test)");
		parser.setSignature((FolSignature) b.getSignature());
		prover = new MleanCoPReasoner("/home/anna/sw/mlProver/mleancop/mleancop.sh");
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void SimpleQueryTest() throws ParserException, IOException {
		FolFormula f1 = (FolFormula) parser.parseFormula("p||!p");
		FolFormula f2 = (FolFormula) parser.parseFormula("p&&!p");
		assertTrue(prover.query(f1));
		assertFalse(prover.query(f2));
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ComplexQueryTest() throws ParserException, IOException {
		FolFormula f1 = (FolFormula) parser.parseFormula("[](forall X:(q(X)))=>forall X:( [](q(X)))");
		assertTrue(prover.query(f1));
	}
}
