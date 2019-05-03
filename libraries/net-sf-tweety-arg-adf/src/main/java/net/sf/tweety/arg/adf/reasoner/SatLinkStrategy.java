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

import net.sf.tweety.arg.adf.semantics.Link;
import net.sf.tweety.arg.adf.semantics.LinkType;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.util.Cache;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.Conjunction;
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
public class SatLinkStrategy implements LinkStrategy {

	private SatSolver solver;

	public SatLinkStrategy(SatSolver solver) {
		this.solver = solver;
	}

	@Override
	public Link compute(AbstractDialecticalFramework adf, Argument a, Argument b) {
		Cache<Argument,PlFormula> cache = new Cache<Argument,PlFormula>(arg -> new Proposition(arg.getName()));
		PlFormula fromPl = a.toPlFormula(cache);
		PlFormula toAccPl = adf.getAcceptanceCondition(b).toPlFormula(cache);
		boolean attacking = isAttacking(fromPl, toAccPl);
		boolean supporting = isSupporting(fromPl, toAccPl);
		LinkType linkType = LinkType.get(attacking, supporting);
		return new Link(a, b, linkType);
	}

	private boolean isAttacking(PlFormula from, PlFormula toAcc) {
		Collection<PlFormula> clauses = new Conjunction();
		clauses.add(new Negation(toAcc));
		clauses.add(new Disjunction(toAcc, new Negation(from)));

		boolean sat = solver.isSatisfiable(clauses);
		return !sat;
	}

	private boolean isSupporting(PlFormula from, PlFormula toAcc) {
		Collection<PlFormula> clauses = new Conjunction();
		clauses.add(new Negation(toAcc));
		clauses.add(new Disjunction(toAcc, from));

		boolean sat = solver.isSatisfiable(clauses);
		return !sat;
	}

}
