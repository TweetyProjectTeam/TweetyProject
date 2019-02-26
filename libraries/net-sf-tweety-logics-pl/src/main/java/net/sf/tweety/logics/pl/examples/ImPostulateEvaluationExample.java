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
package net.sf.tweety.logics.pl.examples;

import net.sf.tweety.commons.postulates.PostulateEvaluator;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.HsInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.NaiveMusEnumerator;
import net.sf.tweety.logics.pl.postulates.ImPostulate;
import net.sf.tweety.logics.pl.sat.PlMusEnumerator;
import net.sf.tweety.logics.pl.sat.Sat4jSolver;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.semantics.PossibleWorldIterator;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.PlSignature;
import net.sf.tweety.logics.pl.util.RandomSampler;

/**
 * Example code illustrating automatic postulate evaluation of
 * inconsistency measures.
 * @author Matthias Thimm
 */
public class ImPostulateEvaluationExample {
	public static void main(String[] args) {
		SatSolver.setDefaultSolver(new Sat4jSolver());
		PlMusEnumerator.setDefaultEnumerator(new NaiveMusEnumerator<PlFormula>(SatSolver.getDefaultSolver()));
		BeliefSetInconsistencyMeasure<PlFormula> im = new HsInconsistencyMeasure<PlBeliefSet,PlFormula>(new PossibleWorldIterator());
		RandomSampler sampler = new RandomSampler(new PlSignature(4),0.2,2,4);
		PostulateEvaluator<PlFormula,PlBeliefSet> evaluator = new PostulateEvaluator<PlFormula,PlBeliefSet>(sampler, im);
//		evaluator.addPostulate(ImPostulate.ADJUNCTIONINVARIANCE);
//		evaluator.addPostulate(ImPostulate.ATTENUATION);
//		evaluator.addPostulate(ImPostulate.CONTRADICTION);
		evaluator.addPostulate(ImPostulate.EQUALCONFLICT);
		evaluator.addPostulate(ImPostulate.MINORMALIZATION);
		evaluator.addPostulate(ImPostulate.PENALTY);
		evaluator.addPostulate(ImPostulate.SUPERADDITIVITY);
		evaluator.addPostulate(ImPostulate.SAFEFORMULAINDEPENDENCE);
		System.out.println(evaluator.evaluate(10, false));
	}
}
