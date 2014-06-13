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
package net.sf.tweety.arg.prob.dynamics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.prob.PartialProbabilityAssignment;
import net.sf.tweety.arg.prob.semantics.PASemantics;
import net.sf.tweety.arg.prob.semantics.ProbabilisticExtension;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.func.SimpleRealValuedFunction;
import net.sf.tweety.math.norm.RealVectorNorm;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.Solver;
import net.sf.tweety.math.probability.Probability;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * This operator implements an update of some probabilistic assessment of 
 * arguments upon the observation of an argumentation theory. More specifically, for
 * a given probabilistic semantics S, some norm N, a function f, and a partial probability assignment PPA,
 * it computes the S-prob-function that 1.) has minimal N-distance to PPA-compliant prob'functions and 2.) maximizes
 * function f.  
 * @author Matthias Thimm
 */
public class PAUpdateOperator extends AbstractPAChangeOperator {
	
	/**
	 * Creates a new change operator for the given semantics that uses the specified norm
	 * for distance measuring and the given function for optimizing.
	 * @param semantics the semantics used for change.
	 * @param norm the norm used for distance measurement between probabilistic extensions.
	 * @param f the function that is maximized on the set of probabilistic extensions with minimal distance. 
	 */
	public PAUpdateOperator(PASemantics semantics, RealVectorNorm norm,	SimpleRealValuedFunction f) {
		super(semantics, norm, f);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.prob.dynamics.ChangeOperator#change(net.sf.tweety.arg.prob.PartialProbabilityAssignment, net.sf.tweety.arg.dung.DungTheory)
	 */
	@Override
	public ProbabilisticExtension change(PartialProbabilityAssignment ppa, DungTheory theory) {
		// construct optimization problem
		// Note that we have in fact two optimization problems to solve:
		// 1.) first we determine the set of S-prob'functions with minimal N-distance to the PPA-compliant prob'functions
		// 2.) from this set we take a S-prob'function which maximizes f
		// Instead of solving two consecutive optimization problems we just solve one by combining the target functions and
		// weighing the first one by a large factor (therefore imposing a preference of the first optimization problem)
		OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MAXIMIZE);
		// for pi-compliant prob'functions
		Map<Collection<Argument>,FloatVariable> varsComp = new HashMap<Collection<Argument>,FloatVariable>();
		Vector<Term> varsCompVector = new Vector<Term>();
		// for semantically satisfying prob'functions
		Map<Collection<Argument>,FloatVariable> varsSem = new HashMap<Collection<Argument>,FloatVariable>();
		Vector<Term> varsSemVector = new Vector<Term>();
		// prepare optimization problem
		this.prepareOptimizationProblem(ppa, theory, problem, varsComp, varsSem, varsCompVector, varsSemVector);
		// Target function
		// the part that is to be maximized (we maximize over the set of S-functions)
		Term max = this.getFunction().getTerm(varsSemVector);
		// the part that is to be minimized (it is weighted as it is preferred) 
		Term min = new FloatConstant(AbstractPAChangeOperator.FIRST_OPTIMIZATION_WEIGHT).mult(this.getNorm().distanceTerm(varsCompVector,varsSemVector));		
		problem.setTargetFunction(max.minus(min));				
		// Do the optimization
		try{						
			Map<Variable,Term> solution = Solver.getDefaultGeneralSolver().solve(problem);
			ProbabilisticExtension ext = new ProbabilisticExtension();
			// select the best S-prob'function
			for(Collection<Argument> args: varsSem.keySet())
				ext.put(new Extension(args), new Probability(solution.get(varsSem.get(args)).doubleValue()));
			return ext;
		}catch (GeneralMathException ex){
			// This should not happen as the optimization problem is guaranteed to be feasible
			throw new RuntimeException("Fatal error: Optimization problem to compute the closest and best probabilistic extension is not feasible.");
		}
	}
}
