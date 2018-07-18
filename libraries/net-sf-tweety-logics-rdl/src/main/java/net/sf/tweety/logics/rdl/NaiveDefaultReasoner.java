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
package net.sf.tweety.logics.rdl;

import java.util.Collection;

import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.BeliefBaseReasoner;
import net.sf.tweety.logics.fol.prover.FolTheoremProver;
import net.sf.tweety.logics.fol.syntax.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.rdl.semantics.DefaultProcessTree;

/**
 * Implements a naive reasoner for default logic based on exhaustive 
 * application of defaults in process trees.
 * 
 * @author Matthias Thimm, Nils Geilen
 */
public class NaiveDefaultReasoner implements BeliefBaseReasoner<DefaultTheory>{

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefBaseReasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(DefaultTheory theory, Formula query) {
		if(!(query instanceof FolFormula))
			throw new IllegalArgumentException("NaiveDefaultReasoner is only defined for first-order queries.");
		if(!((FolFormula)query).isGround())
			throw new IllegalArgumentException("Query is not grounded.");
		Answer answer = new Answer(theory,query);
		answer.setAnswer(false);
		for (Collection<FolFormula> extension: this.getAllExtensions(theory)){
			FolBeliefSet fbs = (FolBeliefSet)extension;
			FolTheoremProver prover = FolTheoremProver.getDefaultProver();
			if(prover.query(fbs, (FolFormula)query).getAnswerBoolean()){
				answer.setAnswer(true);
				break;
			}
		}
		return answer;
	}
	
	/**
	 * 	@return all extensions of the default theory
	 */
	public Collection<Collection<FolFormula>> getAllExtensions(DefaultTheory theory){
		return new DefaultProcessTree(theory).getExtensions();
	}
	
}
