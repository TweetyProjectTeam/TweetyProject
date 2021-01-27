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
package org.tweetyproject.logics.pl.examples;

import org.tweetyproject.commons.postulates.PostulateEvaluator;
import org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import org.tweetyproject.logics.commons.analysis.HsInconsistencyMeasure;
import org.tweetyproject.logics.commons.analysis.NaiveMusEnumerator;
import org.tweetyproject.logics.pl.postulates.ImPostulate;
import org.tweetyproject.logics.pl.sat.PlMusEnumerator;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.semantics.PossibleWorldIterator;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.logics.pl.util.RandomSampler;

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
		evaluator.addPostulate(ImPostulate.EQUALCONFLICT);
		evaluator.addPostulate(ImPostulate.MINORMALIZATION);
		evaluator.addPostulate(ImPostulate.PENALTY);
		evaluator.addPostulate(ImPostulate.SUPERADDITIVITY);
		evaluator.addPostulate(ImPostulate.SAFEFORMULAINDEPENDENCE);
		System.out.println(evaluator.evaluate(10, false).prettyPrint());
	}
}
