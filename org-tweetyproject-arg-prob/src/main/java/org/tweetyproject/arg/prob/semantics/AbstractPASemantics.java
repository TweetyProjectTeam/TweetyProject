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
import java.util.Map;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.math.equation.Statement;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.FloatVariable;
import org.tweetyproject.math.term.Term;

/**
 * This class bundles common answering behaviour for
 * probabilistic argumentation semantics.
 *
 * @author Matthias Thimm
 */
public abstract class AbstractPASemantics implements PASemantics {
	/** Default */
	public AbstractPASemantics(){

	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.prob.semantics.PASemantics#satisfies(org.tweetyproject.arg.prob.semantics.ProbabilisticExtension, org.tweetyproject.arg.dung.DungTheory)
	 */
	@Override
	public abstract boolean satisfies(ProbabilisticExtension p, DungTheory theory);

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.prob.semantics.PASemantics#getSatisfactionStatement(org.tweetyproject.arg.dung.DungTheory, java.util.Map)
	 */
	@Override
	public abstract Collection<Statement> getSatisfactionStatements(DungTheory theory, Map<Collection<Argument>, FloatVariable> worlds2vars);

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public abstract String toString();

	/**
	 * Constructs the term expressing the probability of the given argument
	 * wrt. to the variables (describing probabilities) of the extensions.
	 * @param arg an argument
	 * @param worlds2vars a map mapping extensions to variables.
	 * @return the term expressing the probability of the given argument.
	 */
	protected Term probabilityTerm(Argument arg, Map<Collection<Argument>,FloatVariable> worlds2vars){
		Term result = null;
		for(Collection<Argument> ext: worlds2vars.keySet()){
			if(ext.contains(arg)){
				Term t = worlds2vars.get(ext);
				if(result == null)
					result = t;
				else result = result.add(t);
			}
		}
		return (result == null)? new FloatConstant(0): result;
	}

}
