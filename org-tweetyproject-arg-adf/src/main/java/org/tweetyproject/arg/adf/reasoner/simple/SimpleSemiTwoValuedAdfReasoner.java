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
 * Reasoner for the semi-two-valued models of an ADF: the complete
 * interpretations v such that v^u is ⊆-minimal, i.e., v^t ∪ v^f is
 * ⊆-maximal, among all complete interpretations.
 *
 * @see "Atefeh Keshavarzi Zafarghandi, Rineke Verbrugge, Bart Verheij. 'Semi-Stable Semantics for Abstract Dialectical Frameworks.' KR 2021."
 *
 * @author Lars Bengel
 */
public class SimpleSemiTwoValuedAdfReasoner extends AbstractSimpleAdfReasoner {

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
		Set<Argument> range = range(v);
		for (ThreeValuedInterpretation w : candidates) {
			Set<Argument> other = range(w);
			if (other.size() > range.size() && other.containsAll(range)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return the arguments mapped to t or f by v
	 */
	private static Set<Argument> range(ThreeValuedInterpretation v) {
		Set<Argument> range = new HashSet<>(v.satisfied());
		range.addAll(v.unsatisfied());
		return range;
	}

}
