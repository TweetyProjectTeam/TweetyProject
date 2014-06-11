package net.sf.tweety.logics.pcl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.sf.tweety.Answer;
import net.sf.tweety.BeliefBase;
import net.sf.tweety.Formula;
import net.sf.tweety.Reasoner;
import net.sf.tweety.Signature;
import net.sf.tweety.logics.cl.syntax.Conditional;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pcl.analysis.MinimalViolation2InconsistencyMeasure;
import net.sf.tweety.logics.pcl.analysis.MinimalViolationInconsistencyMeasure;
import net.sf.tweety.logics.pcl.semantics.ProbabilityDistribution;
import net.sf.tweety.logics.pcl.syntax.ProbabilisticConditional;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.equation.Equation;
import net.sf.tweety.math.equation.Inequation;
import net.sf.tweety.math.norm.ManhattanNorm;
import net.sf.tweety.math.norm.MaximumNorm;
import net.sf.tweety.math.norm.PNorm;
import net.sf.tweety.math.norm.RealVectorNorm;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.solver.LpSolve;
import net.sf.tweety.math.opt.solver.OpenOptWebSolver;
import net.sf.tweety.math.probability.Probability;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.Logarithm;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

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


	public final static int MANHATTAN = 1;
	public final static int EUCLIDEAN = 2;
	public final static int MAXIMUM = 0;
	
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

	/** The corresponding inconsistency measure. */
	private BeliefSetInconsistencyMeasure<ProbabilisticConditional> inc;

	/** The numerical accuracy. */
	private double accuracy;
	
	/**
	 * Creates a new generalized ME-reasoner for the given knowledge base.
	 * @param beliefBase a pcl belief set. 
	 * @param p determines p-norm for computing minimal violation.
	 * @param solver the solver used
	 */
	public GeneralizedMeReasoner(BeliefBase beliefBase, int p){
		this(beliefBase, beliefBase.getSignature(), p);
	}
	
	/**
	 * Creates a new generalized ME-reasoner for the given knowledge base.
	 * @param beliefBase a pcl belief set. 
	 * @param signature another signature (if the probability distribution should be defined 
	 * on that one (that one should subsume the signature of the belief base)
	 * @param norm the norm used for computing minimal violation.
	 * @param solver the solver used
	 */
	public GeneralizedMeReasoner(BeliefBase beliefBase, Signature signature, int p){
		super(beliefBase);	
		if(!(beliefBase instanceof PclBeliefSet))
			throw new IllegalArgumentException("Knowledge base of class PclBeliefSet expected.");
		if(!beliefBase.getSignature().isSubSignature(signature))
			throw new IllegalArgumentException("Given signature is not a super-signature of the belief base's signature.");
		this.signature = signature;	

		this.inc = null;
		this.accuracy = 0.01;
		
		switch(p) {
			case MANHATTAN:
				this.norm = new ManhattanNorm();
				if(LpSolve.checkBinary())
					this.inc = new MinimalViolationInconsistencyMeasure(this.norm, new LpSolve());
				else this.inc = new MinimalViolationInconsistencyMeasure(this.norm, new OpenOptWebSolver());
				break;
			case EUCLIDEAN:
				//special case for Euclidean: we can use OjAlgo library
				this.norm = new PNorm(2);
				this.inc = new MinimalViolation2InconsistencyMeasure();
				break;
			case MAXIMUM:
				if(!LpSolve.checkBinary()) throw new IllegalArgumentException("LPSolve must be configured properly to use Maximum norm.");
				this.norm = new MaximumNorm();
				this.inc = new MinimalViolationInconsistencyMeasure(this.norm, new LpSolve());
				break;
			default:
				this.norm = new PNorm(p);
				this.inc = new MinimalViolationInconsistencyMeasure(this.norm, new OpenOptWebSolver());
		}
			
		
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
		double iValue = inc.inconsistencyMeasure((PclBeliefSet)this.getKnowledgBase());		
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
		// NOTE: instead of one equality, we use two inequalities with a little tolerance
		//       to avoid numerical problems
		problem.add(new Inequation(this.norm.normTerm(vioVars),new FloatConstant(iValue-accuracy),Inequation.GREATER_EQUAL));
		problem.add(new Inequation(this.norm.normTerm(vioVars),new FloatConstant(iValue+accuracy),Inequation.LESS_EQUAL));
		// target function is the entropy
		Term targetFunction = null;
		for(PossibleWorld w: worlds){
			if(targetFunction == null)
				targetFunction = vars.get(w).mult(new Logarithm(vars.get(w)));
			else targetFunction = targetFunction.add(vars.get(w).mult(new Logarithm(vars.get(w))));			
		}
		problem.setTargetFunction(targetFunction);
		try{			
			OpenOptWebSolver solver = new OpenOptWebSolver();
			solver.solver = "ralg";
			Map<Variable,Term> solution = solver.solve(problem);
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
