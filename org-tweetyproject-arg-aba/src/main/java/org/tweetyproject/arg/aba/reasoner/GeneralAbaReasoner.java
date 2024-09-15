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
package org.tweetyproject.arg.aba.reasoner;

import java.util.Collection;

import org.tweetyproject.arg.aba.semantics.AbaExtension;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.arg.aba.syntax.Assumption;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.QualitativeReasoner;

/**
 * This is an abstract generalization over non-flat ABA reasoners.
 *
 * @param <T> the language of the underlying ABA theory
 * @author Nils Geilen (geilenn@uni-koblenz.de)
 * @author Matthias Thimm
 */
public abstract class GeneralAbaReasoner<T extends Formula> implements QualitativeReasoner<AbaTheory<T>, Assumption<T>>,
		ModelProvider<Assumption<T>, AbaTheory<T>, AbaExtension<T>> {
	/** Default */
	public GeneralAbaReasoner() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.tweetyproject.commons.QualitativeReasoner#query(org.tweetyproject.commons
	 * .BeliefBase, org.tweetyproject.commons.Formula)
	 */
	@Override
	public Boolean query(AbaTheory<T> beliefbase, Assumption<T> query) {
		return this.query(beliefbase, query, InferenceMode.SKEPTICAL);
	}

	/**
	 * Queries the given ABA theory for the given assumption using the given
	 * inference type.
	 *
	 * @param beliefbase    an ABA theory
	 * @param query         some assumption
	 * @param inferenceMode either InferenceMode.SKEPTICAL or
	 *                      InferenceMode.CREDULOUS
	 * @return "true" if the query is accepted
	 */
	public Boolean query(AbaTheory<T> beliefbase, Assumption<T> query, InferenceMode inferenceMode) {
		Collection<AbaExtension<T>> extensions = this.getModels(beliefbase);
		if (inferenceMode.equals(InferenceMode.SKEPTICAL)) {
			for (AbaExtension<T> e : extensions)
				if (!e.contains(query))
					return false;
			return true;
		}
		// so its credulous semantics
		for (AbaExtension<T> e : extensions) {
			if (e.contains(query))
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.tweetyproject.commons.ModelProvider#getModels(org.tweetyproject.commons.
	 * BeliefBase)
	 */
	@Override
	public abstract Collection<AbaExtension<T>> getModels(AbaTheory<T> bbase);

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.arg.aba.reasoner.GeneralABAReasoner#getModel(org.
	 * tweetyproject.arg.aba.syntax.ABATheory)
	 */
	@Override
	public AbaExtension<T> getModel(AbaTheory<T> bbase) {
		// just return the first one.
		return this.getModels(bbase).iterator().next();
	}
}
