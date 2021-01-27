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
package org.tweetyproject.logics.commons.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.Interpretation;
import org.tweetyproject.commons.InterpretationIterator;
import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.equation.Equation;
import org.tweetyproject.math.equation.Inequation;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.opt.solver.Solver;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.FloatVariable;
import org.tweetyproject.math.term.IntegerConstant;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;

/**
 * This class implements the Eta-inconsistency measure, cf. [Knight, 2002].
 * @author Matthias Thimm
 * @param <B> the type of belief bases
 *
 * @param <S> The type of formula
 */
public class EtaInconsistencyMeasure<B extends BeliefBase,S extends Formula> extends BeliefSetInconsistencyMeasure<S>{

	/** Used for enumerating the interpretations of the underlying language. */
	private InterpretationIterator<S,B,? extends Interpretation<B,S>> it;
	
	/** 
	 * Creates a new inconsistency measure that uses the interpretations given
	 * by the given iterator.
	 * @param it some interpretation iterator.
	 */
	public EtaInconsistencyMeasure(InterpretationIterator<S,B,? extends Interpretation<B,S>> it){
		this.it = it;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> formulas) {
		// re-initialize interpretation iterator with correct signature
		this.it = it.reset(formulas);
		// We implement this as an optimization problem and maximize eta
		OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MAXIMIZE);
		FloatVariable eta = new FloatVariable("eta");
		problem.add(new Inequation(eta,new FloatConstant(0),Inequation.GREATER_EQUAL));
		problem.add(new Inequation(eta,new FloatConstant(1),Inequation.LESS_EQUAL));
		problem.setTargetFunction(eta);
		Map<Interpretation<B,S>,Variable> worlds2vars = new HashMap<Interpretation<B,S>,Variable>();
		int i = 0;
		Term normConstraint = null;		
		this.it = it.reset();
		while(this.it.hasNext()){
			Interpretation<B,S> interpretation = this.it.next();
			FloatVariable var = new FloatVariable("w" + i++);
			problem.add(new Inequation(var,new FloatConstant(0),Inequation.GREATER_EQUAL));
			problem.add(new Inequation(var,new FloatConstant(1),Inequation.LESS_EQUAL));
			worlds2vars.put(interpretation, var);
			if(normConstraint == null)
				normConstraint = var;
			else normConstraint = normConstraint.add(var);
		}		
		problem.add(new Equation(normConstraint, new IntegerConstant(1)));
		// for each formula, it probability must be at least as large as eta
		for(S f: formulas){
			Term leftTerm = null;
			this.it = it.reset();
			while(this.it.hasNext()){
				Interpretation<B,S> interpretation = this.it.next();
				if(interpretation.satisfies(f))
					if(leftTerm == null)
						leftTerm = worlds2vars.get(interpretation);
					else leftTerm = leftTerm.add(worlds2vars.get(interpretation));					
			}
			if(leftTerm == null)
				problem.add(new Inequation(eta,new FloatConstant(0),Inequation.LESS_EQUAL));
			else problem.add(new Inequation(leftTerm.minus(eta),new FloatConstant(0),Inequation.GREATER_EQUAL));
		}
		// solve the problem
		try {
			Map<Variable, Term> solution = Solver.getDefaultLinearSolver().solve(problem);			
			return 1-solution.get(eta).doubleValue();
		} catch (GeneralMathException e) {
			// there is probably an inconsistent formula, so it should have maximal inconsistency
			return 1d;
		} catch (Exception e){
			// now the problem is probably consistent.
			return 0d;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "eta";
	}
}
