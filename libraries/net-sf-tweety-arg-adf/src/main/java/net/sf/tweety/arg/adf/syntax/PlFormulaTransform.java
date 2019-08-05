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
package net.sf.tweety.arg.adf.syntax;

import java.util.Collection;
import java.util.function.Function;

import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Contradiction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Equivalence;
import net.sf.tweety.logics.pl.syntax.Implication;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Tautology;

/**
 * @author Mathias Hofer
 *
 */
public class PlFormulaTransform implements SimpleTransform<PlFormula> {

	private Function<Argument, PlFormula> argumentToPlFormula;
	
	/**
	 * @param argumentToPlFormula
	 */
	public PlFormulaTransform(Function<Argument, PlFormula> argumentToPlFormula) {
		super();
		this.argumentToPlFormula = argumentToPlFormula;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.SimpleTransform#transformDisjunction(java.lang.
	 * Object, java.lang.Object)
	 */
	@Override
	public PlFormula transformDisjunction(Collection<PlFormula> subconditions) {
		return new Disjunction(subconditions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.SimpleTransform#transformConjunction(java.lang.
	 * Object, java.lang.Object)
	 */
	@Override
	public PlFormula transformConjunction(Collection<PlFormula> subconditions) {
		return new Conjunction(subconditions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.SimpleTransform#transformImplication(java.lang.
	 * Object, java.lang.Object)
	 */
	@Override
	public PlFormula transformImplication(PlFormula left, PlFormula right) {
		return new Implication(left, right);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.SimpleTransform#transformEquivalence(java.lang.
	 * Object, java.lang.Object)
	 */
	@Override
	public PlFormula transformEquivalence(PlFormula left, PlFormula right) {
		return new Equivalence(left, right);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.SimpleTransform#transformExclusiveDisjunction(java
	 * .lang.Object, java.lang.Object)
	 */
	@Override
	public PlFormula transformExclusiveDisjunction(PlFormula left, PlFormula right) {
		return new Negation(new Equivalence(left, right));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.SimpleTransform#transformNegation(java.lang.
	 * Object)
	 */
	@Override
	public PlFormula transformNegation(PlFormula sub) {
		return new Negation(sub);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.SimpleTransform#transformArgument()
	 */
	@Override
	public PlFormula transformArgument(Argument argument) {
		return argumentToPlFormula.apply(argument);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.SimpleTransform#transformContradiction()
	 */
	@Override
	public PlFormula transformContradiction() {
		return new Contradiction();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.SimpleTransform#transformTautology()
	 */
	@Override
	public PlFormula transformTautology() {
		return new Tautology();
	}

}
