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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.tweety.commons.util.VectorTools;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.opt.ConstraintSatisfactionProblem;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.Solver;

import org.riso.numerical.*;

import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements a wrapper for L-BFGS.
 * 
 * @author Matthias Thimm
 */
public class LbfgsSolver extends Solver {
	
	/**
	 * Logger.
	 */
	private Logger log = LoggerFactory.getLogger(LbfgsSolver.class);
	
	/**
	 * The starting point for the solver.
	 */
	private Map<Variable,Term> startingPoint;
	
	public LbfgsSolver(Map<Variable,Term> startingPoint) {
		this.startingPoint = startingPoint;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#solve()
	 */
	@Override
	public Map<Variable, Term> solve(ConstraintSatisfactionProblem problem) throws GeneralMathException {
		if(problem.size() > 0)
			throw new IllegalArgumentException("The gradient descent method works only for optimization problems without constraints.");
		
		this.log.trace("Solving the following optimization problem using L-BFGS:\n===BEGIN===\n" + problem + "\n===END===");
		Term func = ((OptimizationProblem)problem).getTargetFunction();
		if(((OptimizationProblem)problem).getType() == OptimizationProblem.MAXIMIZE)
			func = new IntegerConstant(-1).mult(func);	
		// variables need to be ordered
		List<Variable> variables = new ArrayList<Variable>(func.getVariables());
		List<Term> gradient = new LinkedList<Term>();		
		for(Variable v: variables)
			gradient.add(func.derive(v).simplify());
		Map<Variable,Term> currentGuess = this.startingPoint;
		// set parameters for L-BFGS
		int n = variables.size();
		int m = 1000;		
		double[] x = new double[n];
		for(int i = 0; i < n; i++)
			x[i] = currentGuess.get(variables.get(i)).doubleValue();
		double f = func.replaceAllTerms(currentGuess).doubleValue();
		double[] g = new double[n];
		for(int i = 0; i < n; i++)
			g[i] = gradient.get(i).replaceAllTerms(currentGuess).doubleValue();
		boolean diagco = false;
		double[] diag = new double[n];
		int[] iprint = new int[2];
		iprint[0] = -1;
		iprint[1] = 3;
		double eps = 0.00001;
		double xtol = 10e-16;
		int[] iflag = new int[1];
		iflag[0] = 0;
		this.log.trace("Starting optimization.");
		while(iflag[0] >= 0){
			try{
				new LBFGS().lbfgs(n, m, x, f, g, diagco, diag, iprint, eps, xtol, iflag);
				this.log.trace("Current manhattan distance of gradient to zero: " + VectorTools.manhattanDistanceToZero(g));
			}catch(Exception e){
				throw new GeneralMathException("Call to L-BFGS failed.");
			}
			if(iflag[0] == 0){
				break;
			}else if(iflag[0] == 1){				
				int i = 0;
				for(Variable v: variables){
					/*
					// if the variable should be positive, make some corrections
					// NOTE: this is a workaround.
					if(v.isPositive() && x[i]<0){
						currentGuess.put(v, new FloatConstant(-x[i]/2));
						x[i] = -x[i]/2;
						// restart optimization
						iflag[0] = 0;						
					}else*/ 
					currentGuess.put(v, new FloatConstant(x[i]));
					i++;
				}
				f = func.replaceAllTerms(currentGuess).doubleValue();
				for(i = 0; i < n; i++)
					g[i] = gradient.get(i).replaceAllTerms(currentGuess).doubleValue();					
			}
		}
		this.log.trace("Optimum found: " + currentGuess);
		return currentGuess;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#isInstalled()
	 */
	public static boolean isInstalled() throws UnsupportedOperationException{
		// as Lbgfs is included as a Maven dependency, this solver
		// is always installed
		return true;
	}
}
