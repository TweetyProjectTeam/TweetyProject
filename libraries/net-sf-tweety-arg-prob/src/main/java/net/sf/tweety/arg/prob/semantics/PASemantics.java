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
package net.sf.tweety.arg.prob.semantics;

import java.util.Collection;
import java.util.Map;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.math.equation.Statement;
import net.sf.tweety.math.term.FloatVariable;

/**
 * This interface contains common methods for probabilistic argumentation semantics.
 * 
 * @author Matthias Thimm
 */
public interface PASemantics {
	
	/**
	 * Checks whether the given probabilistic extension satisfies the given
	 * argumentation theory wrt. this semantics.
	 * @param p a probabilistic extension.
	 * @param theory an argumentation theory
	 * @return "true" iff the given distribution satisfies the given conditional.
	 */
	public boolean satisfies(ProbabilisticExtension p, DungTheory theory);
	
	/**
	 * Returns the mathematical statement corresponding to the satisfaction
	 * of the given theory wrt. this semantics.
	 * @param theory an argumentation theory.
	 * @param worlds2vars a map mapping the (probabilities of the) extensions to mathematical variables (for constructing the statement).
	 * @return the mathematical statement corresponding to the satisfaction
	 * of the given theory wrt. this semantics.
	 */
	public Collection<Statement> getSatisfactionStatements(DungTheory theory, Map<Collection<Argument>,FloatVariable> worlds2vars);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString();
}
