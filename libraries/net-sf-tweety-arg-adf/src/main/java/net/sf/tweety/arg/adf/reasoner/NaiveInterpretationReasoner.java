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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;

public class NaiveInterpretationReasoner extends AbstractDialecticalFrameworkReasoner {

	/**
	 * A SAT solver
	 */
	private SatSolver solver;

	public NaiveInterpretationReasoner(SatSolver solver) {
		this.solver = solver;
	}

	@Override
	public Collection<Interpretation> getModels(AbstractDialecticalFramework adf) {
		SatEncoding enc = new SatEncoding(adf);
		Set<PlFormula> rho = new HashSet<PlFormula>();
		rho.add(enc.conflictFreeInterpretation());
		Collection<Interpretation> models = new LinkedList<Interpretation>();
		Interpretation interpretation;
		while ((interpretation = existsNai(adf, new Interpretation(adf), rho, enc)) != null) {
			rho.add(enc.refineLarger(interpretation));
			models.add(interpretation);
		}

		return models;
	}

	@Override
	public Interpretation getModel(AbstractDialecticalFramework adf) {
		SatEncoding enc = new SatEncoding(adf);
		return existsNai(adf, new Interpretation(adf), Arrays.asList(enc.conflictFreeInterpretation()), enc);
	}

	private Interpretation existsNai(AbstractDialecticalFramework adf, Interpretation interpretation,
			Collection<PlFormula> excluded, SatEncoding enc) {
		Collection<PlFormula> rho = new LinkedList<PlFormula>(excluded);
		rho.add(enc.largerInterpretation(interpretation));
		net.sf.tweety.commons.Interpretation<PlBeliefSet, PlFormula> witness = solver.getWitness(rho);
		Interpretation result = null;
		while (witness != null) {
			result = enc.interpretationFromWitness(witness);
			rho.add(enc.largerInterpretation(result));
			witness = solver.getWitness(rho);
		}
		return result;
	}

}
