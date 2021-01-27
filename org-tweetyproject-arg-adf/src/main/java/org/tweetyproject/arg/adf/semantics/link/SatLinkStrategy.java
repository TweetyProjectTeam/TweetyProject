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
import org.tweetyproject.arg.adf.syntax.pl.Atom;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Negation;
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
	 * org.tweetyproject.arg.adf.semantics.LinkStrategy#link(org.tweetyproject.arg.adf.
	 * syntax.Argument, org.tweetyproject.arg.adf.syntax.Argument,
	 * org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition)
	 */
	@Override
	public LinkType compute(Argument parent, AcceptanceCondition childAcc) {
		if (!childAcc.contains(parent)) {
			throw new IllegalArgumentException("The parent does not occur in the child acceptance condition!");
		}

		Function<Argument, Atom> mapping = new CacheMap<>(arg -> Atom.of(arg.getName()));
		
		TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(mapping, false);
		
		try (SatSolverState state = solver.createState()) {
			Atom childAccName = transformer.collect(childAcc, state::add);
			
			state.add(Clause.of(new Negation(childAccName)));

			// compute the link type relative to the assumption
			if (assumption != null) {
				for (Argument arg : assumption.satisfied()) {
					state.add(Clause.of(mapping.apply(arg)));
				}
				for (Argument arg : assumption.unsatisfied()) {
					state.add(Clause.of(new Negation(mapping.apply(arg))));
				}
			}

			// used to activate either attacking or supporting check
			Atom toggle = Atom.of("toggle");

			Clause attackingClause = Clause.of(childAccName, new Negation(mapping.apply(parent)), toggle);
			state.add(attackingClause);

			Clause supportingClause = Clause.of(childAccName, mapping.apply(parent), new Negation(toggle));				
			state.add(supportingClause);
			
			// satisfies attackingClause, thus deactivates the attacking check
			state.assume(toggle, true);
			boolean supporting = !state.satisfiable();
			
			// satisfies supportingClause, thus deactivates the supporting check
			state.assume(toggle, false);
			boolean attacking = !state.satisfiable();

			return LinkType.get(attacking, supporting);
		}
	}

}
