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

import java.util.Collection;
import java.util.List;

import org.tweetyproject.arg.adf.semantics.CharacteristicOperator;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;

/**
 * Computes the grounded interpretation (GR) of an ADF, the least fixpoint of Γ,
 * by iterating Γ from the interpretation that maps every argument to u. Unlike
 * the other simple reasoners it does not enumerate interpretations; each step
 * completes only the undecided parents of an argument whose Kleene value is u.
 *
 * @author Lars Bengel
 */
public class SimpleGroundedAdfReasoner extends AbstractSimpleAdfReasoner {

	@Override
	public Collection<ThreeValuedInterpretation> getModels(AbstractDialecticalFramework adf) {
		return List.of(grounded(adf));
	}

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		requireDomain(adf, v);
		return grounded(adf).equals(v);
	}

	private static ThreeValuedInterpretation grounded(AbstractDialecticalFramework adf) {
		ThreeValuedInterpretation v = ThreeValuedInterpretation.allUndecided(adf.getNodes());
		while (true) {
			ThreeValuedInterpretation next = CharacteristicOperator.apply(adf, v);
			if (next.equals(v)) {
				return v;
			}
			v = next;
		}
	}

}
