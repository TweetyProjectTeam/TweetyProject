/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.prob.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.tweetyproject.logics.commons.analysis.InconsistencyMeasure;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.prob.semantics.PASemantics;
import org.tweetyproject.arg.prob.syntax.PartialProbabilityAssignment;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.equation.Equation;
import org.tweetyproject.math.norm.RealVectorNorm;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.opt.solver.Solver;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.FloatVariable;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;


/**
 * This inconsistency measure measures the distance between a given partial
 * probability assignment to the set of rational/justifiable probabilistic extensions
 * of a given Dung theory.
 * 
 * @author Matthias Thimm
 *
 */
public class PAInconsistencyMeasure implements InconsistencyMeasure<PartialProbabilityAssignment> {
		
	/** The norm used for measuring the distances. */
	private RealVectorNorm norm;
	/** The Dung theory against the partial prob assignments are measured. */
	private DungTheory dungTheory;
	/** The semantics against which the inconsistency of partial probability assignments are measured. */
	private PASemantics semantics;
	
	/**
	 * Creates a new inconsinstency measure which uses the given norm and
	 * measures wrt. the given theory.
	 * @param norm a norm
	 * @param theory a Dung theory
	 * @param semantics the semantics against which the inconsistency of partial probability assignments are measured.
	 */
	public PAInconsistencyMeasure(RealVectorNorm norm, DungTheory theory, PASemantics semantics){
		this.semantics = semantics;
		this.norm = norm;
		this.dungTheory = theory;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.InconsistencyMeasure#inconsistencyMeasure(org.tweetyproject.BeliefBase)
	 */
	@Override
	public Double inconsistencyMeasure(PartialProbabilityAssignment ppa) {
		// construct optimization problem
		OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MINIMIZE);
		Set<Set<Argument>> configurations = new SetTools<Argument>().subsets(this.dungTheory);
		// for pi-compliant prob'functions
		Map<Set<Argument>,FloatVariable> varsComp = new HashMap<Set<Argument>,FloatVariable>();
		Vector<Term> varsCompVector = new Vector<Term>();
		// for semantically satisfying prob'functions
		Map<Collection<Argument>,FloatVariable> varsSem = new HashMap<Collection<Argument>,FloatVariable>();
		Vector<Term> varsSemVector = new Vector<Term>();
		Term normConstraintComp = null;
		Term normConstraintSem = null;
		for(Set<Argument> w: configurations){
			FloatVariable varComp = new FloatVariable("c" + w.toString(),0,1);
			FloatVariable varSem = new FloatVariable("s" + w.toString(),0,1);
			varsComp.put(w, varComp);
			varsSem.put(w, varSem);
			varsCompVector.add(varComp);
			varsSemVector.add(varSem);
			if(normConstraintComp == null)
				normConstraintComp = varComp;
			else normConstraintComp = normConstraintComp.add(varComp);
			if(normConstraintSem == null)
				normConstraintSem = varSem;
			else normConstraintSem = normConstraintSem.add(varSem);
		}
		problem.add(new Equation(normConstraintComp,new FloatConstant(1)));
		problem.add(new Equation(normConstraintSem,new FloatConstant(1)));
		// add constraints from partial probability assignment
		for(Argument arg: ppa.keySet()){
			Term leftSide = new FloatConstant(ppa.get(arg).doubleValue());
			Term rightSide = null;
			for(Set<Argument> set: configurations)
				if(set.contains(arg))
					if(rightSide == null)
						rightSide = varsComp.get(set);
					else rightSide = rightSide.add(varsComp.get(set));
			problem.add(new Equation(leftSide,rightSide));
		}
		// add constraints imposed by semantics
		problem.addAll(this.semantics.getSatisfactionStatements(this.dungTheory, varsSem));
		// Target function
		problem.setTargetFunction(this.norm.distanceTerm(varsCompVector,varsSemVector));		
		// Do the optimization

		try{			
			Map<Variable,Term> solution = Solver.getDefaultGeneralSolver().solve(problem);
			//for(Variable v: solution.keySet())
			//	System.out.println(v + "\t" + solution.get(v));
			return problem.getTargetFunction().replaceAllTerms(solution).value().doubleValue();
		}catch (GeneralMathException ex){
			// This should not happen as the optimization problem is guaranteed to be feasible
			throw new RuntimeException("Fatal error: Optimization problem to compute the inconsistency measure is not feasible.");
		}
	}

}
