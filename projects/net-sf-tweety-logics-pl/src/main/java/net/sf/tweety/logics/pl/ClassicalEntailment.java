package net.sf.tweety.logics.pl;

import java.util.*;

import net.sf.tweety.*;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.*;

/**
 * This class implements classical entailment for propositional logic.
 * 
 * @author Matthias Thimm
 */
public class ClassicalEntailment extends EntailmentRelation<PropositionalFormula> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.EntailmentRelation#entails(java.util.Collection, net.sf.tweety.Formula)
	 */
	@Override
	public boolean entails(Collection<PropositionalFormula> formulas, PropositionalFormula formula) {
		PropositionalSignature signature = new PropositionalSignature();
		for(PropositionalFormula f: formulas)
			signature.addAll(f.getAtoms());
		signature.addAll(formula.getAtoms());
		Set<PossibleWorld> possibleWorlds = PossibleWorld.getAllPossibleWorlds(signature);
		for(PossibleWorld w: possibleWorlds)
			if(w.satisfies(formulas))
				if(!w.satisfies(formula))
					return false;
		return true;		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.EntailmentRelation#isConsistent(java.util.Collection)
	 */
	@Override
	public boolean isConsistent(Collection<PropositionalFormula> formulas) {
		return !this.entails(formulas, new Contradiction());
	}

}
