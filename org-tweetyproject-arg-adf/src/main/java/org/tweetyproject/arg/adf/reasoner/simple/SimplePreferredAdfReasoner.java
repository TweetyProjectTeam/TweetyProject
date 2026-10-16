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
import java.util.List;

import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;

/**
 * Enumerates the preferred interpretations (PR) of an ADF: the ≤_i-maximal
 * admissible interpretations (Brewka et al. 2013, Definition 4).
 * <p>
 * Every admissible interpretation lies below a complete one, so these are
 * exactly the ≤_i-maximal complete interpretations. The reasoner collects all
 * complete interpretations (3^n candidates) and keeps the maximal ones.
 *
 * @author Lars Bengel
 */
public class SimplePreferredAdfReasoner extends AbstractSimpleAdfReasoner {

	private final SimpleCompleteAdfReasoner complete = new SimpleCompleteAdfReasoner();

	@Override
	public Collection<ThreeValuedInterpretation> getModels(AbstractDialecticalFramework adf) {
		Collection<ThreeValuedInterpretation> candidates = complete.getModels(adf);
		List<ThreeValuedInterpretation> models = new ArrayList<>();
		for (ThreeValuedInterpretation v : candidates) {
			if (isMaximal(v, candidates)) {
				models.add(v);
			}
		}
		return models;
	}

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		return complete.isModel(adf, v) && isMaximal(v, complete.getModels(adf));
	}

	private static boolean isMaximal(ThreeValuedInterpretation v, Collection<ThreeValuedInterpretation> candidates) {
		for (ThreeValuedInterpretation w : candidates) {
			if (w.isMoreInformativeThan(v)) {
				return false;
			}
		}
		return true;
	}

}
