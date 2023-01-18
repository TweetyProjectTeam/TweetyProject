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
package org.tweetyproject.arg.aspic.syntax;

import java.util.Collection;

import org.tweetyproject.commons.Signature;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.commons.syntax.interfaces.Invertable;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 * Indefeasible implementation of <code>InferenceRule&lt;T&gt;</code>
 *
 * @param <T> is the type of the language that the ASPIC theory's rules range
 *            over
 * @author Nils Geilen
 */
public class StrictInferenceRule<T extends Invertable> extends InferenceRule<T> {

	/**
	 * Constructs an empty instance
	 */
	public StrictInferenceRule() {
	}

	/**
	 * Constructs a strict inference rule p -&gt; c
	 * 
	 * @param conclusion ^= p
	 * @param premise    ^= c
	 */
	public StrictInferenceRule(T conclusion, Collection<T> premise) {
		super(conclusion, premise);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.arg.aspic.syntax.InferenceRule#isDefeasible()
	 */
	@Override
	public boolean isDefeasible() {
		return false;
	}

	@Override
	public StrictInferenceRule<T> clone() {
		StrictInferenceRule<T> rule = new StrictInferenceRule<>();
		rule.addPremises(this.getPremise());
		rule.setConclusion(this.getConclusion());
		return rule;
	}

	@SuppressWarnings("unchecked")
	@Override
	public StrictInferenceRule<T> substitute(Term<?> v, Term<?> t) throws IllegalArgumentException {
		StrictInferenceRule<T> n = this.clone();
		Signature sig = this.getSignature();
		if (sig instanceof FolSignature) {
			n = new StrictInferenceRule<>();
			RelationalFormula c2 = ((RelationalFormula) this.getConclusion()).substitute(v, t);
			for (T x : this.getPremise()) {
				RelationalFormula p2 = ((RelationalFormula) x).substitute(v, t);
				n.addPremise((T) p2);
			}
			n.setConclusion((T) c2);
		}
		return n;
	}

}
