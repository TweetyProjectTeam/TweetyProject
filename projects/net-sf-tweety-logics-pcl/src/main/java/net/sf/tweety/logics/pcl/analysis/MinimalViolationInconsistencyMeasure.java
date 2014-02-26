package net.sf.tweety.logics.pcl.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pcl.PclBeliefSet;
import net.sf.tweety.logics.pcl.semantics.ProbabilityDistribution;
import net.sf.tweety.logics.pcl.syntax.ProbabilisticConditional;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.equation.Equation;
import net.sf.tweety.math.norm.RealVectorNorm;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.solver.OpenOptSolver;
import net.sf.tweety.math.opt.solver.OpenOptWebSolver;
import net.sf.tweety.math.probability.Probability;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * This class provides a general implementation for the minimal violation inconsistency measure,
 * cf. [Potyka, 2014]. It accepts any real vector norm but may be slow in practice and is numerically not optimal.
 * @author Matthias Thimm
 */
public class MinimalViolationInconsistencyMeasure extends BeliefSetInconsistencyMeasure<ProbabilisticConditional,PclBeliefSet> {

	/** The norm. */
	private RealVectorNorm norm;
	
	/**
	 * Creates a new measure the given norm
	 * @param norm some norm.
	 */
	public MinimalViolationInconsistencyMeasure(RealVectorNorm norm){
		this.norm = norm;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<ProbabilisticConditional> formulas) {
		PclBeliefSet beliefSet = new PclBeliefSet(formulas);
		// Create variables for the probability of each possible world and
		// set up the optimization problem for computing the minimal violation.
		OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MINIMIZE);
		Set<PossibleWorld> worlds = PossibleWorld.getAllPossibleWorlds((PropositionalSignature)beliefSet.getSignature());
		Map<PossibleWorld,Variable> worlds2vars = new HashMap<PossibleWorld,Variable>();
		int i = 0;
		Term normConstraint = null;
		for(PossibleWorld w: worlds){
			FloatVariable var = new FloatVariable("w" + i++,0,1);
			worlds2vars.put(w, var);
			if(normConstraint == null)
				normConstraint = var;
			else normConstraint = normConstraint.add(var);
		}		
		problem.add(new Equation(normConstraint, new IntegerConstant(1)));
		// For each conditional (B_i|A_i)[p_i], create a variable d_i and
		// add a constraint P(A_i B_i) - p_i P(A_i) = d_i		
		Map<ProbabilisticConditional,Variable> vio = new HashMap<ProbabilisticConditional,Variable>();		
		i = 0;		
		for(ProbabilisticConditional c: beliefSet){
			FloatVariable v = new FloatVariable("v" + i,-1,1);
			vio.put(c, v);
			Term leftSide = null;
			for(PossibleWorld w: worlds){
				if(w.satisfies(c.getPremise())){
					if(leftSide == null)
						leftSide = new FloatConstant(-c.getProbability().doubleValue()).mult(worlds2vars.get(w));
					else leftSide = leftSide.minus(new FloatConstant(c.getProbability().doubleValue()).mult(worlds2vars.get(w)));
					if(w.satisfies(c.getConclusion()))
						leftSide = leftSide.add(worlds2vars.get(w));
				}
			}
			problem.add(new Equation(leftSide, v));
			i++;
		}
		// set up the target function which is the norm of the d_i variables
		Term targetFunction = this.norm.normTerm(vio.values().toArray(new Term[0]));
		problem.setTargetFunction(targetFunction);
		try{			
			OpenOptSolver solver = new OpenOptWebSolver(problem);
			solver.solver = "ralg";
			solver.contol = 1e-4;			
			solver.ignoreNotFeasibleError = true;
			//System.out.println(solver.getOpenOptCode());
			Map<Variable,Term> solution = solver.solve();
			// prepare probability function
			ProbabilityDistribution<PossibleWorld> p = new ProbabilityDistribution<PossibleWorld>(beliefSet.getSignature());
			for(PossibleWorld world: worlds)
				p.put(world, new Probability(solution.get(worlds2vars.get(world)).doubleValue()));
			return targetFunction.replaceAllTerms(solution).doubleValue();			
		}catch (GeneralMathException e){
			// This should not happen as the optimization problem is guaranteed to be feasible
			throw new RuntimeException("Fatal error: Optimization problem to compute the minimal distance to a consistent knowledge base is not feasible.");
		}
	}
}
