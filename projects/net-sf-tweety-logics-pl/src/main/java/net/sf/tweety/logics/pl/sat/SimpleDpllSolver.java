/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016-2018 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.pl.sat;

import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * This class provides a simple reference implementation of  the DPLL (Davis–Putnam–Logemann–Loveland)
 * algorithm for satisfiability testing, see e.g <a href="https://en.wikipedia.org/wiki/DPLL_algorithm">https://en.wikipedia.org/wiki/DPLL_algorithm</a>.
 * 
 * The order of the variables is simply taken by the standard iterator of the induced signature. Only
 * unit propagation is used for satisfiability testing.
 * 
 * @author Matthias Thimm
 */
public class SimpleDpllSolver extends SatSolver {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.sat.SatSolver#getWitness(java.util.Collection)
	 */
	@Override
	public Interpretation getWitness(Collection<PropositionalFormula> formulas) {
		Collection<Disjunction> clauses = new HashSet<Disjunction>();
		// check if we are working with CNF; if not, convert
		for(PropositionalFormula f: formulas) {
			if(f.isClause())
				clauses.add((Disjunction)f);
			else {
				f = f.toCnf();
				for(PropositionalFormula c: ((Conjunction)f)) 
					clauses.add((Disjunction)c);				
			}
		}
		// we need some data structures
		// keeps track of the literals which are already assigned a truth value
		Stack<PropositionalFormula> sel_literals = new Stack<PropositionalFormula>();
		// keeps track of the atoms which have not assigned a truth value yet
		Stack<Proposition> remaining_atoms = new Stack<Proposition>();
		remaining_atoms.addAll(PropositionalSignature.getSignature(clauses));
		// for each literal on the "sel_literals" stack, the next stack says whether
		// this literal was selected to have its truth value (true),
		// or derived using unit propagation (false)
		Stack<Boolean> sel_literals_selected = new Stack<Boolean>();		
		while(!remaining_atoms.isEmpty()) {
			Proposition var = remaining_atoms.pop();
			sel_literals.push(var);
			sel_literals_selected.push(true);			
			// check for unit clauses
			boolean foundUnit = true;
			while(foundUnit) {
				foundUnit = false;
				for(Disjunction clause: clauses) {
					boolean isSatisfied = false;
					boolean isContradiction = true;
					boolean isUnit = false;
					PropositionalFormula unitLiteral = null;
					for(PropositionalFormula lit: clause) {
						if(sel_literals.contains(lit)) {
							isSatisfied = true;
							isContradiction = false;
							break;
						}
						if(!sel_literals.contains(lit.complement())) {
							isContradiction = false;
							if(unitLiteral == null) {
								unitLiteral = lit;
								isUnit = true;
							}else {
								isUnit = false;
							}
						}
					}					
					if(!isSatisfied && isUnit) {
						foundUnit = true;
						sel_literals.push(unitLiteral);
						sel_literals_selected.push(false);
						if(unitLiteral instanceof Negation)
							remaining_atoms.remove(((Negation)unitLiteral).getFormula());
						else remaining_atoms.remove(unitLiteral);
						break;
					}
					if(isContradiction) {
						// do backtracking
						while(!sel_literals.isEmpty()) {
							PropositionalFormula p = sel_literals.pop();
							if(p instanceof Negation) {
								remaining_atoms.push((Proposition) ((Negation)p).getFormula());
								sel_literals_selected.pop();
							}else if(sel_literals_selected.pop()) {
								sel_literals_selected.push(true);
								sel_literals.push(new Negation(p));
								break;
							}else {
								remaining_atoms.push((Proposition) p);
							}
						}
						// if the stack sel_literals is empty now, the set of clauses
						// are unsatisfiable
						if(sel_literals.isEmpty())
							return null;
						// check all clauses again
						foundUnit = true;
						break;
					}
				}			
			}
		}
		// we found a witness
		PossibleWorld w = new PossibleWorld();
		for(PropositionalFormula p: sel_literals)
			if(p instanceof Proposition)
				w.add((Proposition)p);
		return w;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.sat.SatSolver#isSatisfiable(java.util.Collection)
	 */
	@Override
	public boolean isSatisfiable(Collection<PropositionalFormula> formulas) {
		return this.getWitness(formulas) != null;
	}

}
