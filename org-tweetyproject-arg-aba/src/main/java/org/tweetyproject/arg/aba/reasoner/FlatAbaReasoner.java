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
 /**
 * 
 */
package org.tweetyproject.arg.aba.reasoner;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.aba.semantics.AbaExtension;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.arg.aba.syntax.Assumption;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.InferenceMode;

/**
 * This class models a reasoner over ABA formulae.
 * This reasoner can only be used with flat ABA theories because
 * only those can be transformed into Dung frameworks.
 * 
 * @param <T> the type of formulas
 * 
 * @author Nils Geilen 
 * @author Matthias Thimm
 */
public class FlatAbaReasoner<T extends Formula> extends GeneralAbaReasoner<T> {

	private Semantics semantics;
	
	/**
	 * Creates a new instance
	 * 
	 * @param semantics
	 *            an indicator for the used semantics (c.f.
	 *            org.tweetyproject.arg.dung.semantics.Semantics)
	 */
	public FlatAbaReasoner(Semantics semantics) {
		this.semantics = semantics;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.aba.reasoner.GeneralABAReasoner#query(org.tweetyproject.arg.aba.syntax.ABATheory, org.tweetyproject.arg.aba.syntax.Assumption, org.tweetyproject.commons.InferenceMode)
	 */
	@Override
	public Boolean query(AbaTheory<T> beliefbase, Assumption<T> query, InferenceMode inferenceMode) {
		Argument arg = new Argument(query.getConclusion().toString());
		DungTheory dt = beliefbase.asDungTheory();
		AbstractExtensionReasoner aer = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics);
		return aer.query(dt,arg,inferenceMode);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.aba.reasoner.GeneralABAReasoner#getModels(org.tweetyproject.arg.aba.syntax.ABATheory)
	 */
	@Override
	public Collection<AbaExtension<T>> getModels(AbaTheory<T> abat) {
		DungTheory dt = abat.asDungTheory();
		AbstractExtensionReasoner aer = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics);
		Collection<AbaExtension<T>> result = new HashSet<>();
		for (Extension ext : aer.getModels(dt)) {
			AbaExtension<T> abaext = new AbaExtension<T>();
			for (Argument arg : ext) {
				for (Assumption<T> ass : abat.getAssumptions()) {
					if (ass.toString().equals(arg.toString())) {
						abaext.add(ass);
						break;
					}
				}
			}
			result.add(abaext);
		}
		return result;
	}
}
