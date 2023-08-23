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

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Kernel SK = (A, R') for strong equivalence wrt. unchallenged semantics
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class UnchallengedKernel extends EquivalenceKernel {

	@Override
	public Collection<Attack> getUselessAttacks(DungTheory theory) {
		var uselessAttacks = new AdmissibleKernel().getUselessAttacks(theory);
		for (Argument a: theory) {
			for (Argument b : theory) {
				if(theory.isAttackedBy(b, b)) {
					if (a != b) {
						if (!theory.isAttackedBy(a, b)) {
							uselessAttacks.add(new Attack(a, b));
						}
					}
				}
			}
		}
		return uselessAttacks;
	}

}
