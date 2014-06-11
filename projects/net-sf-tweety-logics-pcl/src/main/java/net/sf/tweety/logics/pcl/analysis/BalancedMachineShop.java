package net.sf.tweety.logics.pcl.analysis;

import java.util.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.commons.analysis.CulpabilityMeasure;
import net.sf.tweety.logics.pcl.*;
import net.sf.tweety.logics.pcl.syntax.*;
import net.sf.tweety.logics.pl.semantics.*;
import net.sf.tweety.logics.pl.syntax.*;
import net.sf.tweety.math.*;
import net.sf.tweety.math.equation.*;
import net.sf.tweety.math.opt.*;
import net.sf.tweety.math.opt.solver.*;
import net.sf.tweety.math.term.*;
import net.sf.tweety.math.probability.*;

/**
 * This class implements a consistency restorer using balanced distance minimization, see [Diss, Thimm] for details.
 * @author Matthias Thimm
 */
public class BalancedMachineShop implements BeliefBaseMachineShop {

	/** The precision for comparing culpability values. */
	public static final double PRECISIONCULP = 0.01;
	/** The precision for comparing culpability values. */
	public static final double PRECISIONOPT = 0.002;
	
	/** The culpability measure used by this machine shop. */
	private CulpabilityMeasure<ProbabilisticConditional,PclBeliefSet> culpabilityMeasure;
	
	public BalancedMachineShop(CulpabilityMeasure<ProbabilisticConditional,PclBeliefSet> culpabilityMeasure){
		this.culpabilityMeasure = culpabilityMeasure;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.BeliefBaseMachineShop#repair(net.sf.tweety.BeliefBase)
	 */
	@Override
	public BeliefBase repair(BeliefBase beliefBase) {
		if(!(beliefBase instanceof PclBeliefSet))
			throw new IllegalArgumentException("Belief base of type 'PclBeliefSet' expected.");
		PclBeliefSet beliefSet = (PclBeliefSet) beliefBase;
		PclDefaultConsistencyTester tester = new PclDefaultConsistencyTester();
		if(tester.isConsistent(beliefSet))
			return beliefSet;		
		// get culpability values
		Map<ProbabilisticConditional,Double> culpMeasures = new HashMap<ProbabilisticConditional,Double>();
		for(ProbabilisticConditional pc: beliefSet)
			culpMeasures.put(pc, this.culpabilityMeasure.culpabilityMeasure(beliefSet, pc));		
		// Do a distance minimization but incorporate conformity constraints
		// -----------
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
		// For each conditional add variables eta and tau and
		// add constraints implied by the conditionals
		Map<ProbabilisticConditional,Variable> etas = new HashMap<ProbabilisticConditional,Variable>();
		Map<ProbabilisticConditional,Variable> taus = new HashMap<ProbabilisticConditional,Variable>();
		Term targetFunction = null;
		i = 0;		
		for(ProbabilisticConditional c: beliefSet){
			FloatVariable eta = new FloatVariable("e" + i,0,1);
			FloatVariable tau = new FloatVariable("t" + i++,0,1);
			etas.put(c, eta);
			taus.put(c, tau);
			if(targetFunction == null)
				targetFunction = eta.add(tau);
			else targetFunction = targetFunction.add(eta.add(tau));
			Term leftSide = null;
			Term rightSide = null;
			if(c.isFact()){
				for(PossibleWorld w: worlds)
					if(w.satisfies(c.getConclusion())){
						if(leftSide == null)
							leftSide = worlds2vars.get(w);
						else leftSide = leftSide.add(worlds2vars.get(w));
					}
				rightSide = new FloatConstant(c.getProbability().getValue()).add(eta).minus(tau);
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
				else rightSide = rightSide.mult(new FloatConstant(c.getProbability().getValue()).add(eta).minus(tau));
			}
			if(leftSide == null)
				leftSide = new FloatConstant(0);
			if(rightSide == null)
				rightSide = new FloatConstant(0);
			problem.add(new Equation(leftSide,rightSide));			
		}
		// add conformity constraints
		Stack<ProbabilisticConditional> stack = new Stack<ProbabilisticConditional>();
		stack.addAll(beliefSet);
		while(!stack.isEmpty()){
			ProbabilisticConditional pc1 = stack.pop();
			for(ProbabilisticConditional pc2: stack){
				// the following is equivalent to the code commented out (at least for the solution of the problem)
				Term leftSide = etas.get(pc1).add(taus.get(pc1));//etas.get(pc1).minus(taus.get(pc1)).mult(etas.get(pc1).minus(taus.get(pc1)));
				Term rightSide = etas.get(pc2).add(taus.get(pc2));//etas.get(pc2).minus(taus.get(pc2)).mult(etas.get(pc2).minus(taus.get(pc2)));
				if(culpMeasures.get(pc1) > culpMeasures.get(pc2) - BalancedMachineShop.PRECISIONCULP && culpMeasures.get(pc1) < culpMeasures.get(pc2) + BalancedMachineShop.PRECISIONCULP){				
					problem.add(new Inequation(leftSide,rightSide.minus(new FloatConstant(BalancedMachineShop.PRECISIONOPT)),Inequation.GREATER_EQUAL));
					problem.add(new Inequation(leftSide,rightSide.add(new FloatConstant(BalancedMachineShop.PRECISIONOPT)),Inequation.LESS_EQUAL));
					//System.out.println(pc1 + " == " + pc2);
				}else if(culpMeasures.get(pc1) > culpMeasures.get(pc2)){
					problem.add(new Inequation(leftSide,rightSide,Inequation.GREATER));
					//System.out.println(pc1 + " > " + pc2);
				}
				else {
					problem.add(new Inequation(leftSide,rightSide,Inequation.LESS));
					//System.out.println(pc1 + " < " + pc2);
				}
			}
		}
		problem.setTargetFunction(targetFunction);
		try{			
			OpenOptSolver solver = new OpenOptSolver();
			solver.contol = 1e-8;
			solver.gtol = 1e-15;
			solver.ftol = 1e-15;
			solver.xtol = 1e-15;
			//solver.ignoreNotFeasibleError = true;
			Map<Variable,Term> solution = solver.solve(problem);
			// prepare result
			PclBeliefSet result = new PclBeliefSet();
			for(ProbabilisticConditional pc: beliefSet)
				result.add(new ProbabilisticConditional(pc,new Probability(pc.getProbability().doubleValue() + solution.get(etas.get(pc)).doubleValue() - solution.get(taus.get(pc)).doubleValue())));							
			return result;
		}catch (GeneralMathException e){
			// This should not happen as the optimization problem is guaranteed to be feasible
			throw new RuntimeException("Fatal error: Optimization problem to compute the minimal distance to a consistent knowledge base is not feasible.");
		}
	}

}
