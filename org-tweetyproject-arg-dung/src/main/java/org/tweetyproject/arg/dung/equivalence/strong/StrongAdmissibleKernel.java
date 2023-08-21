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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.equivalence.strong;

import java.util.Collection;

import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Kernel SK = (A, R') for strong equivalence wrt. strong admissible semantics
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class StrongAdmissibleKernel extends EquivalenceKernel  {

	@Override
	public Collection<Attack> getUselessAttacks(DungTheory theory) {
		var output = new AdmissibleKernel().getUselessAttacks(theory);
		output.addAll(new CompleteKernel().getUselessAttacks(theory));
		output.addAll(new GroundedKernel().getUselessAttacks(theory));
		output.addAll(new StableKernel().getUselessAttacks(theory));
		return output;
	}
}
