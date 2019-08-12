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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.reasoner;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;

public class NaiveReasoner extends AbstractDialecticalFrameworkReasoner {

	/**
	 * A SAT solver
	 */
	private IncrementalSatSolver solver;

	public NaiveReasoner(IncrementalSatSolver solver) {
		this.solver = solver;
	}

	@Override
	public Collection<Interpretation> getModels(AbstractDialecticalFramework adf) {
		SatEncoding enc = new SatEncoding(adf);
		Collection<Interpretation> models = new LinkedList<Interpretation>();
		List<Disjunction> excluded = new LinkedList<Disjunction>();
		final Interpretation EMPTY = new Interpretation(adf);
		Interpretation interpretation;
		while ((interpretation = existsNai(adf, EMPTY, excluded, enc)) != null) {
			excluded.add(enc.refineLarger(interpretation));
			models.add(interpretation);
		}
		return models;
	}

	@Override
	public Interpretation getModel(AbstractDialecticalFramework adf) {
		SatEncoding enc = new SatEncoding(adf);
		return existsNai(adf, new Interpretation(adf), Collections.emptyList(), enc);
	}

	private Interpretation existsNai(AbstractDialecticalFramework adf, Interpretation interpretation,
			List<Disjunction> excluded, SatEncoding enc) {
		Interpretation result = null;
		try (SatSolverState state = solver.createState()) {
			state.add(enc.conflictFreeInterpretation());
			state.add(excluded);
			state.add(enc.largerInterpretation(interpretation));
			net.sf.tweety.commons.Interpretation<PlBeliefSet, PlFormula> witness = state.witness();
			while (witness != null) {
				result = enc.interpretationFromWitness(witness);
				state.add(enc.largerInterpretation(result));
				witness = state.witness();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
