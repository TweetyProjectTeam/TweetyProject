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
package net.sf.tweety.arg.adf.semantics.link;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;

import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition;
import net.sf.tweety.arg.adf.transform.TseitinTransformer;
import net.sf.tweety.arg.adf.util.CacheMap;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * Computes the LinkType via two Sat-calls.
 * 
 * @author Mathias Hofer
 *
 */
public final class SatLinkStrategy implements LinkStrategy {

	private final IncrementalSatSolver solver;

	private final Interpretation assumption;

	public SatLinkStrategy(IncrementalSatSolver solver) {
		this(solver, null);
	}

	/**
	 * Computes the link type based on the two-valued assignments of the given
	 * assumption.
	 * 
	 * @param solver the incremental sat solver to be used
	 * @param assumption the assumption on which the link type is computed
	 */
	public SatLinkStrategy(IncrementalSatSolver solver, Interpretation assumption) {
		this.solver = Objects.requireNonNull(solver);
		this.assumption = assumption;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.semantics.LinkStrategy#link(net.sf.tweety.arg.adf.
	 * syntax.Argument, net.sf.tweety.arg.adf.syntax.Argument,
	 * net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition)
	 */
	@Override
	public LinkType compute(Argument parent, AcceptanceCondition childAcc) {
		if (!childAcc.contains(parent)) {
			throw new IllegalArgumentException("The parent does not occur in the child acceptance condition!");
		}

		Function<Argument, Proposition> argumentMapping = new CacheMap<Argument, Proposition>(
				arg -> new Proposition(arg.getName()));
		TseitinTransformer transformer = TseitinTransformer.builder(argumentMapping).build();

		try (SatSolverState state = solver.createState()) {
			Proposition parentProposition = argumentMapping.apply(parent);
			Proposition childAccName = transformer.collect(childAcc, state::add);
			state.add(new Disjunction(Collections.singleton(new Negation(childAccName))));

			// compute the link type relative to the assumption
			if (assumption != null) {
				for (Argument arg : assumption.satisfied()) {
					state.assume(argumentMapping.apply(arg), true);
				}
				for (Argument arg : assumption.unsatisfied()) {
					state.assume(argumentMapping.apply(arg), false);
				}
			}

			// used to activate either attacking or supporting check
			Proposition toggle = new Proposition("toggle");

			Disjunction attackingClause = new Disjunction(childAccName, new Negation(parentProposition));
			attackingClause.add(toggle);
			state.add(attackingClause);

			Disjunction supportingClause = new Disjunction(childAccName, parentProposition);
			supportingClause.add(new Negation(toggle));
			state.add(supportingClause);

			// satisfies supportingClause, thus deactivates the supporting check
			state.assume(toggle, false);
			boolean attacking = !state.satisfiable();

			// satisfies attackingClause, thus deactivates the attacking check
			state.assume(toggle, true);
			boolean supporting = !state.satisfiable();

			return LinkType.get(attacking, supporting);
		}
	}

}
