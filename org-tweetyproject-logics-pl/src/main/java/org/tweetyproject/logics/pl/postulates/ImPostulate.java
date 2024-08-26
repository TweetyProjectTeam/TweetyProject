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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.postulates;

import java.util.Collection;
import java.util.Comparator;

import org.tweetyproject.commons.postulates.Postulate;
import org.tweetyproject.commons.postulates.PostulateEvaluatable;
import org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * An abstract postulate for inconsistency measures in propositional
 * logic; the ancestor of all concrete postulates.
 *
 * @author Matthias Thimm
 *
 */
public abstract class ImPostulate implements Postulate<PlFormula>{


	/** The MONOTONY postulate **/
	public static final ImPostulate MONOTONY = new ImMonotony();
	/** The DOMINANCE postulate **/
	public static final ImPostulate DOMINANCE = new ImDominance();
	/** The FREEFORMULAINDEPENDENCE postulate **/
	public static final ImPostulate FREEFORMULAINDEPENDENCE = new ImFreeFormulaIndependence();
	/** The SAFEFORMULAINDEPENDENCE postulate **/
	public static final ImPostulate SAFEFORMULAINDEPENDENCE = new ImSafeFormulaIndependence();
	/** The FREEFORMULADILUTION postulate **/
	public static final ImPostulate FREEFORMULADILUTION = new ImFreeFormulaDilution();
	/** The CONSISTENCY postulate **/
	public static final ImPostulate CONSISTENCY = new ImConsistency();
	/** The NORMALIZATION postulate **/
	public static final ImPostulate NORMALIZATION = new ImNormalization();
	/** The SUPERADDITIVITY postulate **/
	public static final ImPostulate SUPERADDITIVITY = new ImSuperAdditivity();
	/** The PENALTY postulate **/
	public static final ImPostulate PENALTY = new ImPenalty();
	/** The MINORMALIZATION postulate **/
	public static final ImPostulate MINORMALIZATION = new ImMINormalization();
	/** The MISEPARABILITY postulate **/
	public static final ImPostulate MISEPARABILITY = new ImMISeparability();
	/** The ATTENUATION postulate **/
	public static final ImPostulate ATTENUATION = new ImAttenuation();
	/** The ADJUNCTIONINVARIANCE postulate **/
	public static final ImPostulate ADJUNCTIONINVARIANCE = new ImAdjunctionInvariance();
	/** The IRRELEVANCEOFSYNTAX postulate **/
	public static final ImPostulate IRRELEVANCEOFSYNTAX = new ImIrrelevanceOfSyntax();
	/** The EXCHANGE postulate **/
	public static final ImPostulate EXCHANGE = new ImExchange();
	/** The EQUALCONFLICT postulate **/
	public static final ImPostulate EQUALCONFLICT = new ImEqualConflict();
	/** The CONTRADICTION postulate **/
	public static final ImPostulate CONTRADICTION = new ImContradiction();
	/** The WEAKDOMINANCE postulate **/
	public static final ImPostulate WEAKDOMINANCE = new ImWeakDominance();

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.postulates.Postulate#isApplicable(java.util.Collection)
	 */
	@Override
	public abstract boolean isApplicable(Collection<PlFormula> kb);

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.postulates.Postulate#isSatisfied(org.tweetyproject.commons.BeliefBase, org.tweetyproject.commons.postulates.PostulateEvaluatable)
	 */
	@Override
	public boolean isSatisfied(Collection<PlFormula> kb, PostulateEvaluatable<PlFormula> ev) {
		if(ev instanceof BeliefSetInconsistencyMeasure<?>)
			return this.isSatisfied(kb, (BeliefSetInconsistencyMeasure<PlFormula>) ev);
		throw new RuntimeException("PostulateEvaluatable of type InconsistencyMeasure<PlBeliefSet> expected.");
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.postulates.Postulate#isSatisfied(org.tweetyproject.commons.BeliefBase, org.tweetyproject.commons.postulates.PostulateEvaluatable)
	 */
	/**
	 *
	 * Return whether the formulas are satisfied
	 * @param kb collection of formulas
	 * @param ev BeliefSet Inconsistency Measure
	 * @return whether the formulas are satisfied
	 */
	public abstract boolean isSatisfied(Collection<PlFormula> kb, BeliefSetInconsistencyMeasure<PlFormula> ev);

	/**
	 * Comparator for sorting MUS by comparing hash codes.
	 */
	class SimpleMUSComparator implements Comparator<Object> {
		public int compare(Object mus1, Object mus2) {
			return mus1.hashCode() - mus2.hashCode();
		}
	}

    /** Default Constructor */
    public SimpleMUSComparator(){}
}
