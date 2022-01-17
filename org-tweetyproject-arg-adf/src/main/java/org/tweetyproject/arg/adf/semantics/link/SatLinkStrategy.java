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
package org.tweetyproject.arg.adf.semantics.link;

import java.util.Objects;
import java.util.function.Function;

import org.tweetyproject.arg.adf.sat.IncrementalSatSolver;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.transform.TseitinTransformer;
import org.tweetyproject.arg.adf.util.CacheMap;

/**
 * Computes the LinkType via two Sat-calls.
 * 
 * @author Mathias Hofer
 *
 */
public final class SatLinkStrategy implements LinkStrategy {

	private final IncrementalSatSolver solver;
/**
 * 
 * @param solver solver
 */
	public SatLinkStrategy(IncrementalSatSolver solver) {
		this.solver = Objects.requireNonNull(solver);
	}

	@Override
	public LinkType compute(Argument parent, AcceptanceCondition childAcc) {
		return compute(parent, childAcc, null);
	}

	@Override
	public LinkType compute(Argument parent, AcceptanceCondition childAcc, Interpretation assumption) {
		if (!childAcc.contains(parent)) {
			throw new IllegalArgumentException("The parent does not occur in the child acceptance condition!");
		}

		Function<Argument, Literal> mapping = new CacheMap<>(arg -> Literal.create(arg.getName()));

		TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(mapping, false);

		try (SatSolverState state = solver.createState()) {
			Literal childAccName = transformer.collect(childAcc, state::add);

			state.add(Clause.of(childAccName.neg()));

			// compute the link type relative to the assumption
			if (assumption != null) {
				for (Argument arg : assumption.satisfied()) {
					state.add(Clause.of(mapping.apply(arg)));
				}
				for (Argument arg : assumption.unsatisfied()) {
					state.add(Clause.of(mapping.apply(arg).neg()));
				}
			}

			// used to activate either attacking or supporting check
			Literal toggle = Literal.create("toggle");

			Clause attackingClause = Clause.of(childAccName, mapping.apply(parent).neg(), toggle);
			state.add(attackingClause);

			Clause supportingClause = Clause.of(childAccName, mapping.apply(parent), toggle.neg());
			state.add(supportingClause);

			// satisfies attackingClause, thus deactivates the attacking check
			state.assume(toggle);
			boolean supporting = !state.satisfiable();

			// satisfies supportingClause, thus deactivates the supporting check
			state.assume(toggle.neg());
			boolean attacking = !state.satisfiable();

			return LinkType.get(attacking, supporting);
		}
	}

}
