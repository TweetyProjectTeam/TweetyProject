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

import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;

/**
 * Enumerates the stable models (ST) of an ADF: the two-valued models v such
 * that the arguments mapped to t are exactly those mapped to t by the grounded
 * interpretation of the reduct D^v. Checks all 2^n two-valued interpretations.
 *
 * @see "Gerhard Brewka, Stefan Ellmauthaler, Hannes Strass, Johannes P. Wallner, Stefan Woltran. 'Abstract Dialectical Frameworks Revisited.' IJCAI 2013."
 *
 * @author Lars Bengel
 */
public class SimpleStableAdfReasoner extends AbstractSimpleAdfReasoner {

	private final SimpleTwoValuedModelAdfReasoner twoValuedModel = new SimpleTwoValuedModelAdfReasoner();
	private final SimpleGroundedAdfReasoner grounded = new SimpleGroundedAdfReasoner();

	@Override
	protected boolean isTwoValued() {
		return true;
	}

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		return twoValuedModel.isModel(adf, v) && grounded.getModel(adf.reduct(v)).satisfied().equals(v.satisfied());
	}

}
