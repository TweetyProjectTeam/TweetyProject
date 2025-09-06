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
* Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.eaf.reasoner;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.dung.reasoner.SimpleAdmissibleReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.eaf.syntax.EpistemicArgumentationFramework;

/**
 * This reasoner for epistemic Dung theories performs inference on the admissible extension.
 * Extensions are determined by checking for all admissible sets which set combinations satisfy the epistemic constraint.
 * Note that this reasoner does not compute epistemic extension sets.
 *
 * @author Sandra Hoffmann
 *
 */
public class SimpleEAFAdmissibleReasoner extends AbstractEAFReasoner{

	/**
	 * Computes all admissible extensions that satisfy the epistemic constraint of the EAF.
	 *
	 * @param bbase the epistemic argumentation framework
	 * @return A collection of all admissible extensions that satisfy the constraint.
	 */
	public Collection<Extension<EpistemicArgumentationFramework>> getModels(EpistemicArgumentationFramework bbase) {
		return super.getModels(bbase, Semantics.ADM);
	}

	/**
	 * Computes one admissible extension that satisfies the epistemic constraint of the EAF.
	 *
	 * @param bbase the constrained argumentation framework
	 * @return An admissible extension that satisfies the constraint.
	 */
	public Extension<EpistemicArgumentationFramework> getModel(EpistemicArgumentationFramework bbase) {
		return super.getModel(bbase, Semantics.ADM);
	}

}
