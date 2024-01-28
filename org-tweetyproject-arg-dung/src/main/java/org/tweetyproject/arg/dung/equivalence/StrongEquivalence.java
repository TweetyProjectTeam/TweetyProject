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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.dung.equivalence;

import org.tweetyproject.arg.dung.equivalence.kernel.EquivalenceKernel;
import org.tweetyproject.arg.dung.syntax.*;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;

import java.util.*;

/**
 * Models strong equivalence wrt. to a given kernel
 *
 * @author Lars Bengel
 */
public class StrongEquivalence implements Equivalence<DungTheory> {

	private final EquivalenceKernel kernel;

	/**
	 * initialize Equivalence with the given kernel
	 * 
	 * @param kernel an equivalence kernel
	 */
	public StrongEquivalence(EquivalenceKernel kernel) {
		this.kernel = kernel;
	}

	@Override
	public boolean isEquivalent(DungTheory theory1, DungTheory theory2) {
		DungTheory kernelTheory1 = this.kernel.getKernel(theory1);
		DungTheory kernelTheory2 = this.kernel.getKernel(theory2);

		return kernelTheory1.equals(kernelTheory2);

	}

	@Override
	public boolean isEquivalent(Collection<DungTheory> theories) {
		var first = theories.iterator().next();
		
		for(var theory : theories) {
			if (!isEquivalent(theory, first)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * TODO fix
	 * @param baseTheory
	 * @return
	 */
	public Collection<DungTheory> getEquivalentTheories(DungTheory baseTheory) {
		EnumeratingDungTheoryGenerator theoryGenerator = new EnumeratingDungTheoryGenerator();
		int numArgs = baseTheory.size();
		theoryGenerator.setCurrentSize(numArgs);
		DungTheory baseKernel = this.kernel.getKernel(baseTheory);

		Collection<DungTheory> theories = new HashSet<>();
		while (theoryGenerator.hasNext()) {
			DungTheory theory = theoryGenerator.next();

			if (theory.size() > numArgs) {
				break;
			}

            DungTheory kernelTheory = this.kernel.getKernel(theory);

			if (kernelTheory.getAttacks().equals(baseKernel.getAttacks())) {
				theories.add(theory);
			}
		}

		return theories;
	}

	@Override
	public String getDescription() {
		return "Strong Equivalence";
	}
}
