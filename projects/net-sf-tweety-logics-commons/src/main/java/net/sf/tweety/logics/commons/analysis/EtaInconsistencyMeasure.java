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
package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.commons.InterpretationIterator;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.equation.Equation;
import net.sf.tweety.math.equation.Inequation;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.Solver;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * This class implements the Eta-inconsistency measure, cf. [Knight, 2002].
 * @author Matthias Thimm
 *
 * @param <S> The type of formula
 */
public class EtaInconsistencyMeasure<S extends Formula> extends BeliefSetInconsistencyMeasure<S>{

	/** Used for enumerating the interpretations of the underlying language. */
	private InterpretationIterator<?> it;
	
	/** 
	 * Creates a new inconsistency measure that uses the interpretations given
	 * by the given iterator.
	 * @param it some interpretation iterator.
	 */
	public EtaInconsistencyMeasure(InterpretationIterator<?> it){
		this.it = it;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
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
		Map<Interpretation,Variable> worlds2vars = new HashMap<Interpretation,Variable>();
		int i = 0;
		Term normConstraint = null;		
		this.it = it.reset();
		while(this.it.hasNext()){
			Interpretation interpretation = this.it.next();
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
				Interpretation interpretation = this.it.next();
				if(interpretation.satisfies(f))
					if(leftTerm == null)
						leftTerm = worlds2vars.get(interpretation);
					else leftTerm = leftTerm.add(worlds2vars.get(interpretation));					
			}
			problem.add(new Inequation(leftTerm,eta,Inequation.GREATER_EQUAL));
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
			e.printStackTrace();
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
