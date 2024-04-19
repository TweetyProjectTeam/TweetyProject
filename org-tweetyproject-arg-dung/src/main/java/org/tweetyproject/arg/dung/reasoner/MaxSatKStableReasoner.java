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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.Interpretation;
import org.tweetyproject.logics.pl.sat.MaxSatSolver;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * A MaxSAT-based implementation for solving the MaxStable problem from
 * [Thimm. Optimisation and Approximation in Abstract Argumentation: The Case of Stable Semantics. IJCAI 2024]
 * @author Matthias Thimm
 *
 */
public class MaxSatKStableReasoner implements KOptimisationReasoner {

	private MaxSatSolver solver;
	
	/**
	 * @param solver the used MaxSAT solver
	 */
	public MaxSatKStableReasoner(MaxSatSolver solver) {
		this.solver = solver;
	}
	
	/**
	 * Returns the maximal k such that the given extension is a 
	 * k-stable extension of the given AAF. Returns Integer.MIN_VALUE
	 * if the given set is not a k-stable extension for any k (i.e., iff
	 * it is not conflict-free)
	 * @param aaf some AAF.
	 * @param set some set of arguments.
	 * @return the maximal k such that the given extension is a 
	 * k-stable extension of the given AAF.
	 */
	public static int eval(DungTheory aaf, Set<Argument> set) {
		if(!aaf.isConflictFree(new Extension<DungTheory>(set)))				
			return Integer.MIN_VALUE;
		Set<Argument> s =new HashSet<Argument>(set);
		s.addAll(aaf.getAttacked(set));
		return s.size();
	}
	
	@Override
	public Integer query(DungTheory aaf, Argument arg) {
		// check trivial case, i.e., arg is a self-attacker
		if(aaf.isAttackedBy(arg, arg))
			return Integer.MIN_VALUE;		
		// in/out vars for each argument
		Map<Argument,Proposition> in = new HashMap<>();
		Map<Argument,Proposition> out = new HashMap<>();
		int idx = 0;
		for(Argument a: aaf) {
			in.put(a, new Proposition("in" + ++idx));
			out.put(a, new Proposition("out" + idx));
		}
		// the hard clauses
		Collection<PlFormula> hard_clauses = new HashSet<>();
		// no argument can be in and out
		for(Argument a: aaf)
			hard_clauses.add(new Disjunction(new Negation(in.get(a)),new Negation(out.get(a))));
		// ensure conflict-freeness
		for(Attack att: aaf.getAttacks())
			hard_clauses.add(new Disjunction(new Negation(in.get(att.getAttacker())),new Negation(in.get(att.getAttacked()))));
		// arguments are only out if an attacker is in (only the => part is required, the <= part is handled by maximisation)
		for(Argument a: aaf) {
			Disjunction disj = new Disjunction();
			disj.add(new Negation(out.get(a)));
			for(Argument b: aaf.getAttackers(a))
				disj.add(in.get(b));
			hard_clauses.add(disj);
		}
		// argument arg is in
		hard_clauses.add(in.get(arg));		
		// the soft clauses (maximise range)
		Map<PlFormula,Integer> soft_clauses = new HashMap<>();
		for(Argument a: aaf)
			soft_clauses.put(new Disjunction(in.get(a),out.get(a)),1);
		// solve
		Interpretation<PlBeliefSet,PlFormula> witness = this.solver.getWitness(hard_clauses,soft_clauses);
		return aaf.getNumberOfNodes() - MaxSatSolver.costOf(witness, hard_clauses, soft_clauses);
	}

}
