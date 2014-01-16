package net.sf.tweety.logics.rcl.test;

import java.io.*;

import net.sf.tweety.*;
import net.sf.tweety.logics.fol.parser.*;
import net.sf.tweety.logics.fol.syntax.*;
import net.sf.tweety.logics.rcl.*;
import net.sf.tweety.logics.rcl.parser.*;
import net.sf.tweety.logics.rcl.semantics.*;

public class RclTest {

	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException{
		TweetyLogging.logLevel = TweetyConfiguration.LogLevel.DEBUG;
		TweetyLogging.initLogging();
		
		RclParser parser = new RclParser();
		RclBeliefSet bs = (RclBeliefSet) parser.parseBeliefBaseFromFile(args[0]);
		System.out.println("Knowledge base:\n " + bs);
		
		RelationalRankingFunction kappa = new RelationalBruteForceCReasoner(bs,parser.getSignature(),true).getCRepresentation();
		System.out.println("Simple c-representation:\n" + kappa);		
		
		System.out.println();
		System.out.println("Queries: ");
		
		FolParser pars = new FolParser();
		pars.setSignature(parser.getSignature());
		
		if(args[1] != null && !args[1].equals("")){
			BufferedReader in = new BufferedReader(new FileReader(args[1]));
			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line + "\t:\t" + kappa.rank((FolFormula)pars.parseFormula(line)));				
			}
			in.close();
		}
	}
}
