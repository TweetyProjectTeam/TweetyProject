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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.reasoner.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;

/**
 * Enumerates the semi-stable models of an ADF: the semi-two-valued models v
 * such that the arguments mapped to t are exactly those mapped to t by the
 * grounded interpretation of the sub-reduct D^v, which keeps the arguments
 * mapped to t or u and replaces those mapped to f by ⊥. Checks every
 * semi-two-valued model.
 *
 * @see "Atefeh Keshavarzi Zafarghandi, Rineke Verbrugge, Bart Verheij. 'Semi-Stable Semantics for Abstract Dialectical Frameworks.' KR 2021."
 * @see "Atefeh Keshavarzi Zafarghandi, Johannes P. Wallner. 'Complexity of Semi-Stable Semantics in Abstract Dialectical Frameworks.' COMMA 2024."
 *
 * @author Lars Bengel
 */
public class SimpleSemiStableAdfReasoner extends AbstractSimpleAdfReasoner {

	private final SimpleSemiTwoValuedAdfReasoner semiTwoValued = new SimpleSemiTwoValuedAdfReasoner();
	private final SimpleGroundedAdfReasoner grounded = new SimpleGroundedAdfReasoner();

	@Override
	public Collection<ThreeValuedInterpretation> getModels(AbstractDialecticalFramework adf) {
		List<ThreeValuedInterpretation> models = new ArrayList<>();
		for (ThreeValuedInterpretation v : semiTwoValued.getModels(adf)) {
			if (isSemiStable(adf, v)) {
				models.add(v);
			}
		}
		return models;
	}

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		return semiTwoValued.isModel(adf, v) && isSemiStable(adf, v);
	}

	/**
	 * @param v a semi-two-valued model
	 * @return true if the grounded interpretation of the sub-reduct D^v maps
	 *         exactly the arguments to t that v maps to t
	 */
	private boolean isSemiStable(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		Set<Argument> kept = new HashSet<>(v.satisfied());
		kept.addAll(v.undecided());
		return grounded.getModel(adf.restrict(kept)).satisfied().equals(v.satisfied());
	}

}
