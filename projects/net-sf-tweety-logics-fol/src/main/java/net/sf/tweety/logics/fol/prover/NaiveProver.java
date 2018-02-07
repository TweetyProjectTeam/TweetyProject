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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.fol.prover;

import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.logics.fol.ClassicalInference;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;

/**
 * Uses a naive brute force search procedure for theorem proving.
 * @author Matthias Thimm
 *
 */
public class NaiveProver extends FolTheoremProver {

	public NaiveProver(BeliefBase beliefBase) {
		super(beliefBase);
	}
	
	public NaiveProver() {
		super(new FolBeliefSet());
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.prover.FolTheoremProver#query(net.sf.tweety.logics.fol.FolBeliefSet, net.sf.tweety.logics.fol.syntax.FolFormula)
	 */
	@Override
	public Answer query(Formula query) {
		ClassicalInference inf = new ClassicalInference(this.getKnowledgeBase());
		return inf.query(query);
	}

	@Override
	public boolean equivalent(FolBeliefSet kb, FolFormula a, FolFormula b) {
		return ClassicalInference.equivalent(a, b);
	}

	
	
}
