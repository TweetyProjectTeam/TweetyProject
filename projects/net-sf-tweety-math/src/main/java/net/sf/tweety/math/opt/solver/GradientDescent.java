package net.sf.tweety.math.opt.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.tweety.commons.util.VectorTools;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.opt.ConstraintSatisfactionProblem;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.Solver;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements the gradient descent method to 
 * find an optimum.
 * 
 * @author Matthias Thimm
 *
 */
public class GradientDescent implements Solver {
	
	/**
	 * Logger.
	 */
	private Logger log = LoggerFactory.getLogger(GradientDescent.class);
	
	/**
	 * The precision of the approximation.
	 * The actual used precision depends on the number of variables. 
	 */
	public double precision = 0.00001;
	
	/**
	 * The max step length for the gradient descent.
	 */
	private static final double MAX_STEP_LENGTH = 0.01;
	
	/**
	 * The min step length for the gradient descent.
	 */
	private static final double MIN_STEP_LENGTH = 0.0000000000000000000001;
	
	/**
	 * The starting point for the solver.
	 */
	private Map<Variable,Term> startingPoint;
	
	/**
	 * Creates a new gradient descent solver
	 */
	public GradientDescent(Map<Variable,Term> startingPoint) {
		this.startingPoint = startingPoint;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#solve()
	 */
	@Override
	public Map<Variable, Term> solve(ConstraintSatisfactionProblem problem) throws GeneralMathException {
		if(problem.size() > 0)
			throw new IllegalArgumentException("The gradient descent method works only for optimization problems without constraints.");
		this.log.trace("Solving the following optimization problem using gradient descent:\n===BEGIN===\n" + problem + "\n===END===");
		Term f = ((OptimizationProblem)problem).getTargetFunction();
		if(((OptimizationProblem)problem).getType() == OptimizationProblem.MAXIMIZE)
			f = new IntegerConstant(-1).mult(f);	
		// variables need to be ordered
		List<Variable> variables = new ArrayList<Variable>(f.getVariables());
		List<Term> gradient = new LinkedList<Term>();		
		for(Variable v: variables)
			gradient.add(f.derive(v).simplify());
		Map<Variable,Term> currentGuess = startingPoint;
		Map<Variable,Term> newGuess = new HashMap<Variable,Term>();
		List<Double> currentGradient = Term.evaluateVector(gradient, currentGuess);
		List<Double> newGradient; 
		double actualPrecision = this.precision * variables.size();
		int idx;
		double step,val;
		this.log.trace("Starting optimization.");
		do{
			// find the best step length
			step = GradientDescent.MAX_STEP_LENGTH;			
			while(true){
				idx = 0;
				for(Variable v: variables){
					val = currentGuess.get(v).doubleValue()-(step * currentGradient.get(idx++));
					if(v.isPositive())
						if(val < 0)
							val = currentGuess.get(v).doubleValue() * step;
					newGuess.put(v, new FloatConstant(val));
				}
				newGradient = Term.evaluateVector(gradient, newGuess);
				if(f.replaceAllTerms(currentGuess).doubleValue() <= f.replaceAllTerms(newGuess).doubleValue()){
					step /= 2;
				}else{				
					currentGradient = newGradient;
					currentGuess.putAll(newGuess);
					break;
				}
				if(step < GradientDescent.MIN_STEP_LENGTH)
					throw new GeneralMathException();
			}			
			this.log.trace("Current manhattan distance of gradient to zero: " + VectorTools.manhattanDistanceToZero(currentGradient));
		}while(VectorTools.manhattanDistanceToZero(currentGradient) > actualPrecision);
		this.log.trace("Optimum found: " + currentGuess);
		return currentGuess;
	}


}
