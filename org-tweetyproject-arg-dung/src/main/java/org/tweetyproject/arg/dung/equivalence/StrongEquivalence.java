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

import org.tweetyproject.arg.dung.equivalence.strong.EquivalenceKernel;
import org.tweetyproject.arg.dung.syntax.*;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;

import java.util.*;

/**
 * Models strong equivalence wrt. to a given kernel
 *
 * @author Lars Bengel
 */
public class StrongEquivalence implements Equivalence<DungTheory>, EquivalentTheories<DungTheory> {

	private EquivalenceKernel kernel;

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
		
		for(var elem : theories) {
			if(elem == first) continue;
			else if(!isEquivalent(elem, first)) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * compute all strongly equivalent theories for the the given theory i.e. get
	 * all useless attacks of theory and use them to create other strongly
	 * equivalent theories
	 * 
	 * @param theory a dung theory
	 * @return the collection of strongly equivalent theories
	 *//*
		 * public Collection<DungTheory> getEquivalentTheories(DungTheory theory) {
		 * Collection<Attack> uselessAttacks = this.kernel.getUselessAttacks(theory);
		 * 
		 * DungTheory kernelTheory = this.kernel.getKernel(theory);
		 * 
		 * // iterate over all combinations of useless attacks Collection<DungTheory>
		 * theories = new HashSet<>(); for (Set<Attack> subset: new
		 * SetTools<Attack>().subsets(uselessAttacks)) { // create copy of kernel and
		 * add some useless attacks DungTheory newTheory = new DungTheory(kernelTheory);
		 * newTheory.addAllAttacks(kernelTheory.getAttacks());
		 * newTheory.addAllAttacks(subset);
		 * 
		 * theories.add(newTheory); }
		 * 
		 * return theories; }
		 */

	@Override
	public Collection<DungTheory> getEquivalentTheories(DungTheory baseTheory) {
		EnumeratingDungTheoryGenerator theoryGenerator = new EnumeratingDungTheoryGenerator();
		int numArgs = baseTheory.size();
		DungTheory baseKernel = this.kernel.getKernel(baseTheory);

		Collection<DungTheory> theories = new HashSet<>();
		while (theoryGenerator.hasNext()) {
			DungTheory theory = theoryGenerator.next();

			if (theory.size() > numArgs) {
				break;
			}
			if (theory.size() < numArgs) {
				// continue;
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
		return "strongEQ";
	}
}
