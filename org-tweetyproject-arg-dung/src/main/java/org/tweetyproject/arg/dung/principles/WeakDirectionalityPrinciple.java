/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.dung.principles;

import java.util.Collection;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Weak Directionality Principle
 * <p>
 * A semantics satisfies weak directionality if for every unattacked set 'U' in a dung theory F it holds that:
 * The extensions of F restricted to 'U' are a superset of the extensions of F intersected with 'U'.
 *
 * @see "van der Torre L, Vesic S. The Principle-Based Approach to Abstract Argumentation Semantics.
 * In: Handbook of formal argumentation, Vol. 1. College Publications; 2018. p. 2735-78."
 * @see DirectionalityPrinciple
 *
 * @author Julian Sander
 */
public class WeakDirectionalityPrinciple extends DirectionalityPrinciple {

	@Override
	public String getName() {
		return "Weak Directionality";
	}

	@Override
	public boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev) {
		DungTheory theory = (DungTheory) kb;
		Collection<Extension<DungTheory>> exts = ev.getModels(theory);
		Collection<Extension<DungTheory>> unattackedSets = this.getUnattackedSets(theory);

		for (Extension<DungTheory> set: unattackedSets) {
			// calculate extensions of the theory restricted to set
			Collection<Extension<DungTheory>> extsRestriction = getExtensionsRestriction(ev, theory, set);

			// get intersections of the extensions of theory with set
			Collection<Extension<DungTheory>> extsIntersection = getExtensionsIntersection(exts, set);

			if (!extsRestriction.containsAll(extsIntersection)) return false;
		}
		return true;
	}
}
