package net.sf.tweety.logics.pcl.analysis;

import java.util.*;

import net.sf.tweety.logics.pcl.*;
import net.sf.tweety.logics.pcl.syntax.*;
import net.sf.tweety.logics.pl.semantics.*;
import net.sf.tweety.logics.pl.syntax.*;
import net.sf.tweety.math.*;
import net.sf.tweety.math.opt.*;
import net.sf.tweety.math.term.*;
import net.sf.tweety.logics.commons.analysis.*;

/**
 * This class is capable of checking whether a given conditional knowledge base
 * is consistent by searching for the root of some equivalent multi-dimensional function.
 * 
 * @author Matthias Thimm
 */
public class PclDefaultConsistencyTester extends AbstractBeliefSetConsistencyTester<ProbabilisticConditional,PclBeliefSet> {
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.AbstractBeliefSetConsistencyTester#isConsistent(java.util.Collection)
	 */
	@Override
	public boolean isConsistent(Collection<ProbabilisticConditional> formulas) {
		PclBeliefSet beliefSet = new PclBeliefSet(formulas);
		if(beliefSet.isEmpty()) return true;
		// Create variables for the probability of each possible world and
		// create a multi-dimensional function that has a root iff the belief base is consistent
		List<Term> functions = new ArrayList<Term>();
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
		normConstraint = normConstraint.add(new IntegerConstant(-1));
		functions.add(normConstraint);
		// add constraints implied by the conditionals
		for(ProbabilisticConditional c: beliefSet){
			Term leftSide = null;
			Term rightSide = null;
			if(c.isFact()){
				for(PossibleWorld w: worlds)
					if(w.satisfies(c.getConclusion())){
						if(leftSide == null)
							leftSide = worlds2vars.get(w);
						else leftSide = leftSide.add(worlds2vars.get(w));
					}
				rightSide = new FloatConstant(c.getProbability().getValue());
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
				else rightSide = rightSide.mult(new FloatConstant(c.getProbability().getValue()));
			}
			if(leftSide == null)
				leftSide = new FloatConstant(0);
			if(rightSide == null)
				rightSide = new FloatConstant(0);
			functions.add(leftSide.minus(rightSide));			
		}
		// Search for a root of "functions" using OpenOpt
		Map<Variable,Term> startingPoint = new HashMap<Variable,Term>();
		for(PossibleWorld w: worlds)
			startingPoint.put(worlds2vars.get(w), new IntegerConstant(1));
		OpenOptRootFinder rootFinder = new OpenOptRootFinder(functions,startingPoint);
		RootFinder.PRECISION = 0.001;
		rootFinder.contol = 1.0E-8;
		rootFinder.ftol = 1.0E-8;
		rootFinder.gtol = 1.0E-8;
		rootFinder.xtol = 1.0E-8;		
		try {
			rootFinder.randomRoot();
		} catch (GeneralMathException e) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester#minimalInconsistentSubsets(java.util.Collection)
	 */
	@Override
	public Collection<Collection<ProbabilisticConditional>> minimalInconsistentSubsets(	Collection<ProbabilisticConditional> formulas) {
		return this.minimalInconsistentSubsets(new PclBeliefSet(formulas));
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester#maximalConsistentSubsets(java.util.Collection)
	 */
	@Override
	public Collection<Collection<ProbabilisticConditional>> maximalConsistentSubsets(Collection<ProbabilisticConditional> formulas) {
		return this.maximalConsistentSubsets(new PclBeliefSet(formulas));
	}

}
