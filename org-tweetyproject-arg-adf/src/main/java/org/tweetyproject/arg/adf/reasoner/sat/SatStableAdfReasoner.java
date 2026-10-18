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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.reasoner.sat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.sat.SatSolver;
import org.tweetyproject.sat.sat4j.Sat4jIncrementalSolver;

/**
 * Reasoner for the stable models (ST) of an ADF: the two-valued models v such
 * that the arguments mapped to t are exactly those mapped to t by the grounded
 * interpretation of the reduct D^v. As in k++ADF, the two-valued models are
 * enumerated with one SAT instance and each of them is checked.
 *
 * @see "Gerhard Brewka, Stefan Ellmauthaler, Hannes Strass, Johannes P. Wallner, Stefan Woltran. 'Abstract Dialectical Frameworks Revisited.' IJCAI 2013."
 * @see "k++ADF, https://bitbucket.org/andreasniskanen/k-adf"
 *
 * @author Lars Bengel
 */
public class SatStableAdfReasoner extends AbstractSatAdfReasoner {

	private final SatTwoValuedModelAdfReasoner twoValuedModel;

	private final SatGroundedAdfReasoner grounded;

	/**
	 * Creates the reasoner with the {@link Sat4jIncrementalSolver}.
	 */
	public SatStableAdfReasoner() {
		this(Sat4jIncrementalSolver::new);
	}

	/**
	 * @param solvers creates a fresh solver for every call
	 */
	public SatStableAdfReasoner(Supplier<SatSolver> solvers) {
		super(solvers);
		this.twoValuedModel = new SatTwoValuedModelAdfReasoner(solvers);
		this.grounded = new SatGroundedAdfReasoner(solvers);
	}

	@Override
	public Collection<ThreeValuedInterpretation> getModels(AbstractDialecticalFramework adf) {
		return models(adf, null, true, Integer.MAX_VALUE);
	}

	@Override
	public ThreeValuedInterpretation getModel(AbstractDialecticalFramework adf) {
		List<ThreeValuedInterpretation> models = models(adf, null, true, 1);
		return models.isEmpty() ? null : models.get(0);
	}

	@Override
	public Boolean query(AbstractDialecticalFramework adf, Argument argument, InferenceMode mode) {
		requireArgument(adf, argument);
		boolean credulous = mode == InferenceMode.CREDULOUS;
		return models(adf, argument, credulous, 1).isEmpty() != credulous;
	}

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		return twoValuedModel.isModel(adf, v) && isStable(adf, v);
	}

	/**
	 * @param argument if not null, only models that map it to t (if accepted) or
	 *                 not to t (otherwise) are returned
	 */
	private List<ThreeValuedInterpretation> models(AbstractDialecticalFramework adf, Argument argument, boolean accepted, int limit) {
		List<ThreeValuedInterpretation> models = new ArrayList<>();
		try (SatSolver solver = solvers.get()) {
			TwoValuedEncoding encoding = new TwoValuedEncoding(adf, solver);
			encoding.addTwoValuedModels();
			int[] assumptions = argument == null ? new int[0]
					: new int[] { accepted ? encoding.variable(argument) : -encoding.variable(argument) };
			int[] variables = encoding.variables();
			boolean[] witness;
			while (models.size() < limit && (witness = solver.witness(variables, assumptions)) != null) {
				checkInterrupted();
				ThreeValuedInterpretation model = encoding.decode(witness);
				if (isStable(adf, model)) {
					models.add(model);
				}
				encoding.exclude(witness);
			}
		}
		return models;
	}

	/**
	 * @param v a two-valued model
	 * @return true if the grounded interpretation of the reduct D^v maps exactly
	 *         the arguments to t that v maps to t
	 */
	private boolean isStable(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		return grounded.getModel(adf.reduct(v)).satisfied().equals(v.satisfied());
	}

}
