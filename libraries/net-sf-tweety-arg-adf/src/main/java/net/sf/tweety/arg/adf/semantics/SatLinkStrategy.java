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
package net.sf.tweety.arg.adf.semantics;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Function;

import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition;
import net.sf.tweety.arg.adf.transform.TseitinTransformer;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * Computes the LinkType via two Sat-calls. Is stateless if the given SatSolver
 * is.
 * 
 * @author Mathias Hofer
 *
 */
public final class SatLinkStrategy implements LinkStrategy {

	private final SatSolver solver;
	
	private static final Function<Argument, Proposition> ARGUMENT_MAPPING = arg -> new Proposition(arg.getName());
	
	private static final TseitinTransformer TRANSFORMER = new TseitinTransformer(ARGUMENT_MAPPING, false);

	public SatLinkStrategy(SatSolver solver) {
		this.solver = Objects.requireNonNull(solver);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.semantics.LinkStrategy#link(net.sf.tweety.arg.adf.syntax.Argument, net.sf.tweety.arg.adf.syntax.Argument, net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition)
	 */
	@Override
	public LinkType compute(Argument parent, Argument child, AcceptanceCondition childAcc) {
		if (!childAcc.contains(parent)) {
			throw new IllegalArgumentException("The parent does not occur in the child acceptance condition!");
		}
		
		Collection<Disjunction> childAccClauses = new LinkedList<Disjunction>();
		Proposition parentProposition = ARGUMENT_MAPPING.apply(parent);
		Proposition childAccClausesName = TRANSFORMER.collect(childAcc, childAccClauses);
		boolean attacking = isAttacking(parentProposition, childAccClausesName, new LinkedList<PlFormula>(childAccClauses));
		boolean supporting = isSupporting(parentProposition, childAccClausesName, new LinkedList<PlFormula>(childAccClauses));
		return LinkType.get(attacking, supporting);
	}

	private boolean isAttacking(Proposition parent, PlFormula childAcc, Collection<PlFormula> clauses) {
		clauses.add(new Negation(childAcc));
		clauses.add(new Disjunction(childAcc, new Negation(parent)));

		boolean sat = solver.isSatisfiable(clauses);
		return !sat;
	}

	private boolean isSupporting(Proposition parent, PlFormula childAcc, Collection<PlFormula> clauses) {
		clauses.add(new Negation(childAcc));
		clauses.add(new Disjunction(childAcc, parent));

		boolean sat = solver.isSatisfiable(clauses);
		return !sat;
	}

}
