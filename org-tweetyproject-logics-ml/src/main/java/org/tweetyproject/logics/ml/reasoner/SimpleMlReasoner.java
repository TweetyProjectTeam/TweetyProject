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
package org.tweetyproject.logics.ml.reasoner;

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.commons.Interpretation;
import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.logics.fol.syntax.FolBeliefSet;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.ml.semantics.AccessibilityRelation;
import org.tweetyproject.logics.ml.semantics.KripkeModel;
import org.tweetyproject.logics.ml.semantics.MlHerbrandBase;
import org.tweetyproject.logics.ml.semantics.MlHerbrandInterpretation;
import org.tweetyproject.logics.ml.syntax.MlBeliefSet;

/**
 * This class implements inference for modal logic using a brute-force approach.
 * A query, i.e. a formula in modal logic, can be inferred by a knowledge base,
 * iff every Kripke model of the knowledge base is also a Kripke model of the query.
 *
 * @author Anna Gessler
 * @author Matthias Thimm
 */

public class SimpleMlReasoner extends AbstractMlReasoner {
	/**
	 * Default Constructor
	 */
	public SimpleMlReasoner(){
		super();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.ml.reasoner.ModalReasoner#query(org.tweetyproject.logics.ml.syntax.ModalBeliefSet, org.tweetyproject.logics.fol.syntax.FolFormula)
	 */
	@Override
	public Boolean query(MlBeliefSet mbs, FolFormula formula) {
		if(!formula.isWellFormed())
			throw new IllegalArgumentException("The given formula " + formula + " is not well-formed.");
		if(!formula.isClosed())
			throw new IllegalArgumentException("The given formula " + formula + " is not closed.");

		//A Kripke model consists of a set of worlds and an accessibility relation that defines which of those worlds are accessible to each other.
		//To construct all possible Kripke models for the knowledge base, we need to find all possible sets of worlds for the knowledge base
		//and all possible accessibility relations for each of those sets.
		FolSignature sig = new FolSignature();
		sig.addSignature(mbs.getMinimalSignature());
		sig.addSignature(formula.getSignature());
		MlHerbrandBase hBase = new MlHerbrandBase(sig);
		Set<MlHerbrandInterpretation> possibleWorlds = hBase.getAllHerbrandInterpretations();
		Set<Set<MlHerbrandInterpretation>> possibleWorldsCombinations = new SetTools<MlHerbrandInterpretation>().subsets(possibleWorlds);

		//For each set of worlds: Get all possible binary combinations of worlds to construct all possible accessibility relations
		Set<KripkeModel> kripkeModels = new HashSet<KripkeModel>();
		for (Set<MlHerbrandInterpretation> possibleWorldCombination: possibleWorldsCombinations) {
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

	@Override
	public boolean isInstalled() {
		return true;
	}
}
