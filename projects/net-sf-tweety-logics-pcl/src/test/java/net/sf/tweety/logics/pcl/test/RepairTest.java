package net.sf.tweety.logics.pcl.test;

import java.io.IOException;

import net.sf.tweety.ParserException;
import net.sf.tweety.logics.pcl.PclBeliefSet;
import net.sf.tweety.logics.pcl.analysis.GeneralizedMeMachineShop;
import net.sf.tweety.logics.pcl.parser.PclParser;
import net.sf.tweety.logics.pcl.syntax.ProbabilisticConditional;
import net.sf.tweety.math.opt.solver.OpenOptWebSolver;

public class RepairTest {

	public static void main(String[] args) throws ParserException, IOException{
		// NOTE: In order to use generalized ME reasoning and the generalized ME-reasoner an OpenOpt installation has to be used. There are three options for this:
		// * In order to use the web service of OpenOpt please request an API key from Matthias (thimm@uni-koblenz.de).
		// * You can also set up your own web service with the file OpenOptWebSolver.php (OpenOpt has to be installed on the server)" and modify "OpenOptWebSolver.openopt_webservice_url" accordingly
		// * If you have a local installation of OpenOpt just set the following variable to "true"		
		OpenOptWebSolver.openopt_use_local = false;		
		
		// configure web service
		OpenOptWebSolver.openopt_webservice_url = "http://tweety.west.uni-koblenz.de/opt/openopt/solve.php";		
		OpenOptWebSolver.openopt_webservice_apikey = "Please request an API key from Matthias (thimm@uni-koblenz.de) or set up your own web service with the file OpenOptWebSolver.php (OpenOpt has to be installed on the server)";
		
		// some inconsistent belief base
		PclBeliefSet kb = new PclBeliefSet();
		PclParser parser = new PclParser();
		//kb.add((ProbabilisticConditional)parser.parseFormula("(A)[0]"));
		//kb.add((ProbabilisticConditional)parser.parseFormula("(A)[1]"));
		kb.add((ProbabilisticConditional)parser.parseFormula("(sp)[0.25]"));
		kb.add((ProbabilisticConditional)parser.parseFormula("(sp|ss)[0.8]"));
		kb.add((ProbabilisticConditional)parser.parseFormula("(sp|sc)[0.6]"));
		kb.add((ProbabilisticConditional)parser.parseFormula("(sc|sp)[0.7]"));
		kb.add((ProbabilisticConditional)parser.parseFormula("(ss|sp)[0.5]"));
		kb.add((ProbabilisticConditional)parser.parseFormula("(sc| !sp )[0.05]"));
		kb.add((ProbabilisticConditional)parser.parseFormula("(ss| !sp )[0.01]"));
		System.out.println(kb);
		
		// repair
		GeneralizedMeMachineShop gm = new GeneralizedMeMachineShop(2);
		System.out.println(gm.repair(kb));
	}
}
