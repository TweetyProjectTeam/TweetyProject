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
import java.util.concurrent.CancellationException;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.AbstractAdfReasoner;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.sat.SatSolver;
import org.tweetyproject.sat.sat4j.Sat4jIncrementalSolver;

/**
 * Enumerates the conflict-free interpretations (CF) of an ADF as k++ADF does:
 * the interpretation that maps every argument to u, followed by the models of
 * Φ_cf that are strictly more informative, each excluded after it is found.
 *
 * @see "Thomas Linsbichler, Marco Maratea, Andreas Niskanen, Johannes P. Wallner, Stefan Woltran. 'Novel Algorithms for Abstract Dialectical Frameworks based on Complexity Analysis of Subclasses and SAT Solving.' IJCAI 2018."
 * @see "k++ADF, https://bitbucket.org/andreasniskanen/k-adf (enumerate_cf)"
 *
 * @author Lars Bengel
 */
public class SatConflictFreeAdfReasoner extends AbstractAdfReasoner {

	private final Supplier<SatSolver> solvers;

	/**
	 * Creates the reasoner with the {@link Sat4jIncrementalSolver}.
	 */
	public SatConflictFreeAdfReasoner() {
		this(Sat4jIncrementalSolver::new);
	}

	/**
	 * @param solvers creates a fresh solver for every call
	 */
	public SatConflictFreeAdfReasoner(Supplier<SatSolver> solvers) {
		this.solvers = solvers;
	}

	@Override
	public Collection<ThreeValuedInterpretation> getModels(AbstractDialecticalFramework adf) {
		List<ThreeValuedInterpretation> models = new ArrayList<>();
		ThreeValuedInterpretation undecided = ThreeValuedInterpretation.allUndecided(adf.getNodes());
		models.add(undecided);
		if (adf.isEmpty()) {
			return models;
		}
		try (SatSolver solver = solvers.get()) {
			ConflictFreeEncoding encoding = new ConflictFreeEncoding(new AbstractDialecticalFramework(adf), solver);
			encoding.addMoreInformativeThan(undecided);
			int[] variables = encoding.variables();
			boolean[] witness;
			while ((witness = solver.witness(variables)) != null) {
				if (Thread.currentThread().isInterrupted()) {
					throw new CancellationException("Enumeration interrupted.");
				}
				ThreeValuedInterpretation model = encoding.decode(witness);
				models.add(model);
				encoding.addRefineUnequal(model);
			}
		}
		return models;
	}

	@Override
	public ThreeValuedInterpretation getModel(AbstractDialecticalFramework adf) {
		return ThreeValuedInterpretation.allUndecided(adf.getNodes());
	}

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		requireDomain(adf, v);
		try (SatSolver solver = solvers.get()) {
			ConflictFreeEncoding encoding = new ConflictFreeEncoding(new AbstractDialecticalFramework(adf), solver);
			return solver.satisfiable(encoding.assumptions(v));
		}
	}

}
