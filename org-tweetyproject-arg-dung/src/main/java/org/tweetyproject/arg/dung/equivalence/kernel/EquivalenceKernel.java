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
package org.tweetyproject.arg.dung.equivalence.kernel;


import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;

/**
 * A general interface for an equivalence kernel for abstract argumentation frameworks, used to characterise strong equivalence and expansion equivalences wrt. different semantics.
 * A kernel essentially defines which attacks are redundant.
 * These attacks can be removed from the framework without influencing the extensions wrt. the semantics under some expansion of the argumentation framework.
 *
 * @see "Oikarinen, Emilia, and Stefan Woltran. 'Characterizing strong equivalence for argumentation frameworks.' Artificial intelligence 175.14-15 (2011): 1985-2009."
 * @see "Baumann, Ringo. 'Normal and strong expansion equivalence for argumentation frameworks.' Artificial Intelligence 193 (2012): 18-44."
 *
 * @author Lars Bengel
 */
public abstract class EquivalenceKernel {
	/** STABLE kernel */
    public static final EquivalenceKernel STABLE = new StableKernel();
    /** COMPLETE kernel */
    public static final EquivalenceKernel COMPLETE = new CompleteKernel();
    /** GROUNDED kernel */
    public static final EquivalenceKernel GROUNDED = new GroundedKernel();
    /** ADMISSIBLE kernel */
    public static final EquivalenceKernel ADMISSIBLE = new AdmissibleKernel();
    /** Strong Expansion ADMISSIBLE kernel */
    public static final EquivalenceKernel SE_ADMISSIBLE = new StrongExpansionAdmissibleKernel();
    /** Strong Expansion COMPLETE kernel */
    public static final EquivalenceKernel SE_COMPLETE = new StrongExpansionCompleteKernel();
    /** Strong Expansion GROUNDED kernel */
    public static final EquivalenceKernel SE_GROUNDED = new StrongExpansionGroundedKernel();
    /** Local Expansion COMPLETE kernel */
    public static final EquivalenceKernel LE_COMPLETE = new LocalExpansionCompleteKernel();

    /**
     * Computes the kernel of the given AF
     * @param theory a dung theory
     * @return a dung theory representing the kernel of the given AF
     */
	public DungTheory getKernel(DungTheory theory) {
        // create copy of theory
        DungTheory kernel = new DungTheory(theory);
        kernel.addAllAttacks(theory.getAttacks());

        // remove all redundant attacks to retrieve the kernel of the theory
        Collection<Attack> redundantAttacks = getRedundantAttacks(theory);
        kernel.removeAll(redundantAttacks);
        return kernel;
    }

    /**
     * Computes the set of redundant attacks, i.e., all attacks that are removed in order to retrieve the kernel of the given AF
     * @param theory a dung theory
     * @return the set of redundant attacks
     */
    public abstract Collection<Attack> getRedundantAttacks(DungTheory theory);
    
    /**
     * Returns the corresponding strong equivalence kernel for the specified semantics
     * @param semantics some semantics
     * @return equivalence kernel for the given semantics
     */
    public static EquivalenceKernel getStrongEquivalenceKernelForSemantics(Semantics semantics) {
        switch (semantics) {
			case ADM,PR,UC,ID,SST,EA -> {
                return EquivalenceKernel.ADMISSIBLE;
            }
            case CO -> {
                return EquivalenceKernel.COMPLETE;
            }
			case GR,SAD -> {
                return EquivalenceKernel.GROUNDED;
            }
            case ST -> {
                return EquivalenceKernel.STABLE;
            }
            default -> throw new IllegalArgumentException("No kernel exists for semantics: " + semantics);
        }
    }
}
