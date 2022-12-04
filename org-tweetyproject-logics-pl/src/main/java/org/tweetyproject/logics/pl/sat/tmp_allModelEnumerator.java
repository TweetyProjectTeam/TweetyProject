package org.tweetyproject.logics.pl.sat;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.tweetyproject.commons.Interpretation;
import org.tweetyproject.commons.InterpretationSet;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.logics.pl.syntax.Proposition;

public class tmp_allModelEnumerator {
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.sat.SatSolver#getWitness(java.util.Collection)
	 */

	public Set<InterpretationSet<Proposition,PlBeliefSet,PlFormula>> getWitness(Collection<PlFormula> formulas) {
		Collection<Disjunction> clauses = new HashSet<Disjunction>();
		// check if we are working with CNF; if not, convert
		for(PlFormula f: formulas) {
			if(f.isClause())
				clauses.add((Disjunction)f);
			else {
	 			f = f.toCnf();
				for(PlFormula c: ((Conjunction)f)) 
					clauses.add((Disjunction)c);				
			}
		}
		// we need some data structures
		// keeps track of the literals which are already assigned a truth value
		Stack<PlFormula> sel_literals = new Stack<PlFormula>();
		// keeps track of the atoms which have not assigned a truth value yet
		HashSet<Proposition> remaining_atoms = new HashSet<Proposition>();
		remaining_atoms.addAll(PlSignature.getSignature(clauses).toCollection());
		// for each literal on the "sel_literals" stack, the next stack says whether
		// this literal was selected to have its truth value (true),
		// or derived using unit propagation (false)
		Set<PossibleWorld> allModels = new HashSet<PossibleWorld>();
		Stack<Boolean> sel_literals_selected = new Stack<Boolean>();		
		for(PlFormula f : formulas)
			allModels.addAll(f.getModels());
		Set<InterpretationSet<Proposition, PlBeliefSet, PlFormula>> satModels = new HashSet<InterpretationSet<Proposition, PlBeliefSet, PlFormula>> ();
		for(PossibleWorld w : allModels) {
//			if(w.satisfies(formulas)) {
				satModels.add(w);
//			}
		}
		return satModels;
	}

}
