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
import org.tweetyproject.arg.adf.semantics.TruthValue;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;

/**
 * Reasoner for the initial models (IS) of an ADF: the non-trivial admissible
 * interpretations v such that no admissible interpretation lies strictly
 * between the trivial interpretation and v. Keeps the ≤_i-minimal non-trivial
 * admissible interpretations; verification checks all non-trivial
 * interpretations strictly less informative than v.
 *
 * @see "Lars Bengel, Matthias Thimm. 'Initial Models and Serialisability in Abstract Dialectical Frameworks.' IJCAI 2025."
 *
 * @author Lars Bengel
 */
public class SimpleInitialAdfReasoner extends AbstractSimpleAdfReasoner {

	private final SimpleAdmissibleAdfReasoner admissible = new SimpleAdmissibleAdfReasoner();

	@Override
	public Collection<ThreeValuedInterpretation> getModels(AbstractDialecticalFramework adf) {
		List<ThreeValuedInterpretation> candidates = new ArrayList<>();
		for (ThreeValuedInterpretation v : admissible.getModels(adf)) {
			if (!isTrivial(v)) {
				candidates.add(v);
			}
		}
		List<ThreeValuedInterpretation> models = new ArrayList<>();
		for (ThreeValuedInterpretation v : candidates) {
			if (candidates.stream().noneMatch(v::isMoreInformativeThan)) {
				models.add(v);
			}
		}
		return models;
	}

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		if (!admissible.isModel(adf, v) || isTrivial(v)) {
			return false;
		}
		List<Argument> decided = new ArrayList<>(v.satisfied());
		decided.addAll(v.unsatisfied());
		// every non-empty proper subset of the decided arguments is set to u
		for (int mask = 1; mask < (1 << decided.size()) - 1; mask++) {
			ThreeValuedInterpretation w = v;
			for (int i = 0; i < decided.size(); i++) {
				if ((mask & (1 << i)) != 0) {
					w = w.with(decided.get(i), TruthValue.UNDECIDED);
				}
			}
			if (admissible.isModel(adf, w)) {
				return false;
			}
		}
		return true;
	}

	private static boolean isTrivial(ThreeValuedInterpretation v) {
		return v.undecided().size() == v.size();
	}

}
