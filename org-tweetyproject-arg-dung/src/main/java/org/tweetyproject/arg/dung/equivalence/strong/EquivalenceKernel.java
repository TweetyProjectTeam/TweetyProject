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

package org.tweetyproject.arg.dung.equivalence.strong;

import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;

/**
 * An abstract kernel for strong equivalence in abstract argumentation frameworks
 *
 * @author Lars Bengel
 */
public abstract class EquivalenceKernel {

	/**
	 * STABLE
	 */
    public static final EquivalenceKernel STABLE = new StableKernel();
    /**
     * COMPLETE
     */
    public static final EquivalenceKernel COMPLETE = new CompleteKernel();
    /**
     * GROUNDED
     */
    public static final EquivalenceKernel GROUNDED = new GroundedKernel();
    /**
     * ADMISSIBLE
     */
    public static final EquivalenceKernel ADMISSIBLE = new AdmissibleKernel();
    /**
     * UNCHALLENGED
     */
    public static final EquivalenceKernel UNCHALLENGED = new UnchallengedKernel();
    /**
     * STRONG ADMISSIBLE
     */
    public static final EquivalenceKernel STRONGADMISSIBLE = new StrongAdmissibleKernel();

    /**
     * computes the kernel of the given AF
     * @param theory a dung theory
     * @return a dung theory representing the kernel of the given AF
     */
    @SuppressWarnings("unlikely-arg-type")
	public DungTheory getKernel(DungTheory theory) {
        // create copy of theory
        DungTheory kernel = new DungTheory(theory);
        kernel.addAllAttacks(theory.getAttacks());

        // remove all useless attacks to retrieve the kernel of the theory
        Collection<Attack> uselessAttacks = getUselessAttacks(theory);
        kernel.removeAll(uselessAttacks);
        return kernel;
    }

    /**
     * compute the set of 'useless' attacks, i.e. all attacks that are are cut to retrieve the kernel of the AF
     * @param theory a dung theory
     * @return the set of useless attacks
     */
    public abstract Collection<Attack> getUselessAttacks(DungTheory theory);
    
    /**
     * Returns a suitable kernel for the specified semantics
     * @param semantics Semantics influencing the equivalence
     * @return kernel to assess the equivalence
     */
    public static EquivalenceKernel getKernel(Semantics semantics) {
    	switch (semantics) {
		case ADM: {
			return EquivalenceKernel.ADMISSIBLE;
		}
		case CO:{
			return EquivalenceKernel.COMPLETE;
		}
		case GR:{
			return EquivalenceKernel.GROUNDED;
		}
		case ST:{
			return EquivalenceKernel.STABLE;
		}
		case PR:{
			return EquivalenceKernel.ADMISSIBLE;
		}
		case UC:{
			return EquivalenceKernel.UNCHALLENGED;
		}
		case SAD:{
			return EquivalenceKernel.STRONGADMISSIBLE;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + semantics);
		}
    }
}
