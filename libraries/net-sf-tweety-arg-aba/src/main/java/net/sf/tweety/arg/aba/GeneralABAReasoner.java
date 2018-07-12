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
 package net.sf.tweety.arg.aba;

import java.util.Collection;

import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.BeliefBaseReasoner;

/**
 * @author Nils Geilen <geilenn@uni-koblenz.de>
 * @author Matthias Thimm
 * This is an abstract gerneralization over non-flat ABA reasoners
 * @param <T>	the language of the underlying ABA theory
 */
public abstract class GeneralABAReasoner <T extends Formula> implements BeliefBaseReasoner<ABATheory<T>> {
	private int inferenceType;

	/**
	 * Creates a new general reasoner.
	 * @param inferenceType The inference type for this reasoner.
	 */
	public GeneralABAReasoner(int inferenceType) {
		this.inferenceType = inferenceType;
	}
	
	/**
	 * @return the inferenceType
	 */
	public int getInferenceType() {
		return inferenceType;
	}

	/**
	 * @param inferenceType the inferenceType to set
	 */
	public void setInferenceType(int inferenceType) {
		this.inferenceType = inferenceType;
	}

	public abstract Collection<Collection<Assumption<T>>> computeExtensions(ABATheory<T> beliefbase);

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefBaseReasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(ABATheory<T> beliefbase, Formula query) {
		Answer result = new Answer(beliefbase,query);
		Collection<Collection<Assumption<T>>> exts = computeExtensions(beliefbase);
		if(inferenceType == Semantics.CREDULOUS_INFERENCE) {
			result.setAnswer(false);
			for(Collection<Assumption<T>> ext:exts) {
				if(ext.contains(query)) {
					result.setAnswer(true);
					break;
				}
			}
		} else {
			result.setAnswer(true);
			for(Collection<Assumption<T>> ext:exts) {
				if(!ext.contains(query)) {
					result.setAnswer(false);
					break;
				}
			}
		}
		return result;	
	}
	
}
