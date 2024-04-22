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
 * A MaxSAT-based implementation for solving the MaxStable* problem from
 * [Thimm. Optimisation and Approximation in Abstract Argumentation: The Case of Stable Semantics. IJCAI 2024]
 * @author Matthias Thimm
 *
 */
public class MaxSatKStableAstReasoner implements KOptimisationReasoner {

	private MaxSatSolver solver;
	
	/**
	 * @param solver some MaxSat solver
	 */
	public MaxSatKStableAstReasoner(MaxSatSolver solver) {
		this.solver = solver;
	}
	
	/**
	 * Returns the maximal k such that the given extension is a 
	 * k-stable* extension of the given AAF. Returns Integer.MIN_VALUE
	 * if the given set is not a k-stable* extension for any k (i.e., iff
	 * it does not have full range)
	 * @param aaf some AAF.
	 * @param set some set of arguments.
	 * @return the maximal k such that the given extension is a 
	 * k-stable* extension of the given AAF.
	 */
	public static int eval(DungTheory aaf, Set<Argument> set) {
		Set<Argument> s =new HashSet<Argument>(set);
		s.addAll(aaf.getAttacked(set));
		if(aaf.getNumberOfNodes() != s.size())				
			return Integer.MIN_VALUE;
		int k = 0;
		for (Attack attack: aaf.getAttacks())
			if(!set.contains(attack.getAttacked()) || !set.contains(attack.getAttacker()))
				k++;
		return k;
	}
	
	@Override
	public Integer query(DungTheory aaf, Argument arg) {
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
		// every argument must be in or out (=full range)
		for(Argument a: aaf)
			hard_clauses.add(new Disjunction(in.get(a),out.get(a)));
		// arguments are only out if an attacker is in (here we need both directions)
		for(Argument a: aaf) {
			Disjunction disj = new Disjunction();
			disj.add(new Negation(out.get(a)));
			for(Argument b: aaf.getAttackers(a)) {
				disj.add(in.get(b));
				hard_clauses.add(new Disjunction(new Negation(in.get(b)),out.get(a)));
			}
			hard_clauses.add(disj);
		}			
		// argument arg is in
		hard_clauses.add(in.get(arg));		
		// the soft clauses (minimise conflicts)
		Map<PlFormula,Integer> soft_clauses = new HashMap<>();
		int i = 0;
		for(Attack att: aaf.getAttacks()) {
			Proposition p = new Proposition("att" + ++i);
			hard_clauses.add(new Negation(in.get(att.getAttacker())).combineWithOr(new Negation(in.get(att.getAttacked()))).combineWithOr(p));
			soft_clauses.put(new Negation(p), 1);
		}		
		// solve
		Interpretation<PlBeliefSet,PlFormula> witness = this.solver.getWitness(hard_clauses,soft_clauses);
		return aaf.getNumberOfEdges() - MaxSatSolver.costOf(witness, hard_clauses, soft_clauses);
	}

}
