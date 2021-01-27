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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.prob.semantics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.math.equation.Statement;
import org.tweetyproject.math.term.FloatVariable;

/**
 * P is justifiable wrt. AF if P is coherent and optimistic.
 * @author Matthias Thimm
 */
public class JustifiablePASemantics extends AbstractPASemantics{

	/** The semantics this semantics is based upon. */
	private PASemantics cohSemantics = new CoherentPASemantics();
	private PASemantics optSemantics = new OptimisticPASemantics();
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.prob.semantics.AbstractPASemantics#satisfies(org.tweetyproject.arg.prob.semantics.ProbabilisticExtension, org.tweetyproject.arg.dung.DungTheory)
	 */
	@Override
	public boolean satisfies(ProbabilisticExtension p, DungTheory theory) {
		return this.cohSemantics.satisfies(p, theory) && this.optSemantics.satisfies(p, theory);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.prob.semantics.AbstractPASemantics#getSatisfactionStatement(org.tweetyproject.arg.dung.DungTheory, java.util.Map)
	 */
	@Override
	public Collection<Statement> getSatisfactionStatements(DungTheory theory, Map<Collection<Argument>, FloatVariable> worlds2vars) {
		Set<Statement> stats = new HashSet<Statement>();
		stats.addAll(this.cohSemantics.getSatisfactionStatements(theory, worlds2vars));
		stats.addAll(this.optSemantics.getSatisfactionStatements(theory, worlds2vars));
		return stats;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.prob.semantics.AbstractPASemantics#toString()
	 */
	@Override
	public String toString() {
		return "Justifiable Semantics";
	}

}
