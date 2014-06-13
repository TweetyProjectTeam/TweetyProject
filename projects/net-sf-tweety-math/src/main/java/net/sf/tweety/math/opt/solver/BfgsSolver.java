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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.matrix.Matrix;
import net.sf.tweety.math.opt.ConstraintSatisfactionProblem;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.Solver;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements the BFGS algorithm for solving unconstrained optimization problems.
 * @author Matthias Thimm
 *
 */
public class BfgsSolver extends Solver {

	/**
	 * Logger.
	 */
	private Logger log = LoggerFactory.getLogger(BfgsSolver.class);
	
	private static final double PRECISION = 0.000000000000000001;
	
	/**
	 * The starting point for the solver.
	 */
	private Map<Variable,Term> startingPoint;
	
	public BfgsSolver(Map<Variable,Term> startingPoint) {
		this.startingPoint = startingPoint;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#solve()
	 */
	@Override
	public Map<Variable, Term> solve(ConstraintSatisfactionProblem problem) throws GeneralMathException {
		if(problem.size() > 0)
			throw new IllegalArgumentException("The gradient descent method works only for optimization problems without constraints.");
		Term func = ((OptimizationProblem)problem).getTargetFunction();
		if(((OptimizationProblem)problem).getType() == OptimizationProblem.MAXIMIZE)
			func = new IntegerConstant(-1).mult(func);	
		// variables need to be ordered
		List<Variable> variables = new ArrayList<Variable>(func.getVariables());
		Matrix gradient = new Matrix(1,variables.size());
		int idx = 0;
		for(Variable v: variables)
			gradient.setEntry(0, idx++, func.derive(v).simplify());			
		Matrix approxInverseHessian = Matrix.getIdentityMatrix(variables.size());
		Matrix currentGuess = new Matrix(1,variables.size());
		idx = 0;
		for(Variable v: variables)
			currentGuess.setEntry(0, idx++, this.startingPoint.get(v));
		Matrix searchDirection, evaluatedGradient, s = null, y, ssT, bysT, syTb;
		double sTy, yTby, distanceToZero, currentStep;
		double actualPrecision = BfgsSolver.PRECISION * variables.size(); 
		while(true){
			evaluatedGradient = this.evaluate(gradient, currentGuess, variables);
			distanceToZero = evaluatedGradient.distanceToZero();
			this.log.trace("Current manhattan distance of gradient to zero: " + distanceToZero);
			if(distanceToZero < actualPrecision)
				break;
			searchDirection = approxInverseHessian.mult(evaluatedGradient.mult(new IntegerConstant(-1))).simplify();			
			currentStep = this.nextBestStep(currentGuess, searchDirection, gradient, variables);
			// we don't find a better guess
			if(currentStep == -1)
				break;			
			s = searchDirection.mult(currentStep);
			currentGuess = currentGuess.add(s).simplify();	
			y = this.evaluate(gradient, currentGuess, variables).minus(evaluatedGradient).simplify();
			// perform Hessian update
			sTy = s.transpose().mult(y).getEntry(0, 0).doubleValue();
			ssT = s.mult(s.transpose());
			yTby = y.transpose().mult(approxInverseHessian.mult(y)).getEntry(0, 0).doubleValue();
			bysT = approxInverseHessian.mult(y.mult(s.transpose())).simplify();
			syTb = s.mult(y.transpose().mult(approxInverseHessian)).simplify();			
			approxInverseHessian = approxInverseHessian.add(ssT.mult((sTy+yTby)/(sTy*sTy))).minus(bysT.add(syTb).mult(1/sTy)).simplify();			
		}
		Map<Variable,Term> result = new HashMap<Variable,Term>();
		idx = 0;
		for(Variable v: variables)
			result.put(v, currentGuess.getEntry(0, idx++));
		return result;
	}
	
	private double nextBestStep(Matrix currentGuess, Matrix searchDirection, Matrix gradient, List<Variable> variables){
		double currentStep = 0.001;		
		Matrix s, newGuess, y;
		do{
			s = searchDirection.mult(currentStep);
			newGuess = currentGuess.add(s).simplify();
			y = this.evaluate(gradient, newGuess, variables);			
			if(y.isFinite())
				return currentStep;
			currentStep *= 9d/10d;	
			if(currentStep < BfgsSolver.PRECISION) return -1;
		}while(true);
	}
	
	private Matrix evaluate(Matrix gradient, Matrix currentGuess, List<Variable> variables){
		Matrix result = new Matrix(1,variables.size());
		for(int i = 0; i < gradient.getYDimension(); i++){
			Term t = gradient.getEntry(0, i);
			for(int j = 0; j < currentGuess.getYDimension(); j++)
				t = t.replaceTerm(variables.get(j), currentGuess.getEntry(0, j));
			result.setEntry(0, i, t.value());
		}
		return result;	
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#isInstalled()
	 */
	public static boolean isInstalled() throws UnsupportedOperationException{
		// as this is a native implementation it is always installed
		return true;
	}
}
