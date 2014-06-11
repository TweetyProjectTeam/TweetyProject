package net.sf.tweety.math.opt.solver;

import java.io.*;
import java.util.*;

import net.sf.tweety.math.opt.*;
import net.sf.tweety.math.term.*;
import net.sf.tweety.util.Exec;


/**
 * This class implements a wrapper to the lpsolve binary
 * for mixed integer  linear programming. See http://lpsolve.sourceforge.net.
 * @author Matthias Thimm
 */
public class LpSolve implements Solver {
	
	/**Path to the binary or lp_solve*/
	private static String binary = "lp_solve";
	
	/** For temporary files. */
	private static File tmpFolder = null;
	
	/**
	 * Checks whether the lp_solve binary is accessible
	 * @return "true" if lp_solve is correctly installed
	 */
	public static boolean checkBinary(){
		try {
			Exec.invokeExecutable(LpSolve.binary + " -h");
			return true;
		} catch (Exception e) {
			return false;
		}		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#solve()
	 */
	@Override
	public Map<Variable, Term> solve(ConstraintSatisfactionProblem problem) {
		if(!problem.isLinear())
			throw new IllegalArgumentException("The solver \"lpsolve\" needs linear optimization problems.");
		//check existence of lp_solve first
		if(!LpSolve.checkBinary())
			return null;
		String output = new String();
		//String error = "";
		try{
			File lpFile = File.createTempFile("lptmp", null, LpSolve.tmpFolder);
//			File lpFile = new File("lptmp2");
			// Delete temp file when program exits.
			lpFile.deleteOnExit();    
			// Write to temp file
			BufferedWriter out = new BufferedWriter(new FileWriter(lpFile));
			out.write(((OptimizationProblem)problem).convertToLpFormat());
			out.close();		
			//execute lp_solve on problem in lp format and retrieve console output					
			Process child = Runtime.getRuntime().exec(LpSolve.binary+ " " + lpFile.getAbsolutePath());
			int c;				
	        InputStream in = child.getInputStream();
	        while ((c = in.read()) != -1)
	            output += ((char)c);
	        in.close();		        		        
	        in = child.getErrorStream();
	      //  while ((c = in.read()) != -1)
	      //      error += (char)c;
	        in.close();
	        lpFile.delete();
		}catch(IOException e){
			//TODO add error handling
			e.printStackTrace();
			return null;
		}		
		//parse output		
		String delimiter = "Actual values of the variables:";
		String assignments = output.substring(output.indexOf(delimiter)+delimiter.length(), output.length());
		StringTokenizer tokenizer = new StringTokenizer(assignments,"\n");
		Set<Variable> variables = problem.getVariables();
		Map<Variable,Term> result = new HashMap<Variable,Term>();
		while(tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken();
			StringTokenizer subTokenizer = new StringTokenizer(token," ");
			String varName = subTokenizer.nextToken().trim();
			String varValue = subTokenizer.nextToken().trim();
			Variable v;
			Constant c;
			try{
				c = new IntegerConstant(Integer.valueOf(varValue));
				v = new IntegerVariable(varName);				
			}catch(NumberFormatException e){
				c = new FloatConstant(Float.valueOf(varValue));
				v = new FloatVariable(varName);
			}
			if(!variables.contains(v)){
				// Check on: v might be a float variable that has been assigned an integer value
				v = new FloatVariable(v.getName());
				if(!variables.contains(v))
					throw new IllegalStateException("Something is terribly wrong: optimization problem contains variables it is not supposed to contain.");
			}				
			variables.remove(v);
			result.put(v, c);			
		}
		if(!variables.isEmpty())	
			throw new IllegalStateException("Not every variable has been assigned a value. This shouldn't happen.");
		return result;
	}

	/**
	 * Sets the path to the binary.
	 * @param binary the path to the binary.
	 */
	public static void setBinary(String binary){
		LpSolve.binary = binary;
	}
	
	/**
	 * Sets the path for the temporary folder.
	 * @param path some path.
	 */
	public static void setTmpFolder(File path){
		LpSolve.tmpFolder = path;
	}
}
