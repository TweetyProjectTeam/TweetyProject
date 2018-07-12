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
package net.sf.tweety.arg.prob.dynamics;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.prob.PartialProbabilityAssignment;
import net.sf.tweety.arg.prob.semantics.*;
import net.sf.tweety.commons.util.SetTools;
import net.sf.tweety.math.equation.Equation;
import net.sf.tweety.math.func.SimpleRealValuedFunction;
import net.sf.tweety.math.norm.RealVectorNorm;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.Term;

/**
 * Provides common functionality for change operators based on probabilistic semantics.
 * @author Matthias Thimm
 */
public abstract class AbstractPAChangeOperator implements ChangeOperator {

	/**
	 * Optimizations carried out by ancestors of this class perform a two-level optimization. First,
	 * one function is minimized and then a second one is maximized. In order to avoid solving two
	 * consecutive optimization problems both target functions are combined and the first one is
	 * weighted by this factor to give preference.
	 */
	protected final static long FIRST_OPTIMIZATION_WEIGHT = 1000;
	
	/** The semantics used for change. */
	private PASemantics semantics;
	/** The norm used for distance measurement between probabilistic extensions. */
	private RealVectorNorm norm;
	/** The function that is maximized on the set of probabilistic extensions with minimal distance. */
	private SimpleRealValuedFunction f;
	
	/**
	 * Creates a new change operator for the given semantics that uses the specified norm
	 * for distance measuring and the given function for optimizing.
	 * @param semantics the semantics used for change.
	 * @param norm the norm used for distance measurement between probabilistic extensions.
	 * @param f the function that is maximized on the set of probabilistic extensions with minimal distance. 
	 */
	public AbstractPAChangeOperator(PASemantics semantics, RealVectorNorm norm, SimpleRealValuedFunction f){
		this.semantics = semantics;
		this.norm = norm;
		this.f = f;
	}
	
	/** Returns the semantics.
	 * @return the semantics.
	 */
	protected PASemantics getSemantics(){
		return this.semantics;
	}
	
	/** Returns the norm.
	 * @return the norm.
	 */
	protected RealVectorNorm getNorm(){
		return this.norm;
	}
	
	/** Returns the function f.
	 * @return the function f.
	 */
	protected SimpleRealValuedFunction getFunction(){
		return this.f;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.prob.dynamics.ChangeOperator#change(net.sf.tweety.arg.prob.PartialProbabilityAssignment, net.sf.tweety.arg.dung.DungTheory)
	 */
	@Override
	public abstract ProbabilisticExtension change(PartialProbabilityAssignment ppa, DungTheory theory);

	protected void prepareOptimizationProblem(PartialProbabilityAssignment ppa, DungTheory theory, OptimizationProblem problem, Map<Collection<Argument>,FloatVariable> varsComp, Map<Collection<Argument>,FloatVariable> varsSem, Vector<Term> varsCompVector, Vector<Term> varsSemVector){
		Set<Set<Argument>> configurations = new SetTools<Argument>().subsets(theory);
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
		problem.addAll(this.semantics.getSatisfactionStatements(theory, varsSem));		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.prob.dynamics.ChangeOperator#change(net.sf.tweety.arg.prob.semantics.ProbabilisticExtension, net.sf.tweety.arg.dung.DungTheory)
	 */
	@Override
	public ProbabilisticExtension change(ProbabilisticExtension p, DungTheory theory){
		// TODO Auto-generated method stub
		return null;
	}

}
