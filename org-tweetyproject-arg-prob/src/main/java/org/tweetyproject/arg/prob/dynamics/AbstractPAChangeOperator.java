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
package org.tweetyproject.arg.prob.dynamics;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.prob.semantics.*;
import org.tweetyproject.arg.prob.syntax.PartialProbabilityAssignment;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.math.equation.Equation;
import org.tweetyproject.math.func.SimpleRealValuedFunction;
import org.tweetyproject.math.norm.RealVectorNorm;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.FloatVariable;
import org.tweetyproject.math.term.Term;

/**
 * Provides common functionality for change operators based on probabilistic
 * semantics.
 *
 * @author Matthias Thimm
 */
public abstract class AbstractPAChangeOperator implements ChangeOperator {

	/**
	 * Optimizations carried out by ancestors of this class perform a two-level
	 * optimization. First,
	 * one function is minimized and then a second one is maximized. In order to
	 * avoid solving two
	 * consecutive optimization problems both target functions are combined and the
	 * first one is
	 * weighted by this factor to give preference.
	 */
	protected final static long FIRST_OPTIMIZATION_WEIGHT = 1000;

	/** The semantics used for change. */
	private PASemantics semantics;
	/** The norm used for distance measurement between probabilistic extensions. */
	private RealVectorNorm norm;
	/**
	 * The function that is maximized on the set of probabilistic extensions with
	 * minimal distance.
	 */
	private SimpleRealValuedFunction f;

	/**
	 * Creates a new change operator for the given semantics that uses the specified
	 * norm
	 * for distance measuring and the given function for optimizing.
	 *
	 * @param semantics the semantics used for change.
	 * @param norm      the norm used for distance measurement between probabilistic
	 *                  extensions.
	 * @param f         the function that is maximized on the set of probabilistic
	 *                  extensions with minimal distance.
	 */
	public AbstractPAChangeOperator(PASemantics semantics, RealVectorNorm norm, SimpleRealValuedFunction f) {
		this.semantics = semantics;
		this.norm = norm;
		this.f = f;
	}

	/**
	 * Returns the semantics.
	 *
	 * @return the semantics.
	 */
	protected PASemantics getSemantics() {
		return this.semantics;
	}

	/**
	 * Returns the norm.
	 *
	 * @return the norm.
	 */
	protected RealVectorNorm getNorm() {
		return this.norm;
	}

	/**
	 * Returns the function f.
	 *
	 * @return the function f.
	 */
	protected SimpleRealValuedFunction getFunction() {
		return this.f;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.tweetyproject.arg.prob.dynamics.ChangeOperator#change(org.tweetyproject.
	 * arg.prob.PartialProbabilityAssignment, org.tweetyproject.arg.dung.DungTheory)
	 */
	@Override
	public abstract ProbabilisticExtension change(PartialProbabilityAssignment ppa, DungTheory theory);

	/**
	 * Prepares an optimization problem for computing probability distributions over
	 * extensions
	 * in a Dung argumentation framework, constrained by a partial probability
	 * assignment (PPA) and
	 * a specific argumentation semantics. This method constructs and adds
	 * constraints to the
	 * optimization problem based on the theory, the partial probability assignment,
	 * and the chosen
	 * semantics.
	 *
	 * @param ppa            The partial probability assignment mapping arguments to
	 *                       probabilities.
	 * @param theory         The Dung theory (argumentation framework) being
	 *                       analyzed.
	 * @param problem        The optimization problem to which constraints will be
	 *                       added.
	 * @param varsComp       A mapping of argument subsets (configurations) to their
	 *                       corresponding
	 *                       floating-point variables representing the probability
	 *                       components.
	 * @param varsSem        A mapping of argument subsets (configurations) to their
	 *                       corresponding
	 *                       floating-point variables representing the probability
	 *                       according to the semantics.
	 * @param varsCompVector A vector storing the floating-point variables for the
	 *                       probability components.
	 * @param varsSemVector  A vector storing the floating-point variables for the
	 *                       probability semantics.
	 */
	protected void prepareOptimizationProblem(PartialProbabilityAssignment ppa, DungTheory theory,
			OptimizationProblem problem, Map<Collection<Argument>, FloatVariable> varsComp,
			Map<Collection<Argument>, FloatVariable> varsSem, Vector<Term> varsCompVector, Vector<Term> varsSemVector) {
		Set<Set<Argument>> configurations = new SetTools<Argument>().subsets(theory);
		Term normConstraintComp = null;
		Term normConstraintSem = null;
		for (Set<Argument> w : configurations) {
			FloatVariable varComp = new FloatVariable("c" + w.toString(), 0, 1);
			FloatVariable varSem = new FloatVariable("s" + w.toString(), 0, 1);
			varsComp.put(w, varComp);
			varsSem.put(w, varSem);
			varsCompVector.add(varComp);
			varsSemVector.add(varSem);
			if (normConstraintComp == null)
				normConstraintComp = varComp;
			else
				normConstraintComp = normConstraintComp.add(varComp);
			if (normConstraintSem == null)
				normConstraintSem = varSem;
			else
				normConstraintSem = normConstraintSem.add(varSem);
		}
		problem.add(new Equation(normConstraintComp, new FloatConstant(1)));
		problem.add(new Equation(normConstraintSem, new FloatConstant(1)));
		// add constraints from partial probability assignment
		for (Argument arg : ppa.keySet()) {
			Term leftSide = new FloatConstant(ppa.get(arg).doubleValue());
			Term rightSide = null;
			for (Set<Argument> set : configurations)
				if (set.contains(arg))
					if (rightSide == null)
						rightSide = varsComp.get(set);
					else
						rightSide = rightSide.add(varsComp.get(set));
			problem.add(new Equation(leftSide, rightSide));
		}
		// add constraints imposed by semantics
		problem.addAll(this.semantics.getSatisfactionStatements(theory, varsSem));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.tweetyproject.arg.prob.dynamics.ChangeOperator#change(org.tweetyproject.
	 * arg.prob.semantics.ProbabilisticExtension,
	 * org.tweetyproject.arg.dung.DungTheory)
	 */
	@Override
	public ProbabilisticExtension change(ProbabilisticExtension p, DungTheory theory) {
		// TODO Auto-generated method stub
		return null;
	}

}
