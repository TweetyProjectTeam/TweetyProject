/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.math.opt.solver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.tweetyproject.commons.Parser;
import org.tweetyproject.commons.util.NativeShell;
import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.opt.problem.GeneralConstraintSatisfactionProblem;
import org.tweetyproject.math.opt.problem.ConstraintSatisfactionProblem;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;

/**
 * Provides a Java binding to the Glpk solver (<a href="https://www.gnu.org/software/glpk">https://www.gnu.org/software/glpk</a>).
 * @author Matthias Thimm
 *
 */
public class GlpkSolver extends Solver {

	/**Path to the binary or lp_solve*/
	public static String binary = "glpsol";
	
	/** For temporary files. */
	private static File tmpFolder = null;
	
	/**
	 * 
	 * @return if solver is installed
	 * @throws UnsupportedOperationException UnsupportedOperationException
	 */
	public static boolean isInstalled() throws UnsupportedOperationException{
		try {
			NativeShell.invokeExecutable(GlpkSolver.binary + " -h");
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.opt.Solver#solve(org.tweetyproject.math.opt.ConstraintSatisfactionProblem)
	 */
	@Override
	public Map<Variable, Term> solve(GeneralConstraintSatisfactionProblem problem)	throws GeneralMathException {
		if(!((ConstraintSatisfactionProblem) problem).isLinear())
			throw new IllegalArgumentException("The solver \"glpk\" needs linear optimization problems.");
		//check existence of lp_solve first
		if(!GlpkSolver.isInstalled())
			throw new RuntimeException("The solver \"glpk\" seems not to be installed.");
		String output = new String();
		//String error = "";
		try{
			File outputFile = File.createTempFile("lpoutput", null, GlpkSolver.tmpFolder);
			File lpFile = File.createTempFile("lptmp", null, GlpkSolver.tmpFolder);
			// Delete temp file when program exits.
			lpFile.deleteOnExit();
			outputFile.deleteOnExit();   
			// Write to temp file
			BufferedWriter out = new BufferedWriter(new FileWriter(lpFile));
			out.write(((OptimizationProblem)problem).convertToCplexLpFormat());
			//System.out.println(((OptimizationProblem)problem).convertToCplexLpFormat());
			out.close();		
			//execute lp_solve on problem in lp format and retrieve console output					
			output = NativeShell.invokeExecutable(GlpkSolver.binary + " --lp " + lpFile.getAbsolutePath() + " -o " + outputFile.getAbsolutePath());
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
			for(Variable v: ((ConstraintSatisfactionProblem) problem).getVariables()){
				idx = outputFromFile.indexOf(" " + v.getName() +  " ");
				tokens = outputFromFile.substring(idx, outputFromFile.indexOf("\n", idx)).split("\\s");
				for(i = 0; i < tokens.length && !Parser.isNumeric(tokens[i]); i++){ ; }
				result.put(v, new FloatConstant(Double.parseDouble(tokens[i])));
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
