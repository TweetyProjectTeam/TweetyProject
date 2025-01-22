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
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.*;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;

import java.util.*;

/**
 * This class defines strong equivalence for {@link DungTheory argumentation frameworks} wrt. some {@link Semantics semantics},
 * i.e., two AFs F andG are strongly equivalent iff they possess the same set of
 * {@link org.tweetyproject.arg.dung.semantics.Extension extensions} wrt. the {@link Semantics semantics} when conjoined
 * with some arbitrary AF H.
 * Can be characterized by a syntactic kernel.
 *
 * @see "Oikarinen, Emilia, and Stefan Woltran. 'Characterizing strong equivalence for argumentation frameworks.' Artificial intelligence 175.14-15 (2011): 1985-2009."
 *
 * @author Lars Bengel
 */
public class StrongEquivalence implements Equivalence<DungTheory> {

	/** the semantics equivalence kernel for this instance */
	private final EquivalenceKernel kernel;

	/**
	 * Initializes a Strong Equivalence Instance with a kernel for the given semantics
	 * @param semantics some semantics
	 */
	public StrongEquivalence(Semantics semantics) {
		this(EquivalenceKernel.getStrongEquivalenceKernelForSemantics(semantics));
	}

	/**
	 * Initialize Strong Equivalence with the given kernel
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
	public boolean isEquivalent(Collection<DungTheory> objects) {
		DungTheory first = objects.iterator().next();
		
		for(DungTheory theory : objects) {
			if (!isEquivalent(theory, first)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getName() {
		return "Strong Equivalence";
	}
}
