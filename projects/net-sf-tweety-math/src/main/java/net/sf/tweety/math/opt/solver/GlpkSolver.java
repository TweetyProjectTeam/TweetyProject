package net.sf.tweety.math.opt.solver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.util.Exec;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.opt.ConstraintSatisfactionProblem;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.Solver;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * Provides a Java binding to the Glpk solver (https://www.gnu.org/software/glpk).
 * @author Matthias Thimm
 *
 */
public class GlpkSolver extends Solver {

	/**Path to the binary or lp_solve*/
	private static String binary = "glpsol";
	
	/** For temporary files. */
	private static File tmpFolder = null;
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#isInstalled()
	 */
	public static boolean isInstalled() throws UnsupportedOperationException{
		try {
			Exec.invokeExecutable(GlpkSolver.binary + " -h");
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#solve(net.sf.tweety.math.opt.ConstraintSatisfactionProblem)
	 */
	@Override
	public Map<Variable, Term> solve(ConstraintSatisfactionProblem problem)	throws GeneralMathException {
		if(!problem.isLinear())
			throw new IllegalArgumentException("The solver \"glpk\" needs linear optimization problems.");
		//check existence of lp_solve first
		if(!GlpkSolver.isInstalled())
			return null;
		String output = new String();
		//String error = "";
		try{
			File outputFile = File.createTempFile("lpoutput", null, GlpkSolver.tmpFolder);
			File lpFile = File.createTempFile("lptmp", null, GlpkSolver.tmpFolder);
			// Delete temp file when program exits.
			lpFile.deleteOnExit();    
			// Write to temp file
			BufferedWriter out = new BufferedWriter(new FileWriter(lpFile));
			out.write(((OptimizationProblem)problem).convertToCplexLpFormat());
			out.close();		
			//execute lp_solve on problem in lp format and retrieve console output					
			output = Exec.invokeExecutable(GlpkSolver.binary + " --lp " + lpFile.getAbsolutePath() + " -o " + outputFile.getAbsolutePath());
			lpFile.delete();
			//parse output		
			if(output.indexOf("PROBLEM HAS NO PRIMAL FEASIBLE SOLUTION") != -1)
				throw new GeneralMathException("Problem is not feasible");
			Scanner scanner = new Scanner(outputFile);
			String outputFromFile = scanner.useDelimiter("\\A").next();
			scanner.close();
			Map<Variable,Term> result = new HashMap<Variable,Term>();
			int idx;
			String[] tokens;
			int i;
			for(Variable v: problem.getVariables()){
				idx = outputFromFile.indexOf(" " + v.getName() +  " ");
				tokens = outputFromFile.substring(idx, outputFromFile.indexOf("\n", idx)).split("\\s");
				for(i = 0; i < tokens.length && !Parser.isNumeric(tokens[i]); i++){ ; }
				result.put(v, new FloatConstant(new Double(tokens[i])));
			}			
			return result;
		}catch(IOException e){
			//TODO add error handling
			e.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	
	}
	
	/**
	 * Sets the path to the binary.
	 * @param binary the path to the binary.
	 */
	public static void setBinary(String binary){
		GlpkSolver.binary = binary;
	}
	
	/**
	 * Sets the path for the temporary folder.
	 * @param path some path.
	 */
	public static void setTmpFolder(File path){
		GlpkSolver.tmpFolder = path;
	}

}
