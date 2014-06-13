/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.math.opt.solver;

import java.io.*;
import java.util.*;

import net.sf.tweety.commons.util.Exec;
import net.sf.tweety.math.opt.*;
import net.sf.tweety.math.term.*;


/**
 * This class implements a wrapper to the lpsolve binary
 * for mixed integer  linear programming. See http://lpsolve.sourceforge.net.
 * @author Matthias Thimm
 */
public class LpSolve extends Solver {
	
	/**Path to the binary or lp_solve*/
	private static String binary = "lp_solve";
	
	/** For temporary files. */
	private static File tmpFolder = null;
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#isInstalled()
	 */
	public static boolean isInstalled() throws UnsupportedOperationException{
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
		if(!LpSolve.isInstalled())
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
