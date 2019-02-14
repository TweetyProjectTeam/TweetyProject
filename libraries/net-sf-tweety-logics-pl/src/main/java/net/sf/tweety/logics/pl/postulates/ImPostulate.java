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
package net.sf.tweety.logics.pl.postulates;

import java.util.Collection;

import net.sf.tweety.commons.postulates.Postulate;
import net.sf.tweety.commons.postulates.PostulateEvaluatable;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * An abstract postulate for inconsistency measures in propositional
 * logic; the ancestor of all concrete postulates.
 * 
 * @author Matthias Thimm
 *
 */
public abstract class ImPostulate implements Postulate<PropositionalFormula>{

	
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
	/** The ATTENUATION postulate **/
	public static final ImPostulate ATTENUATION = new ImAttenuation();
	/** The ADJUNCTIONINVARIANCE postulate **/
	public static final ImPostulate ADJUNCTIONINVARIANCE = new ImAdjunctionInvariance();
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.postulates.Postulate#isApplicable(java.util.Collection)
	 */
	@Override
	public abstract boolean isApplicable(Collection<PropositionalFormula> kb);

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.postulates.Postulate#isSatisfied(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.postulates.PostulateEvaluatable)
	 */
	@Override
	public boolean isSatisfied(Collection<PropositionalFormula> kb, PostulateEvaluatable<PropositionalFormula> ev) {
		if(ev instanceof BeliefSetInconsistencyMeasure<?>)
			return this.isSatisfied(kb, (BeliefSetInconsistencyMeasure<PropositionalFormula>) ev);
		throw new RuntimeException("PostulateEvaluatable of type InconsistencyMeasure<PlBeliefSet> expected.");
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.postulates.Postulate#isSatisfied(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.postulates.PostulateEvaluatable)
	 */
	public abstract boolean isSatisfied(Collection<PropositionalFormula> kb, BeliefSetInconsistencyMeasure<PropositionalFormula> ev);
}
