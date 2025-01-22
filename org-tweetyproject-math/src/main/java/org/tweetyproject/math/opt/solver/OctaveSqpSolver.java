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
import java.util.StringTokenizer;

import org.tweetyproject.commons.util.NativeShell;
import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.equation.Equation;
import org.tweetyproject.math.equation.Inequation;
import org.tweetyproject.math.equation.Statement;
import org.tweetyproject.math.opt.problem.GeneralConstraintSatisfactionProblem;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.FloatVariable;
import org.tweetyproject.math.term.OptProbElement;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;

/**
 * Provides a bridge to the Octave (http://www.gnu.org/software/octave/) optimization
 * solver "sqp" which implements a successive quadratic programming solver for
 * general non-linear optimization problems.
 *
 * Version used is 5.2.0
 * @author Matthias Thimm
 *
 */
public class OctaveSqpSolver extends Solver{

	/** Path to Octave */
	private static String pathToOctave = "octave";

	/**
	 * Default constructor. If "octave" is not in the PATH,
	 * it must be set using the static method "setPathToOctave"
	 */
	public OctaveSqpSolver(){
	}

	/**
	 *  Set path
	 * @param pathToOctave path
	 */
	public static void setPathToOctave(String pathToOctave){
		OctaveSqpSolver.pathToOctave = pathToOctave;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.opt.Solver#solve(org.tweetyproject.math.opt.ConstraintSatisfactionProblem)
	 */
	@Override
	public Map<Variable, Term> solve(GeneralConstraintSatisfactionProblem problem) throws GeneralMathException {
		// only optimization problems
		if(!(problem instanceof OptimizationProblem))
			throw new IllegalArgumentException("Only optimization problems allowed for this solver.");
		OptimizationProblem p = (OptimizationProblem) problem;
		// convert p to Octave SQP format
		// 1. create maps for variables
		Map<Integer,Variable> idx2var = new HashMap<Integer,Variable>();
		Map<Variable,Integer> var2idx = new HashMap<Variable,Integer>();
		Map<Variable,Variable> old2new = new HashMap<Variable,Variable>();
		int idx = 1;
		for(Variable v: p.getVariables()){
			idx2var.put(idx, v);
			var2idx.put(v, idx);
			Variable newVar = new FloatVariable("x(" + idx + ")", v.getLowerBound(), v.getUpperBound());
			old2new.put(v, newVar);
			idx++;
		}
		// 2. encode constraints
		String eqConstraints = "function eq = g(x)\neq=[";
		String ineqConstraints = "function ineq = h(x)\nineq=[";
		boolean hasEquations = false;
		boolean hasInequations = false;
		for(OptProbElement s: p){
			s = ((Statement) s).replaceAllTerms(old2new);
			if(s instanceof Equation){
				eqConstraints += ((Statement) s).getLeftTerm().toString() + " - (" + ((Statement) s).getRightTerm() + ");";
				hasEquations = true;
			}else{
				Inequation ineq = (Inequation) s;
				if(ineq.getType() == Inequation.GREATER_EQUAL)
					ineqConstraints += ((Statement) s).getLeftTerm().toString() + " - (" + ((Statement) s).getRightTerm() + ");";
				else if(ineq.getType() == Inequation.LESS_EQUAL)
					ineqConstraints += ((Statement) s).getRightTerm().toString() + " - (" + ((Statement) s).getLeftTerm() + ");";
				else throw new IllegalArgumentException("No strict inequalities allows for Octave SQP solver.");
				hasInequations = true;
			}
		}
		if(!hasEquations) eqConstraints += "0";
		if(!hasInequations) ineqConstraints += "0";
		eqConstraints += "];\nendfunction\n";
		ineqConstraints += "];\nendfunction\n";
		String octaveCode = eqConstraints + ineqConstraints;
		// 3. encode target function
		octaveCode += "function obj = phi(x)\n";
		if(p.getType() == OptimizationProblem.MAXIMIZE)
			octaveCode += "obj = -" + p.getTargetFunction().replaceAllTerms(old2new) + "\n";
		else octaveCode += "obj = " + p.getTargetFunction().replaceAllTerms(old2new) + "\n";
		octaveCode += "endfunction\n";
		// TODO dirty trick to fix logarithm issues in MaxEnt optimization
		for(int i = 1; i <= idx2var.keySet().size();i++)
			octaveCode = octaveCode.replaceAll("x\\(" + i + "\\) \\* log\\(x\\(" + i + "\\)\\)", "i(x(" + i + "))");
		octaveCode = "function i1 = i(y)\n" +
				"if(y <= 0) i1 = 0\n" +
				"else i1 = y*log(y)\n" +
				"endif\n" +
				"endfunction\n" + octaveCode;
		// 4. encode starting point (just use the zero vector for now)
		octaveCode += "x0 = [";
		boolean isFirst = true;
		for(int i = 1; i <= idx2var.keySet().size(); i++ )
			if(isFirst){
				octaveCode += "0.0";
				isFirst = false;
			}else octaveCode += ";0.0";
		octaveCode += "]\n";
		// 5. encode box constraints
		octaveCode += "lb = [";
		isFirst = true;
		for(int i = 1; i <= idx2var.keySet().size(); i++ )
			if(isFirst){
				octaveCode += idx2var.get(i).getLowerBound();
				isFirst = false;
			}else octaveCode += ";" + idx2var.get(i).getLowerBound();
		octaveCode += "]\n";
		octaveCode += "ub = [";
		isFirst = true;
		for(int i = 1; i <= idx2var.keySet().size(); i++ )
			if(isFirst){
				octaveCode += idx2var.get(i).getUpperBound();
				isFirst = false;
			}else octaveCode += ";" + idx2var.get(i).getUpperBound();
		octaveCode += "]\n";
		// 6. encode sqp call
		octaveCode += "[x, obj, info, iter, nf, lambda] = sqp (x0, @phi, @g, @h, lb, ub)";
		// write code to temp file and execute octave
		try {
			File ocFile = File.createTempFile("octmp", null);
			// Delete temp file when program exits.
			ocFile.deleteOnExit();
			// Write to temp file
			BufferedWriter out = new BufferedWriter(new FileWriter(ocFile));
			out.write(octaveCode);
			out.close();
			String result = NativeShell.invokeExecutable(OctaveSqpSolver.pathToOctave + " " + ocFile.getAbsolutePath());
			// parse result
			Map<Variable, Term> varMap = new HashMap<Variable,Term>();
			String sub = result.substring(result.indexOf("x =")+3);
			sub = sub.trim();
			StringTokenizer tokenizer = new StringTokenizer(sub,"\n");
			for(int i = 1; i <= idx2var.keySet().size(); i++ ){
				FloatConstant f = new FloatConstant(Double.parseDouble((tokenizer.nextToken().trim())));
				varMap.put(idx2var.get(i), f);
			}
			return varMap;
		} catch (IOException e) {
			throw new GeneralMathException("IO error: " + e.getMessage());
		} catch (InterruptedException e) {
			throw new GeneralMathException("Could not call executable 'octave': " + e.getMessage());
		}
	}

	/**
	 * Return if solver is installed
	 *
	 * @return if solver is installed
	 * @throws UnsupportedOperationException UnsupportedOperationException
	 */
	public static boolean isInstalled() throws UnsupportedOperationException{
		try {
			NativeShell.invokeExecutable(OctaveSqpSolver.pathToOctave + " -h");
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
