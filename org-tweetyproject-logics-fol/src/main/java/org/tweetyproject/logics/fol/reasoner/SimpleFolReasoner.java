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
package org.tweetyproject.logics.fol.reasoner;

import java.util.Set;

import org.tweetyproject.logics.fol.semantics.HerbrandBase;
import org.tweetyproject.logics.fol.semantics.HerbrandInterpretation;
import org.tweetyproject.logics.fol.syntax.FolBeliefSet;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.fol.syntax.ForallQuantifiedFormula;

/**
 * Uses a naive brute force search procedure for theorem proving.
 *
 * @author Matthias Thimm
 */
public class SimpleFolReasoner extends FolReasoner {

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.fol.reasoner.FolReasoner#query(org.tweetyproject.logics.fol.syntax.FolBeliefSet, org.tweetyproject.logics.fol.syntax.FolFormula)
	 */
	@Override
	public Boolean query(FolBeliefSet kb, FolFormula formula) {
		if(!formula.isWellFormed())
			throw new IllegalArgumentException("The given formula " + formula + " is not well-formed.");
		if(!formula.isClosed())
			throw new IllegalArgumentException("The given formula " + formula + " is not closed.");
		FolSignature sig = new FolSignature();
		sig.addSignature(kb.getMinimalSignature());
		sig.addSignature(formula.getSignature());
		HerbrandBase hBase = new HerbrandBase(sig);
		Set<HerbrandInterpretation> interpretations = hBase.getAllHerbrandInterpretations();
		for(HerbrandInterpretation i: interpretations)
			if(i.satisfies(kb))
				if(!i.satisfies(formula))
					return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.fol.reasoner.FolTheoremProver#equivalent(org.tweetyproject.logics.fol.syntax.FolBeliefSet, org.tweetyproject.logics.fol.syntax.FolFormula, org.tweetyproject.logics.fol.syntax.FolFormula)
	 */
	@Override
	public boolean equivalent(FolBeliefSet kb, FolFormula f1, FolFormula f2) {
		FolSignature sig = new FolSignature();
		sig.addSignature(f1.getSignature());
		sig.addSignature(f2.getSignature());
		HerbrandBase hBase = new HerbrandBase(sig);
		FolFormula f = f1.combineWithAnd(f2).combineWithOr(f1.complement().combineWithAnd(f2.complement()));
		if(!f.getUnboundVariables().isEmpty())
			f = new ForallQuantifiedFormula(f, f.getUnboundVariables());
		for(HerbrandInterpretation i: hBase.getAllHerbrandInterpretations())
			if(i.satisfies(kb))
				if(!i.satisfies(f))
					return false;
		return true;
	}
	@Override
	public boolean isInstalled() {
		return true;
	}
}
