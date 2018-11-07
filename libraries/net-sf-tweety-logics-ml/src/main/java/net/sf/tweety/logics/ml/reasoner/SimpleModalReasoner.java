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
package net.sf.tweety.logics.ml.reasoner;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.commons.util.SetTools;
import net.sf.tweety.logics.fol.syntax.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.ml.semantics.AccessibilityRelation;
import net.sf.tweety.logics.ml.semantics.KripkeModel;
import net.sf.tweety.logics.ml.semantics.ModalHerbrandBase;
import net.sf.tweety.logics.ml.semantics.ModalHerbrandInterpretation;
import net.sf.tweety.logics.ml.syntax.ModalBeliefSet;

/**
 * This class implements inference for modal logic using a brute-force approach.
 * A query, i.e. a formula in modal logic, can be inferred by a knowledge base, 
 * iff every Kripke model of the knowledge base is also a Kripke model of the query.
 * 
 * @author Anna Gessler
 * @author Matthias Thimm
 */

public class SimpleModalReasoner extends AbstractModalReasoner {
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.ml.reasoner.ModalReasoner#query(net.sf.tweety.logics.ml.syntax.ModalBeliefSet, net.sf.tweety.logics.fol.syntax.FolFormula)
	 */
	@Override
	public Boolean query(ModalBeliefSet mbs, FolFormula formula) {
		if(!formula.isWellFormed())
			throw new IllegalArgumentException("The given formula " + formula + " is not well-formed.");
		if(!formula.isClosed())
			throw new IllegalArgumentException("The given formula " + formula + " is not closed.");	

		
		//A Kripke model consists of a set of worlds and an accessibility relation that defines which of those worlds are accessible to each other.
		//To construct all possible Kripke models for the knowledge base, we need to find all possible sets of worlds for the knowledge base
		//and all possible accessibility relations for each of those sets.
		FolSignature sig = new FolSignature();
		sig.addSignature(mbs.getSignature());
		sig.addSignature(formula.getSignature());
		ModalHerbrandBase hBase = new ModalHerbrandBase(sig);
		Set<ModalHerbrandInterpretation> possibleWorlds = hBase.getAllHerbrandInterpretations(); 
		Set<Set<ModalHerbrandInterpretation>> possibleWorldsCombinations = new SetTools<ModalHerbrandInterpretation>().subsets(possibleWorlds); 

		//For each set of worlds: Get all possible binary combinations of worlds to construct all possible accessibility relations
		Set<KripkeModel> kripkeModels = new HashSet<KripkeModel>();
		for (Set<ModalHerbrandInterpretation> possibleWorldCombination: possibleWorldsCombinations) {
			Set<Pair<Interpretation<FolBeliefSet,FolFormula>,Interpretation<FolBeliefSet,FolFormula>>> setOfPairs = new HashSet<Pair<Interpretation<FolBeliefSet,FolFormula>,Interpretation<FolBeliefSet,FolFormula>>>();
			for (Interpretation<FolBeliefSet,FolFormula> i: possibleWorldCombination) {	
				for (Interpretation<FolBeliefSet,FolFormula> i2: possibleWorldCombination) {
					Pair<Interpretation<FolBeliefSet,FolFormula>,Interpretation<FolBeliefSet,FolFormula>> p = new Pair<Interpretation<FolBeliefSet,FolFormula>,Interpretation<FolBeliefSet,FolFormula>>(i,i2);
					setOfPairs.add(p); 
				}
			}
			Set<Set<Pair<Interpretation<FolBeliefSet,FolFormula>, Interpretation<FolBeliefSet,FolFormula>>>> setOfPairsSubsets  = new SetTools<Pair<Interpretation<FolBeliefSet,FolFormula>,Interpretation<FolBeliefSet,FolFormula>>>().subsets(setOfPairs);
			for (Set<Pair<Interpretation<FolBeliefSet,FolFormula>, Interpretation<FolBeliefSet,FolFormula>>> p : setOfPairsSubsets) {
				AccessibilityRelation ar = new AccessibilityRelation(p);
				KripkeModel m = new KripkeModel(possibleWorldCombination, ar); //Construct a Kripke model for each possible accessibility relation for each possible set of worlds
				kripkeModels.add(m);
			}
		}
		
		//Test if every Kripke model for the knowledge base is also a Kripke model for the formula
		for (KripkeModel k: kripkeModels)
			if (k.satisfies(mbs))
				if (!(k.satisfies((FolFormula) formula)))
					return false;
		return true;	
	}
}
