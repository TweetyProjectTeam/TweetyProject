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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.dung.principles;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.postulates.Postulate;
import org.tweetyproject.commons.postulates.PostulateEvaluatable;

import java.util.Collection;

/**
 * Models a principle for argumentation semantics i.e. a property that
 * can be satisfied or violated by a semantics.
 *
 * @author Lars Bengel
 */
public abstract class Principle implements Postulate<Argument> {

    /** The I-maximality principle **/
    public static final Principle I_MAXIMALITY = new IMaximalityPrinciple();
    /** The conflict-free principle **/
    public static final Principle CONFLICT_FREE = new ConflictFreePrinciple();
    /** The admissibility principle **/
    public static final Principle ADMISSIBILITY = new AdmissibilityPrinciple();
    /** The strong admissibility principle **/
    public static final Principle STRONG_ADMISSIBILITY = new StrongAdmissibilityPrinciple();
    /** The reinstatement principle **/
    public static final Principle REINSTATEMENT = new ReinstatementPrinciple();
    /** The weak reinstatement principle **/
    public static final Principle WEAK_REINSTATEMENT = new WeakReinstatementPrinciple();
    /** The CF-reinstatement principle **/
    public static final Principle CF_REINSTATEMENT = new CFReinstatementPrinciple();
    /** The directionality principle **/
    public static final Principle DIRECTIONALITY = new DirectionalityPrinciple();
    /** The Irrelevance of Necessarily Rejected Arguments (INRA) principle **/
    public static final Principle INRA = new INRAPrinciple();
    /** The Strong Completeness Outside Odd Cycles (SCOOC) principle **/
    public static final Principle SCOOC = new SCOOCPrinciple();
    /** The modularization principle **/
    public static final Principle MODULARIZATION = new ModularizationPrinciple();
    /** The reduct admissibility principle **/
    public static final Principle REDUCT_ADM = new ReductAdmissibilityPrinciple();
    /** The semi qualified admissibility principle **/
    public static final Principle SEMIQUAL_ADM = new SemiQualifiedAdmissibilityPrinciple();
    /** The SCC decomposability principle **/
    public static final Principle SCC_DECOMPOSABILITY = new SccDecomposabilityPrinciple();
    /** The naivety principle **/
    public static final Principle NAIVETY = new NaivetyPrinciple();
    /** The Allowing Abstention Principle */
    public static final Principle ALLOWINGABSTENTION = new AllowingAbstentionPrinciple();

    /* (non-Javadoc)
     * @see org.tweetyproject.commons.postulates.Postulate#isApplicable(java.util.Collection)
     */
    @Override
    public abstract boolean isApplicable(Collection<Argument> kb);

    /* (non-Javadoc)
     * @see org.tweetyproject.commons.postulates.Postulate#isSatisfied(org.tweetyproject.commons.BeliefBase, org.tweetyproject.commons.postulates.PostulateEvaluatable)
     */
    @Override
    public boolean isSatisfied(Collection<Argument> kb, PostulateEvaluatable<Argument> ev) {
        if(ev instanceof AbstractExtensionReasoner)
            return this.isSatisfied(kb, (AbstractExtensionReasoner) ev);
        throw new RuntimeException("PostulateEvaluatable of type AbstractExtensionReasoner expected.");
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.commons.postulates.Postulate#isSatisfied(org.tweetyproject.commons.BeliefBase, org.tweetyproject.commons.postulates.PostulateEvaluatable)
     */
    
    /**
     * Computes whether the given extension reasoner (i.e. semantics) satisfies this principle for this specific instance
     * @param kb some argumentation framework
     * @param ev an extension reasoner
     * @return true, if this principle is satisfied for this instance and semantics
     */
    public abstract boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev);
}
