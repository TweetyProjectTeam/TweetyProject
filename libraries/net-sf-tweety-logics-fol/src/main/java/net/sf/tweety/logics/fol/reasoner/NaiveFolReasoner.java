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
package net.sf.tweety.logics.fol.reasoner;

import java.util.Set;

import net.sf.tweety.logics.fol.semantics.HerbrandBase;
import net.sf.tweety.logics.fol.semantics.HerbrandInterpretation;
import net.sf.tweety.logics.fol.syntax.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.syntax.ForallQuantifiedFormula;

/**
 * Uses a naive brute force search procedure for theorem proving.
 * @author Matthias Thimm
 *
 */
public class NaiveFolReasoner extends FolReasoner {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.reasoner.FolReasoner#query(net.sf.tweety.logics.fol.syntax.FolBeliefSet, net.sf.tweety.logics.fol.syntax.FolFormula)
	 */
	@Override
	public Boolean query(FolBeliefSet kb, FolFormula formula) {
		if(!formula.isWellFormed())
			throw new IllegalArgumentException("The given formula " + formula + " is not well-formed.");
		if(!formula.isClosed())
			throw new IllegalArgumentException("The given formula " + formula + " is not closed.");		
		FolSignature sig = new FolSignature();
		sig.addSignature(kb.getSignature());
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
	 * @see net.sf.tweety.logics.fol.reasoner.FolTheoremProver#equivalent(net.sf.tweety.logics.fol.syntax.FolBeliefSet, net.sf.tweety.logics.fol.syntax.FolFormula, net.sf.tweety.logics.fol.syntax.FolFormula)
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
}
