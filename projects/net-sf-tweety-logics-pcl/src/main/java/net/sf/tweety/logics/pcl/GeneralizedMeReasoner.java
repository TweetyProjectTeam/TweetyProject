package net.sf.tweety.logics.pcl;

import java.util.*;

import net.sf.tweety.*;
import net.sf.tweety.logics.cl.syntax.*;
import net.sf.tweety.logics.pcl.analysis.*;
import net.sf.tweety.logics.pcl.semantics.*;
import net.sf.tweety.logics.pcl.syntax.*;
import net.sf.tweety.logics.pl.semantics.*;
import net.sf.tweety.logics.pl.syntax.*;
import net.sf.tweety.math.*;
import net.sf.tweety.math.equation.*;
import net.sf.tweety.math.norm.RealVectorNorm;
import net.sf.tweety.math.opt.*;
import net.sf.tweety.math.opt.solver.*;
import net.sf.tweety.math.term.*;
import net.sf.tweety.math.probability.*;

/**
 * This class implements a generalized maximum entropy reasoner for probabilistic
 * conditional logic as proposed in [Potyka, Thimm, 2014] which also works for
 * inconsistent belief sets. It computes the generalized  ME-distribution for
 * the given belief set and answers queries with respect to this ME-distribution.
 * 
 * @author Matthias Thimm
 *
 */
public class GeneralizedMeReasoner extends Reasoner {

	/**
	 * The ME-distribution this reasoner bases on.
	 */
	private ProbabilityDistribution<PossibleWorld> meDistribution = null;
	
	/**
	 * The signature of the reasoner.
	 */
	private Signature signature = null;
	
	/** The norm. */
	private RealVectorNorm norm;
	
	/**
	 * Creates a new generalized ME-reasoner for the given knowledge base.
	 * @param beliefBase a pcl belief set. 
	 * @param norm the norm used for computing minimal violation.
	 */
	public GeneralizedMeReasoner(BeliefBase beliefBase, RealVectorNorm norm){
		this(beliefBase, beliefBase.getSignature(),norm);
	}
	
	/**
	 * Creates a new generalized ME-reasoner for the given knowledge base.
	 * @param beliefBase a pcl belief set. 
	 * @param signature another signature (if the probability distribution should be defined 
	 * on that one (that one should subsume the signature of the belief base)
	 * @param norm the norm used for computing minimal violation.
	 */
	public GeneralizedMeReasoner(BeliefBase beliefBase, Signature signature, RealVectorNorm norm){
		super(beliefBase);		
		this.norm = norm;
		if(!(beliefBase instanceof PclBeliefSet))
			throw new IllegalArgumentException("Knowledge base of class PclBeliefSet expected.");
		if(!beliefBase.getSignature().isSubSignature(signature))
			throw new IllegalArgumentException("Given signature is not a super-signature of the belief base's signature.");
		this.signature = signature;
	}
	
	/**
	 * Returns the ME-distribution this reasoner bases on.
	 * @return the ME-distribution this reasoner bases on.
	 */
	public ProbabilityDistribution<PossibleWorld> getMeDistribution(){
		if(this.meDistribution == null)
			this.meDistribution = this.computeMeDistribution();
		return this.meDistribution;
	}
	
	/**
	 * Computes the ME-distribution this reasoner bases on.
	 * @return the ME-distribution this reasoner bases on.
	 */
	private ProbabilityDistribution<PossibleWorld> computeMeDistribution(){
		// get inconsistency value
		MinimalViolationInconsistencyMeasure m = new MinimalViolationInconsistencyMeasure(this.norm);
		double iValue = m.inconsistencyMeasure((PclBeliefSet)this.getKnowledgBase());		
		// construct optimization problem
		OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MINIMIZE);
		Set<PossibleWorld> worlds = PossibleWorld.getAllPossibleWorlds((PropositionalSignature) this.signature);
		Map<PossibleWorld,Variable> vars = new HashMap<PossibleWorld,Variable>();		
		int cnt = 0;
		Term normConstraint = null;
		for(PossibleWorld w: worlds){
			Variable var = new FloatVariable("w" + cnt,0,1);
			vars.put(w, var);
			if(normConstraint == null)
				normConstraint = var;
			else normConstraint = normConstraint.add(var);
			cnt++;
		}
		problem.add(new Equation(normConstraint,new FloatConstant(1)));
		// add constraints imposed by conditionals
		cnt = 0;
		// violation variables
		Vector<Term> vioVars = new Vector<Term>(); 
		for(ProbabilisticConditional pc: (PclBeliefSet)this.getKnowledgBase()){
			Variable vio = new FloatVariable("x" + cnt++,-1,1);
			vioVars.add(vio);
			Term leftSide = null;
			Term rightSide = null;			
			if(pc.isFact()){
				for(PossibleWorld w: worlds)
					if(w.satisfies(pc.getConclusion())){
						if(leftSide == null)
							leftSide = vars.get(w);
						else leftSide = leftSide.add(vars.get(w));
					}
				rightSide = new FloatConstant(pc.getProbability().getValue());
			}else{				
				PropositionalFormula body = pc.getPremise().iterator().next();
				PropositionalFormula head_and_body = pc.getConclusion().combineWithAnd(body);
				for(PossibleWorld w: worlds){
					if(w.satisfies(head_and_body)){
						if(leftSide == null)
							leftSide = vars.get(w);
						else leftSide = leftSide.add(vars.get(w));
					}
					if(w.satisfies(body)){
						if(rightSide == null)
							rightSide = vars.get(w);
						else rightSide = rightSide.add(vars.get(w));
					}					
				}
				if(rightSide == null)
					rightSide = new FloatConstant(0);
				else rightSide = rightSide.mult(new FloatConstant(pc.getProbability().getValue()));
			}
			if(leftSide == null)
				leftSide = new FloatConstant(0);
			if(rightSide == null)
				rightSide = new FloatConstant(0);
			rightSide = rightSide.add(vio);
			problem.add(new Equation(leftSide,rightSide));
		}
		// add constraint on violation variables
		// NOTE: instead of an equation we make to inequations with a little tolerance
		//       to allow for numerical issues
		problem.add(new Inequation(this.norm.normTerm(vioVars),new FloatConstant(iValue-Probability.PRECISION),Inequation.GREATER_EQUAL));
		problem.add(new Inequation(this.norm.normTerm(vioVars),new FloatConstant(iValue+Probability.PRECISION),Inequation.LESS_EQUAL));
		// target function is the entropy
		Term targetFunction = null;
		for(PossibleWorld w: worlds){
			if(targetFunction == null)
				targetFunction = vars.get(w).mult(new Logarithm(vars.get(w)));
			else targetFunction = targetFunction.add(vars.get(w).mult(new Logarithm(vars.get(w))));			
		}
		problem.setTargetFunction(targetFunction);
		try{			
			OpenOptWebSolver solver = new OpenOptWebSolver(problem);
			solver.solver = "ralg";
			Map<Variable,Term> solution = solver.solve();
			// construct probability distribution
			ProbabilityDistribution<PossibleWorld> p = new ProbabilityDistribution<PossibleWorld>(this.signature);
			for(PossibleWorld w: worlds)
				p.put(w, new Probability(solution.get(vars.get(w)).doubleValue()));
			return p;					
		}catch (GeneralMathException e){
			// This should not happen as the optimization problem is guaranteed to be feasible (the knowledge base is consistent)
			throw new RuntimeException("Fatal error: Optimization problem to compute the ME-distribution is not feasible.");
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.Reasoner#query(net.sf.tweety.Formula)
	 */
	@Override
	public Answer query(Formula query) {
		if(!(query instanceof Conditional) && !(query instanceof PropositionalFormula))
			throw new IllegalArgumentException("Reasoning in probabilistic conditional logic is only defined for (probabilistic) conditionals and propositional queries.");
		ProbabilityDistribution<PossibleWorld> meDistribution = this.getMeDistribution();
		if(query instanceof ProbabilisticConditional){
			Answer answer = new Answer(this.getKnowledgBase(),query);
			boolean bAnswer = meDistribution.satisfies(query);
			answer.setAnswer(bAnswer);
			answer.appendText("The answer is: " + bAnswer);
			return answer;			
		}
		if(query instanceof Conditional){
			Answer answer = new Answer(this.getKnowledgBase(),query);
			Probability bAnswer = meDistribution.probability((Conditional)query);
			answer.setAnswer(bAnswer.doubleValue());
			answer.appendText("The answer is: " + bAnswer);
			return answer;
		}
		if(query instanceof PropositionalFormula){
			Answer answer = new Answer(this.getKnowledgBase(),query);
			Probability bAnswer = meDistribution.probability(query);
			answer.setAnswer(bAnswer.doubleValue());
			answer.appendText("The answer is: " + bAnswer);
			return answer;
		}			
		return null;
	}	
}
