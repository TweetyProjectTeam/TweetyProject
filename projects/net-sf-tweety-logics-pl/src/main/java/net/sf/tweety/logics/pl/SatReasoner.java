package net.sf.tweety.logics.pl;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Reasoner;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * Uses the default SAT reasoner to perform reasoning in propositional logic
 * @author Matthias Thimm
 */
public class SatReasoner extends Reasoner {

	/**
	 * Creates a new reasoner for the given belief base.
	 * @param beliefBase
	 */
	public SatReasoner(BeliefBase beliefBase) {
		super(beliefBase);
		if(!(beliefBase instanceof PlBeliefSet))
			throw new IllegalArgumentException("Instance of class PlBeliefSet expected.");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Reasoner#query(net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(Formula query) {
		Collection<PropositionalFormula> formulas = new HashSet<PropositionalFormula>();
		formulas.add(new Negation((PropositionalFormula)query));
		Answer ans = new Answer(this.getKnowledgBase(), query);
		ans.setAnswer(!SatSolver.getDefaultSolver().isConsistent(formulas));
		return ans;
	}

}
