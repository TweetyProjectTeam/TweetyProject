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
package net.sf.tweety.math.opt.solver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.tweety.commons.util.NativeShell;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.equation.Equation;
import net.sf.tweety.math.equation.Inequation;
import net.sf.tweety.math.equation.Statement;
import net.sf.tweety.math.opt.ConstraintSatisfactionProblem;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.Solver;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;


/**
 * This class implements a wrapper for the OpenOpt optimization library.
 * @author Matthias Thimm
 *
 */
public class OpenOptSolver extends Solver {
	
	/**
	 * Logger.
	 */
	private Logger log = LoggerFactory.getLogger(OpenOptSolver.class);
	
	private double contol = 1e-8;
	private double ftol = 1e-8;
	private double gtol = 1e-8;
	private double xtol = 1e-8;
	private double maxIter = 1e16;
	private double maxFunEvals = 1e16;
	private String solver = "ralg";
	protected boolean ignoreNotFeasibleError = false;
	
	/**
	 * A starting point for the optimization.
	 */
	private Map<Variable,Term> startingPoint;
	
	/**
	 * A map mapping old variables to new variables.
	 */
	private Map<Variable,Variable> oldVars2newVars = new HashMap<Variable,Variable>();
	
	/**
	 * A map mapping old variables to new variables.
	 */
	private Map<Variable,Variable> newVars2oldVars = new HashMap<Variable,Variable>();
	
	/**
	 * A map mapping indices to new variables.
	 */
	private Map<Integer,Variable> idx2newVars = new HashMap<Integer,Variable>();
	
	/**
	 * Creates a new solver.
	 */
	public OpenOptSolver() {
	}
	
	/**
	 * Creates a new solver for the given problem.
	 * @param problem a csp.
	 * @param startingPoint a starting point.
	 */
	public OpenOptSolver(Map<Variable,Term> startingPoint) {
		this.startingPoint = startingPoint;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#solve()
	 */
	@Override
	public Map<Variable, Term> solve(ConstraintSatisfactionProblem problem) throws GeneralMathException {
		// do a renaming of variables		
		int idx = 0;
		for(Variable v: problem.getVariables()){
			Variable newV = new FloatVariable("x[" + idx + "]");
			this.oldVars2newVars.put(v, newV);
			this.newVars2oldVars.put(newV, v);
			this.idx2newVars.put(idx,newV);
			idx++;
		}
		//System.out.println(this.getOpenOptCode((OptimizationProblem)problem));System.exit(0);
		String output = "";
		//String error = "";
		InputStream in = null;
		Process child = null;
		try{
			File ooFile = File.createTempFile("ootmp", null);
			// Delete temp file when program exits.
			ooFile.deleteOnExit();    
			// Write to temp file
			BufferedWriter out = new BufferedWriter(new FileWriter(ooFile));
			this.log.info("Building Python code for OpenOpt.");
			out.write(this.getOpenOptCode((OptimizationProblem)problem));
			out.close();			
			//execute openopt on problem and retrieve console output
			this.log.info("Calling OpenOpt optimization library.");
			child = Runtime.getRuntime().exec("python " + ooFile.getAbsolutePath());
			int c;		
			in = child.getInputStream();
	        while ((c = in.read()) != -1){
	            output += ((char)c);
	        }
			in.close();		        		        
	        in = child.getErrorStream();
	      //  while ((c = in.read()) != -1)
	      //      error += (char)c;	        	        
		}catch(IOException e){
			log.error(e.getMessage());
			return null;
		}finally{
			try {
				if(in != null) in.close();
			} catch (IOException e) {
				// ignore
			}
			if(child != null) child.destroy();
		}
		// TODO check error appropriately
		if(output.contains("NO FEASIBLE SOLUTION") && !this.ignoreNotFeasibleError){
			this.log.info("The optimization problem seems to be unfeasible.");
			throw new GeneralMathException("The optimization problem seems to be unfeasible.");
		}
		// parser output
		this.log.info("Parsing solution from OpenOpt.");
		try{
			return this.parseOutput(output);
		}catch(Exception e){
			this.log.error(e.getMessage());
			throw new GeneralMathException(e.getMessage());
		}
	}

	/**
	 * Builds the OpenOpt code for the given problem which can be interpreted
	 * by a python.
	 * @return the python code for the given problem
	 */
	public String getOpenOptCode(OptimizationProblem problem){
		// replace vars
		problem.setTargetFunction(problem.getTargetFunction().replaceAllTerms(this.oldVars2newVars));
		// we have to minimize
		if(problem.getType() == OptimizationProblem.MAXIMIZE){
			problem.setTargetFunction(problem.getTargetFunction().mult(new IntegerConstant(-1)));
			problem.setType(OptimizationProblem.MINIMIZE);
		}			
		Set<Statement> constraints = new HashSet<Statement>(problem);
		problem.clear();
		for(Statement s: constraints)
			problem.add(s.replaceAllTerms(this.oldVars2newVars));		
		String code = new String();
		
		//write header
		code += "from FuncDesigner import *\n";
		code += "from openopt import NLP\n";
		code += "from numpy import *\n";
		code += "\n";	
		
		// add some auxiliary functions
		code+= "def log_mod(x):\n";
		code+= "	if x > 0.0:\n";
		code+= "		return log(x)\n";
		code+= "	else:\n";
		code+= "		return 0.0\n\n";
		
		//write objective (replace auxiliary functions)
		code += "objective = lambda x: ";
		
		code += problem.getTargetFunction().toString().replace("log", "log_mod") + "\n\n";
		// write startingpoint
		boolean first = true;
		if(this.startingPoint != null){
			code += "startingpoint = [";			
			for(int i = 0; i < this.idx2newVars.keySet().size(); i++)
				if(first){
					first = false;
					code += startingPoint.get(this.newVars2oldVars.get(this.idx2newVars.get(i))).doubleValue();
				}else code += "," + startingPoint.get(this.newVars2oldVars.get(this.idx2newVars.get(i))).doubleValue();
			code += "]\n\n";
		}
		// add box constraints
		code += "lb = [";
		first = true;
		for(int i = 0; i < this.idx2newVars.keySet().size(); i++)
			if(first){
				first = false;
				code += this.newVars2oldVars.get((this.idx2newVars.get(i))).getLowerBound();
			}else code += "," + this.newVars2oldVars.get((this.idx2newVars.get(i))).getLowerBound();
		code += "]\n";
		code += "ub = [";
		first = true;
		for(int i = 0; i < this.idx2newVars.keySet().size(); i++)
			if(first){
				first = false;
				code += this.newVars2oldVars.get((this.idx2newVars.get(i))).getUpperBound();
			}else code += "," + this.newVars2oldVars.get((this.idx2newVars.get(i))).getUpperBound();
		code += "]\n";
		// specify problem
		if(this.startingPoint != null)
			code += "p = NLP(objective,startingpoint,lb=lb,ub=ub)\n\n";
		else code += "p = NLP(objective,lb=lb,ub=ub)\n\n";
		// add constraints		
		int idx = 0;
		List<String> equalities = new ArrayList<String>();
		List<String> inequalities = new ArrayList<String>();
		for(Statement s: problem){
			if(s instanceof Equation){
				// add equality constraints
				Equation eq = (Equation)s.toNormalizedForm();
				equalities.add("c" + idx);
				code += "c" + idx + " = lambda x: " + eq.getLeftTerm().toString().replace("log", "log_mod") + "\n";				
			}else{
				Inequation ineq = (Inequation) s.toNormalizedForm();
				inequalities.add("i" + idx);
				code += "i" + idx + " = lambda x: " + new FloatConstant(-1).mult(ineq.getLeftTerm()).toString().replace("log", "log_mod") + "\n";
			}
			idx++;
		}
		if(!equalities.isEmpty())
			code += "\np.h = " + equalities + "\n\n";
		if(!inequalities.isEmpty())
			code += "\np.c = " + inequalities + "\n\n";
		// write commands			
		code += "p.contol = " + this.contol + "\n";
		code += "p.ftol = " + this.ftol + "\n";
		code += "p.gtol = " + this.gtol + "\n";
		code += "p.xtol = " + this.xtol + "\n";
		code += "p.maxIter = " + this.maxIter + "\n";
		code += "p.maxFunEvals = " + this.maxFunEvals + "\n";
		code += "r = p.solve('" + this.solver + "')\n";
		code += "print r.xf";
		this.log.trace("Generated the OpenOpt code:\n===BEGIN===\n" + code + "\n===END===");		
		return code;
	}
	
	/**
	 * This method parses the output data of an OpenOpt run
	 * @param output a string.
	 * @params length the length of the array to be parsed.
	 * @return a map from variable to terms
	 */
	protected Map<Variable,Term> parseOutput(String output){
		try{
			int valuesBegin = output.lastIndexOf("[");
			int valuesEnd = output.lastIndexOf("]");
			String values = output.substring(valuesBegin+1, valuesEnd);
			String[] tokens = values.split(" ");
			double[] r = new double[this.idx2newVars.keySet().size()];
			int i = 0;
			for(String token : tokens){
				if(token.trim().equals(""))
					continue;
				r[i] = new Double(token.trim());
				i++;
				if(i==this.idx2newVars.keySet().size()) break;
			}
			Map<Variable,Term> result = new HashMap<Variable,Term>();
			for(Integer j: this.idx2newVars.keySet())
				result.put(this.newVars2oldVars.get(this.idx2newVars.get(j)), new FloatConstant(r[j]));
			return result;
		}catch(Exception e){
			this.log.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#isInstalled()
	 */
	public static boolean isInstalled() throws UnsupportedOperationException{
		// first check whether Python is installed
		try {
			NativeShell.invokeExecutable("python -h");
		} catch (Exception e) {
			return false;
		}
		// then check whether OpenOpt is installed as a Python package
		// by running a small python script
		try {
			String scr = "try:\n"
					+ "\timport openopt\n"
					+ "\timport FuncDesigner\n"
					+ "\timport numpy\n"
					+ "\tprint \"yes\"\n"
					+ "except ImportError:\n"
					+ "\tprint \"no\"\n";
			File ooFile = File.createTempFile("ootmp", null);
			// Delete temp file when program exits.
			ooFile.deleteOnExit();    
			// Write to temp file
			BufferedWriter out = new BufferedWriter(new FileWriter(ooFile));
			out.write(scr);			
			out.close();
			String output = NativeShell.invokeExecutable("python " + ooFile.getAbsolutePath());
			ooFile.delete();
			return output.trim().equals("yes");
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException("Unable to create a temporary file for testing whether OpenOpt is installed.");
		}
	}
	
	public double getContol() {
		return contol;
	}

	public void setContol(double contol) {
		this.contol = contol;
	}

	public double getFtol() {
		return ftol;
	}

	public void setFtol(double ftol) {
		this.ftol = ftol;
	}

	public double getGtol() {
		return gtol;
	}

	public void setGtol(double gtol) {
		this.gtol = gtol;
	}

	public double getXtol() {
		return xtol;
	}

	public void setXtol(double xtol) {
		this.xtol = xtol;
	}

	public double getMaxIter() {
		return maxIter;
	}

	public void setMaxIter(double maxIter) {
		this.maxIter = maxIter;
	}

	public double getMaxFunEvals() {
		return maxFunEvals;
	}

	public void setMaxFunEvals(double maxFunEvals) {
		this.maxFunEvals = maxFunEvals;
	}

	public String getSolver() {
		return solver;
	}

	public void setSolver(String solver) {
		this.solver = solver;
	}

	public boolean isIgnoreNotFeasibleError() {
		return ignoreNotFeasibleError;
	}

	public void setIgnoreNotFeasibleError(boolean ignoreNotFeasibleError) {
		this.ignoreNotFeasibleError = ignoreNotFeasibleError;
	}
}
