package net.sf.tweety.logics.pcl.analysis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.InconsistencyMeasure;
import net.sf.tweety.logics.pcl.PclBeliefSet;
import net.sf.tweety.logics.pcl.syntax.ProbabilisticConditional;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.equation.Equation;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.solver.OpenOptSolver;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class models an approximation from below to the distance minimization inconsistency measure as proposed in [Thimm,UAI,2009], see [PhD thesis, Thimm].
 * 
 * @author Matthias Thimm
 */
public class LowerApproxDistanceMinimizationInconsistencyMeasure implements InconsistencyMeasure<PclBeliefSet> {

	/**
	 * Logger.
	 */
	static private Logger log = LoggerFactory.getLogger(DistanceMinimizationInconsistencyMeasure.class);
	
	/**
	 * For archiving.
	 */
	private Map<PclBeliefSet,Double> archive = new HashMap<PclBeliefSet,Double>();
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.probabilisticconditionallogic.analysis.InconsistencyMeasure#inconsistencyMeasure(net.sf.tweety.logics.probabilisticconditionallogic.PclBeliefSet)
	 */
	@Override
	public Double inconsistencyMeasure(PclBeliefSet beliefSet) {
		log.trace("Starting to compute minimal distance inconsistency measure for '" + beliefSet + "'.");
		// check archive
		if(this.archive.containsKey(beliefSet))
			return this.archive.get(beliefSet);
		// first check whether the belief set is consistent		
		log.trace("Checking whether '" + beliefSet + "' is inconsistent.");
		if(beliefSet.size() == 0 || new PclDefaultConsistencyTester().isConsistent(beliefSet)){
			// update archive
			this.archive.put(beliefSet, new Double(0));
			return new Double(0);
		}
		log.trace("'" + beliefSet + "' is inconsistent, preparing optimization problem for computing the measure.");
		// Create variables for the probability of each possible world and
		// set up the optimization problem for computing the minimal
		// distance to a consistent belief set.
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
		// For each conditional add a variables mu and nu and
		// add constraints implied by the conditionals
		Map<ProbabilisticConditional,Variable> mus = new HashMap<ProbabilisticConditional,Variable>();
		Map<ProbabilisticConditional,Variable> nus = new HashMap<ProbabilisticConditional,Variable>();
		Term targetFunction = null;
		i = 0;		
		for(ProbabilisticConditional c: beliefSet){
			FloatVariable mu = new FloatVariable("m" + i,0,1);
			FloatVariable nu = new FloatVariable("n" + i++,0,1);
			mus.put(c, mu);
			nus.put(c, nu);
			if(targetFunction == null)
				targetFunction = mu.add(nu);
			else targetFunction = targetFunction.add(mu.add(nu));
			Term leftSide = null;
			Term rightSide = null;
			if(c.isFact()){
				for(PossibleWorld w: worlds)
					if(w.satisfies(c.getConclusion())){
						if(leftSide == null)
							leftSide = worlds2vars.get(w);
						else leftSide = leftSide.add(worlds2vars.get(w));
					}
				rightSide = new FloatConstant(c.getProbability().getValue()).add(mu).minus(nu);
			}else{				
				PropositionalFormula body = c.getPremise().iterator().next();
				PropositionalFormula head_and_body = c.getConclusion().combineWithAnd(body);
				for(PossibleWorld w: worlds){
					if(w.satisfies(head_and_body)){
						if(leftSide == null)
							leftSide = worlds2vars.get(w);
						else leftSide = leftSide.add(worlds2vars.get(w));
					}
					if(w.satisfies(body)){
						if(rightSide == null)
							rightSide = worlds2vars.get(w);
						else rightSide = rightSide.add(worlds2vars.get(w));
					}					
				}
				if(rightSide == null)
					rightSide = new FloatConstant(0);
				else{
					rightSide = rightSide.mult(new FloatConstant(c.getProbability().getValue()));
					rightSide = rightSide.add(mu).minus(nu);
				}
			}
			if(leftSide == null)
				leftSide = new FloatConstant(0);
			if(rightSide == null)
				rightSide = new FloatConstant(0);
			problem.add(new Equation(leftSide,rightSide));			
		}
		problem.setTargetFunction(targetFunction);
		try{			
			//TODO use lp_solve
			OpenOptSolver solver = new OpenOptSolver(problem);
			solver.contol = 1e-3;
			solver.gtol = 1e-60;
			solver.ftol = 1e-60;
			solver.xtol = 1e-60;
			//solver.ignoreNotFeasibleError = true;
			Map<Variable,Term> solution = solver.solve();
			return problem.getTargetFunction().replaceAllTerms(solution).doubleValue();
		}catch (GeneralMathException e){
			// This should not happen as the optimization problem is guaranteed to be feasible
			throw new RuntimeException("Fatal error: Optimization problem to compute the minimal distance to a consistent knowledge base is not feasible.");
		}		
	}
}
