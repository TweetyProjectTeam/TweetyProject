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
 * A MaxSAT-based implementation for solving the MaxAdm# problem from
 * [Skiba,Thimm; 2024]
 * @author Matthias Thimm
 *
 */
public class MaxSatKAdmissibleReasoner implements KOptimisationReasoner {

	private MaxSatSolver solver;
	
	/**
	 * @param solver the used MaxSAT solver
	 */
	public MaxSatKAdmissibleReasoner(MaxSatSolver solver) {
		this.solver = solver;
	}
	
	/**
	 * Returns the maximal k such that the given extension is a 
	 * k-adm# extension of the given AAF. Returns Integer.MIN_VALUE
	 * if the given set is not a k-adm# extension for any k (i.e., iff
	 * it is not conflict-free)
	 * @param aaf some AAF.
	 * @param set some set of arguments.
	 *  @return the maximal k such that the given extension is a 
	 * k-adm# extension of the given AAF.
	 */
	public static int eval(DungTheory aaf, Collection<Argument> set) {
		if(!aaf.isConflictFree(new Extension<DungTheory>(set)))				
			return Integer.MIN_VALUE;
		return aaf.getNumberOfNodes() - aaf.getUnattackedAttackers(set).size();
	}
	
	@Override
	public Integer query(DungTheory aaf, Argument arg) {
		// check trivial case, i.e., arg is a self-attacker
		if(aaf.isAttackedBy(arg, arg))
			return Integer.MIN_VALUE;		
		// in/out vars for each argument
		// and vars for representing that an argument is an unattacked attacker
		Map<Argument,Proposition> in = new HashMap<>();
		Map<Argument,Proposition> out = new HashMap<>();
		Map<Argument,Proposition> unatt = new HashMap<>();
		int idx = 0;
		for(Argument a: aaf) {
			in.put(a, new Proposition("in" + ++idx));
			out.put(a, new Proposition("out" + idx));
			unatt.put(a, new Proposition("unatt" + idx));
		}
		idx = 0;
		Map<Attack,Proposition> att_sat = new HashMap<>();
		for(Attack att: aaf.getAttacks()) {
			att_sat.put(att, new Proposition("att_sat" + ++idx));
		}
		// the hard clauses
		Collection<PlFormula> hard_clauses = new HashSet<>();
		// no argument can be in and out
		for(Argument a: aaf)
			hard_clauses.add(new Disjunction(new Negation(in.get(a)),new Negation(out.get(a))));
		// ensure conflict-freeness
		for(Attack att: aaf.getAttacks())
			hard_clauses.add(new Disjunction(new Negation(in.get(att.getAttacker())),new Negation(in.get(att.getAttacked()))));
		// if an argument is out then there must be an attacker that is in
		// (only that direction is enforced)
		for(Argument a: aaf) {
			Disjunction disj = new Disjunction();
			disj.add(new Negation(out.get(a)));
			for(Argument b: aaf.getAttackers(a)) {
				disj.add(in.get(b));		
			}
			hard_clauses.add(disj);
		}
		// argument arg is in
		hard_clauses.add(in.get(arg));		
		// model att_sat and unatt
		for(Attack att: aaf.getAttacks()) {
			hard_clauses.add(new Negation(att_sat.get(att)).combineWithOr(new Negation(in.get(att.getAttacked()))).combineWithOr(out.get(att.getAttacker())));
			hard_clauses.add(in.get(att.getAttacked()).combineWithOr(att_sat.get(att)));
			hard_clauses.add(new Negation(out.get(att.getAttacker())).combineWithOr(att_sat.get(att)));
			hard_clauses.add(att_sat.get(att).combineWithOr(new Negation(unatt.get(att.getAttacker()))));			
		}
		// the soft clauses (maximise the number of arguments b such that "if some attacked argument of b is in, then b is out")
		Map<PlFormula,Integer> soft_clauses = new HashMap<>();
		for(Argument b: aaf) {
			Disjunction d = new Disjunction();
			for(Argument a: aaf.getAttacked(b)) {
				d.add(new Negation(att_sat.get(new Attack(b,a))));
			}
			if(d.isEmpty())
				hard_clauses.add(unatt.get(b));
			else {
				d.add(unatt.get(b));
				hard_clauses.add(d);
			}				
			soft_clauses.put(unatt.get(b),1);			
		}		
		// solve
		Interpretation<PlBeliefSet,PlFormula> witness = this.solver.getWitness(hard_clauses,soft_clauses);
		return aaf.getNumberOfNodes() - MaxSatSolver.costOf(witness, hard_clauses, soft_clauses);
	}
}
