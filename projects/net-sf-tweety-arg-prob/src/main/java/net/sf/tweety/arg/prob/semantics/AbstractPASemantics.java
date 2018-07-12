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

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.math.equation.Statement;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.Term;

/**
 * This class bundles common answering behaviour for
 * probabilistic argumentation semantics.
 * 
 * @author Matthias Thimm
 */
public abstract class AbstractPASemantics implements PASemantics {

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.prob.semantics.PASemantics#satisfies(net.sf.tweety.arg.prob.semantics.ProbabilisticExtension, net.sf.tweety.arg.dung.DungTheory)
	 */
	@Override
	public abstract boolean satisfies(ProbabilisticExtension p, DungTheory theory);

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.prob.semantics.PASemantics#getSatisfactionStatement(net.sf.tweety.arg.dung.DungTheory, java.util.Map)
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
