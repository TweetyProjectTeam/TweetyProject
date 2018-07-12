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
package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


import net.sf.tweety.commons.Formula;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.equation.Inequation;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.Solver;
import net.sf.tweety.math.term.BinaryVariable;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * This class implements the inconsistency measure I_CC from 
 * [Said Jabbour and Yue Ma and Badran Raddaoui. Inconsistency Measurement Thanks to MUS Decomposition. AAMAS 2014.]
 * 
 * The measure is implemented using the integer programming approach presented in 
 * [Said Jabbour, Yue Ma, Badran Raddaoui, Lakhdar Sais, Yakoub Salhi.
 *  On Structure-Based Inconsistency Measures and Their Computations via Closed Set Packing. AAMAS 2015]
 *  
 * @author Matthias Thimm
 *
 * @param <S> The specific type of formulas
 */
public class CcInconsistencyMeasure<S extends Formula> extends BeliefSetInconsistencyMeasure<S> {
	
	/** The MUS enumerator used for the measure. */
	private MusEnumerator<S> enumerator;
	/** A solver for integer linear programs. */
	private Solver solver;
	
	/**
	 * Creates a new measure that uses the given MUS enumerator and
	 * Integer programming solver
	 * @param enumerator some MUS enumerator
	 * @param solver some integer solver.
	 */
	public CcInconsistencyMeasure(MusEnumerator<S> enumerator, Solver solver){
		this.enumerator = enumerator;
		this.solver = solver;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	public Double inconsistencyMeasure(Collection<S> formulas) {
		Collection<Collection<S>> mises = this.enumerator.minimalInconsistentSubsets(formulas);
		// special case of consistent set		
		if(mises.isEmpty())
			return 0d;
		// construct an integer linear programming problem
		// associate a binary variable to each formula
		Map<S,Variable> f2v = new HashMap<S,Variable>();
		Map<Variable,S> v2f = new HashMap<Variable,S>();
		int idx = 0;
		for(S f: formulas){
			BinaryVariable v = new BinaryVariable("X" + idx++); 
			f2v.put(f, v);
			v2f.put(v,f);
		}
		// associate a binary variable to each MIS
		Map<Collection<S>,Variable> m2v = new HashMap<Collection<S>,Variable>();
		Map<Variable,Collection<S>> v2m = new HashMap<Variable,Collection<S>>();
		idx = 0;
		for(Collection<S> mis: mises){
			BinaryVariable v = new BinaryVariable("Y" + idx++); 
			m2v.put(mis, v);
			v2m.put(v,mis);
		}
		// target function is the maximal number of selected MISes
		Term target = null;
		for(Variable v: v2m.keySet())
			if(target == null)
				target = v;
			else target = target.add(v);
		OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MAXIMIZE);
		problem.setTargetFunction(target);
		// add constraints
		// 1) each formula is contained in maximal one mis
		for(S f: formulas){
			Term left = null;
			for(Collection<S> mis: mises){
				if(mis.contains(f))
					if(left == null)
						left = m2v.get(mis);
					else left = left.add(m2v.get(mis));
			}
			if(left != null)
				problem.add(new Inequation(left,new IntegerConstant(1),Inequation.LESS_EQUAL));
		}
		// 2) if a mis is selected, all its contained formulas must be selected
		// 3) if all formulas of a mis are selected, the mis must be selected
		for(Collection<S> mis: mises){
			Term left2 = new IntegerConstant(-mis.size()).mult(m2v.get(mis));
			Term left3 = new IntegerConstant(-1).mult(m2v.get(mis));
			for(S f: mis){
				left2 = left2.add(f2v.get(f));
				left3 = left3.add(f2v.get(f));
			}
			problem.add(new Inequation(left2,new IntegerConstant(0),Inequation.GREATER_EQUAL));
			problem.add(new Inequation(left3,new IntegerConstant(mis.size()-1),Inequation.LESS_EQUAL));			
		}		
		try {
			Map<Variable,Term> result = this.solver.solve(problem);
			return problem.getTargetFunction().replaceAllTerms(result).doubleValue();
		} catch (GeneralMathException e) {
			throw new RuntimeException(e);			
		}		
	}
}
