package net.sf.tweety.math.opt.solver;

//import java.io.*;
import java.util.*;

import net.sf.tweety.math.opt.*;
import net.sf.tweety.math.term.*;


/**
 * This class implements a wrapper to the glpk library
 * for general linear programming. See http://www.gnu.org/software/glpk/. 
 * @author Matthias Thimm
 *
 */
public class Glpk extends Solver{
	
	/**
	 * Creates a new solver for the given optimization problem.
	 * @param problem an optimization problem.
	 */
	public Glpk(OptimizationProblem problem){
		super(problem);
		if(!problem.isLinear())
			throw new IllegalArgumentException("The solver \"glpk\" needs linear optimization problems.");
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#solve()
	 */
	@Override
	public Map<Variable, Term> solve() {
		/*
		String output = new String();
		String error = "";
		try{
			File glpkFile = File.createTempFile("glpktmp", null);
			// Delete temp file when program exits.
			glpkFile.deleteOnExit();    
			// Write to temp file
			BufferedWriter out = new BufferedWriter(new FileWriter(glpkFile));
			out.write(((OptimizationProblem)this.getProblem()).convertToCplexLpFormat());
			out.close();		
			//execute glpsol on problem in cplex lp format and retrieve console output					
			Process child = Runtime.getRuntime().exec("glpsol --cpxlp " + glpkFile.getAbsolutePath());
			int c;				
	        InputStream in = child.getInputStream();
	        while ((c = in.read()) != -1)
	            output += ((char)c);
	        in.close();		        		        
	        in = child.getErrorStream();
	        while ((c = in.read()) != -1)
	            error += (char)c;
	        in.close();	        
		}catch(IOException e){
			//TODO add error handling
			e.printStackTrace();
			return null;
		}	
		//parse output
		 * 
		 */
		//TODO IMPLEMENT ME
		return null;
	}
	
}
