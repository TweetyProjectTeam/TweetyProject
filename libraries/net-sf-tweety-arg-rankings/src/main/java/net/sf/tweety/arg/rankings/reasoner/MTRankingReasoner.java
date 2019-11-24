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
package net.sf.tweety.arg.rankings.reasoner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.rankings.semantics.NumericalArgumentRanking;
import net.sf.tweety.commons.util.SetTools;
import net.sf.tweety.math.equation.Equation;
import net.sf.tweety.math.equation.Inequation;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.solver.ApacheCommonsSimplex;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.Product;
import net.sf.tweety.math.term.Sum;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * This class implements the argument ranking approach of [Matt, Toni. A
 * game-theoretic measure of argument strength for abstract argumentation. JELIA
 * 2008]. In this approach, the strength of an argument is computed using a
 * two-person zero-sum strategic game in which the strategies of players are
 * sets of arguments.
 * 
 * @author Anna Gessler
 */
public class MTRankingReasoner extends AbstractRankingReasoner<NumericalArgumentRanking> {

	@Override
	public Collection<NumericalArgumentRanking> getModels(DungTheory bbase) {
		Collection<NumericalArgumentRanking> ranks = new HashSet<NumericalArgumentRanking>();
		ranks.add(this.getModel(bbase));
		return ranks;
	}

	@Override
	public NumericalArgumentRanking getModel(DungTheory kb) {
		NumericalArgumentRanking ranking = new NumericalArgumentRanking();
		ranking.setSortingType(NumericalArgumentRanking.SortingType.DESCENDING);
		Set<Set<Argument>> subsets = new SetTools<Argument>().subsets(kb.getNodes());
		for (Argument a : kb) 
			ranking.put(a, computeStrengthOfArgument(a, kb, subsets)); 
		return ranking;
	}

	/**
	 * Computes the value of the zero-sum game for the given argument.
	 * 
	 * @param a  an Argument
	 * @param kb DungTheory
	 * @param subsets all subsets of the knowledge base
	 * @return strength value of the given argument
	 */
	public double computeStrengthOfArgument(Argument a, DungTheory kb, Set<Set<Argument>> subsets) {
		/* 
		 * The value of the game is the solution to a linear optimization problem
		*/
		OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MAXIMIZE);
		Variable target_var = new FloatVariable("PMAX");
		problem.setTargetFunction(target_var);
		
		//Generate strategies of the proponent and opponent of the zero-sum strategic game
		Set<Collection<Argument>> proponent_strategies = new HashSet<Collection<Argument>>();
		Set<Collection<Argument>> opponent_strategies = new HashSet<Collection<Argument>>();
		for (Set<Argument> p : subsets) {
			opponent_strategies.add(p);
			if (p.contains(a)) 
				proponent_strategies.add(p);
		}

		/*
		 * Add linear inequality constraints to problem
		 * See [Matt, Toni. A game-theoretic measure of argument strength for abstract argumentation.
		 * JELIA 2008] for details
		 */
		List<FloatVariable> probability_variables = new ArrayList<FloatVariable>();
		for (int count = 1; count <= proponent_strategies.size(); count++) { 
			FloatVariable p_i = new FloatVariable("P" + count);
			probability_variables.add(p_i);
			problem.add(new Inequation(p_i, new FloatConstant(0.0), Inequation.GREATER_EQUAL));
		}
		problem.add(new Inequation(target_var, new FloatConstant(0.0), Inequation.GREATER_EQUAL));
		
		for (Collection<Argument> j : opponent_strategies) {
			List<Term> products = new ArrayList<Term>(); 
			int pi = 0;
			for (Collection<Argument> i : proponent_strategies) {
				FloatConstant rewards_ij = new FloatConstant(computeRewards(i, j, kb));
				products.add(new Product(rewards_ij, probability_variables.get(pi++)));
			}
			problem.add( new Inequation(new Sum(products).minus(target_var), new FloatConstant(0.0),
					Inequation.GREATER_EQUAL));
		}
		problem.add(new Equation(new Sum(probability_variables), new FloatConstant(1.0)));

		/*
		 * Solve problem with simplex algorithm
		 */
		ApacheCommonsSimplex solver = new ApacheCommonsSimplex();
		solver.onlyPositive = true;
		try {
			Map<Variable, Term> solution = solver.solve(problem);
			return solution.get(target_var).doubleValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1.0;
	}

	/**
	 * Computes the rewards of the given strategy (set of arguments).
	 * 
	 * @param A  set of arguments
	 * @param B  set of arguments
	 * @param kb knowledge base containing the relations between A and B
	 * @return rewards of A
	 */
	public double computeRewards(Collection<Argument> A, Collection<Argument> B, DungTheory kb) {
		if (kb.isAttacked(new Extension(A), new Extension(A)))
			return 0.0;
		else if (kb.isAttacked(new Extension(A), new Extension(B)))
			return computeDegreeOfAcceptability(A, B, kb);
		return 1.0;
	}

	/**
	 * Computes the degree of acceptability of the strategy A wrt. strategy B.
	 * 
	 * @param A  set of arguments
	 * @param B  set of arguments
	 * @param kb knowledge base containing the relations between A and B
	 * @return degree of acceptability of A wrt. B.
	 */
	public double computeDegreeOfAcceptability(Collection<Argument> A, Collection<Argument> B, DungTheory kb) {
		int attacks_from_A_to_B = 0;
		for (Argument b : B) {
			Set<Argument> attacks = kb.getAttackers(b);
			attacks.retainAll(A);
			attacks_from_A_to_B += attacks.size();
		}
		int attacks_from_B_to_A = 0;
		for (Argument a : A) {
			Set<Argument> attacks = kb.getAttackers(a);
			attacks.retainAll(B);
			attacks_from_B_to_A += attacks.size();
		}

		double result = 1.0;
		result += 1.0 - (1.0 / (attacks_from_A_to_B + 1.0));
		result -= 1.0 - (1.0 / (attacks_from_B_to_A + 1.0));
		return 0.5 * result;
	}

}