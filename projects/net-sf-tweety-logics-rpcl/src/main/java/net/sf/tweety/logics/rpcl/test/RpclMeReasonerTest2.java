package net.sf.tweety.logics.rpcl.test;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.sf.tweety.ParserException;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.rpcl.RpclBeliefSet;
import net.sf.tweety.logics.rpcl.RpclMeReasoner;
import net.sf.tweety.logics.rpcl.parser.RpclParser;
import net.sf.tweety.logics.rpcl.semantics.AggregatingSemantics;

public class RpclMeReasonerTest2 {

	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException{
		RpclParser parser = new RpclParser();
		RpclBeliefSet bs = parser.parseBeliefBaseFromFile("/Users/mthimm/Versioning/sourceforge-tweety/trunk/examples/rpcl/cold.rpcl");
		System.out.println(bs);
		FolParser folParser = new FolParser();
		folParser.setSignature((FolSignature)bs.getSignature());
		FolFormula query = (FolFormula)folParser.parseFormula("cold(anna)");
		RpclMeReasoner reasoner = new RpclMeReasoner(bs, new AggregatingSemantics(),(FolSignature)bs.getSignature(), RpclMeReasoner.STANDARD_INFERENCE);
		System.out.println(reasoner.query(query));		
	}
}
