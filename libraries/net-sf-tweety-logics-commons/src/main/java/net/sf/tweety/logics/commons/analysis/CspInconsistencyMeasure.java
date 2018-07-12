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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.equation.Equation;
import net.sf.tweety.math.equation.Inequation;
import net.sf.tweety.math.func.FractionSequenceFunction;
import net.sf.tweety.math.func.SimpleFunction;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.Solver;
import net.sf.tweety.math.term.BinaryVariable;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * This class implements the inconsistency measure I_CSP from 
 * [Said Jabbour, Yue Ma, Badran Raddaoui, Lakhdar Sais, Yakoub Salhi.
 *  On Structure-Based Inconsistency Measures and Their Computations via Closed Set Packing. AAMAS 2015]
 * 
 * The measure is implemented using an integer programming approach sketched in the above paper.
 * 
 *  Note that this measure is equivalent to I_W from
 *  [Said Jabbour and Yue Ma and Badran Raddaoui and Lakhdar Sais and Yakoub Salhi.
 *  A MIS Partition Based Framework for Measuring Inconsistency. KR 2016]
 *  
 * @author Matthias Thimm
 *
 * @param <S> The specific type of formulas
 */
public class CspInconsistencyMeasure<S extends Formula> extends BeliefSetInconsistencyMeasure<S> {
	
	/** The MUS enumerator used for the measure. */
	private MusEnumerator<S> enumerator;
	/** A solver for integer linear programs. */
	private Solver solver;
	/** Used for weighing the cardinalities of the Pi*/
	private SimpleFunction<Double,Double> measureFunction;
	
	/**
	 * Creates a new measure that uses the given measure function, MUS enumerator, and
	 * Integer programming solver
	 * @param measureFunction used for weighing the cardinalities of the Pi
	 * @param enumerator some MUS enumerator
	 * @param solver some integer solver.
	 */
	public CspInconsistencyMeasure(SimpleFunction<Double,Double> measureFunction, MusEnumerator<S> enumerator, Solver solver){
		this.measureFunction = measureFunction;
		this.enumerator = enumerator;
		this.solver = solver;
	}
	
	/**
	 * Creates a new measure that uses the given MUS enumerator and
	 * Integer programming solver. The fractions 1/x are used to weigh
	 * Pis on position x=i 
	 * @param enumerator some MUS enumerator
	 * @param solver some integer solver.
	 */
	public CspInconsistencyMeasure(MusEnumerator<S> enumerator, Solver solver){
		this(new FractionSequenceFunction(),enumerator,solver);
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
		// we are constructing P={P1,...Pn} where P1 u...u Pn = mises
		// and each Pi is a CSP. Note that P can be of maximal cardinality mises.size().
		
		// associate a binary variable to each formula and Pi pair
		// (indicating that the formula is contained some set in Pi)
		Map<S,List<Variable>> f2v = new HashMap<S,List<Variable>>();
		Map<Variable,S> v2f = new HashMap<Variable,S>();
		int idx = 0;
		for(S f: formulas){
			List<Variable> vars = new ArrayList<Variable>(); 
			for(int i = 0; i < mises.size(); i++){
				BinaryVariable v = new BinaryVariable("X" + idx + "_" + i);				
				v2f.put(v,f);
				vars.add(v);
			}
			idx++;
			f2v.put(f, vars);
		}
		// associate a binary variable to each pair of MIS and Pi
		// (indicating that the MIS is contained in that Pi)
		Map<Collection<S>,List<Variable>> m2v = new HashMap<Collection<S>,List<Variable>>();
		Map<Variable,Collection<S>> v2m = new HashMap<Variable,Collection<S>>();
		List<Collection<Variable>> p2v = new ArrayList<Collection<Variable>>();
		for(int i = 0; i < mises.size(); i++)
			p2v.add(new HashSet<Variable>());
		idx = 0;
		for(Collection<S> mis: mises){
			List<Variable> vars = new ArrayList<Variable>();
			for(int i = 0; i < mises.size(); i++){
				BinaryVariable v = new BinaryVariable("Y" + idx + "_" + i);				
				v2m.put(v,mis);
				vars.add(v);
				p2v.get(i).add(v);
			}
			idx++;
			m2v.put(mis, vars);			
		}
		// target function is the sum of the products of the cardinalities
		// of each Pi with the wi (given as parameter)
		Term target = null;
		for(int i = 0; i < mises.size(); i++){
			for(Variable v: p2v.get(i))
				if(target == null)
					target = new FloatConstant(this.measureFunction.eval((double) (i+1))).mult(v); 
				else target = target.add(new FloatConstant(this.measureFunction.eval((double) (i+1))).mult(v));			
		}
		OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MAXIMIZE);
		problem.setTargetFunction(target);
		// add constraints
		// 1) each formula is contained in maximal one mis for all Pi
		for(int i = 0; i < mises.size(); i++){
			for(S f: formulas){
				Term left = null;
				for(Collection<S> mis: mises){
					if(mis.contains(f))
						if(left == null)
							left = m2v.get(mis).get(i);
						else left = left.add(m2v.get(mis).get(i));
				}
				if(left != null)
					problem.add(new Inequation(left,new IntegerConstant(1),Inequation.LESS_EQUAL));
			}
		}
		// 2) if a mis is selected, all its contained formulas must be selected (for all Pi)
		// 3) if all formulas of a mis are selected, the mis must be selected (for all Pi)
		for(int i = 0; i < mises.size(); i++){
			for(Collection<S> mis: mises){
				Term left2 = new IntegerConstant(-mis.size()).mult(m2v.get(mis).get(i));
				Term left3 = new IntegerConstant(-1).mult(m2v.get(mis).get(i));
				for(S f: mis){
					left2 = left2.add(f2v.get(f).get(i));
					left3 = left3.add(f2v.get(f).get(i));
				}
				problem.add(new Inequation(left2,new IntegerConstant(0),Inequation.GREATER_EQUAL));
				problem.add(new Inequation(left3,new IntegerConstant(mis.size()-1),Inequation.LESS_EQUAL));			
			}		
		}
		// 4) each MIS must occur in exactly one Pi
		for(Collection<S> mis: mises){
			Term sum = null;
			for(Variable v: m2v.get(mis))
				if(sum == null)
					sum = v;
				else sum = sum.add(v);
			problem.add(new Equation(sum,new IntegerConstant(1)));
		}
		// 5) the cardinalities of the Pi are non-increasing in i
		for(int i = 0; i < mises.size()-1; i++){
			Term left = null;
			for(Variable v: p2v.get(i))
				if(left == null)
					left = v;
				else left = left.add(v);
			for(Variable v: p2v.get(i+1))
				if(left == null)
					left = new FloatConstant(-1).mult(v);
				else left = left.minus(v);
			problem.add(new Inequation(left,new IntegerConstant(0),Inequation.GREATER_EQUAL));
		}
		//System.out.println(problem);
		try {
			Map<Variable,Term> result = this.solver.solve(problem);
			//System.out.println(result);
			return problem.getTargetFunction().replaceAllTerms(result).doubleValue();
		} catch (GeneralMathException e) {
			throw new RuntimeException(e);			
		}		
	}
}