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
package net.sf.tweety.arg.adf.transform;

import java.util.Collection;
import java.util.function.Consumer;

import net.sf.tweety.arg.adf.syntax.Argument;

/**
 * A convenience interface for simple transformation operations on the recursive
 * structure of the acceptance conditions.
 * 
 * @author Mathias Hofer
 *
 * @param <R>
 *            the result of the transform operation
 */
public interface SimpleTransform<R> extends Transform<R, R> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.Transform#transformDisjunction(java.util.
	 * function.Consumer, java.util.Collection)
	 */
	@Override
	default R transformDisjunction(Consumer<R> consumer, Collection<R> subconditions, int polarity) {
		R result = transformDisjunction(subconditions);
		consumer.accept(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.Transform#transformConjunction(java.util.
	 * function.Consumer, java.util.Collection)
	 */
	@Override
	default R transformConjunction(Consumer<R> consumer, Collection<R> subconditions, int polarity) {
		R result = transformConjunction(subconditions);
		consumer.accept(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.Transform#transformImplication(java.util.
	 * function.Consumer, java.lang.Object, java.lang.Object)
	 */
	@Override
	default R transformImplication(Consumer<R> consumer, R left, R right, int polarity) {
		R result = transformImplication(left, right);
		consumer.accept(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.Transform#transformEquivalence(java.util.
	 * function.Consumer, java.lang.Object, java.lang.Object)
	 */
	@Override
	default R transformEquivalence(Consumer<R> consumer, R left, R right, int polarity) {
		R result = transformEquivalence(left, right);
		consumer.accept(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.Transform#transformExclusiveDisjunction(java
	 * .util.function.Consumer, java.lang.Object, java.lang.Object)
	 */
	@Override
	default R transformExclusiveDisjunction(Consumer<R> consumer, R left, R right, int polarity) {
		R result = transformExclusiveDisjunction(left, right);
		consumer.accept(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.Transform#transformNegation(java.util.
	 * function.Consumer, java.lang.Object)
	 */
	@Override
	default R transformNegation(Consumer<R> consumer, R sub, int polarity) {
		R result = transformNegation(sub);
		consumer.accept(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.Transform#transformArgument(java.util.
	 * function.Consumer, net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	default R transformArgument(Consumer<R> consumer, Argument argument, int polarity) {
		R result = transformArgument(argument);
		consumer.accept(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.Transform#transformContradiction(java.util.
	 * function.Consumer)
	 */
	@Override
	default R transformContradiction(Consumer<R> consumer, int polarity) {
		R result = transformContradiction();
		consumer.accept(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.Transform#transformTautology(java.util.
	 * function.Consumer)
	 */
	@Override
	default R transformTautology(Consumer<R> consumer, int polarity) {
		R result = transformTautology();
		consumer.accept(result);
		return result;
	}

	public R transformDisjunction(Collection<R> subconditions);

	public R transformConjunction(Collection<R> subconditions);

	public R transformImplication(R left, R right);

	public R transformEquivalence(R left, R right);

	public R transformExclusiveDisjunction(R left, R right);

	public R transformNegation(R sub);

	public R transformArgument(Argument argument);

	public R transformContradiction();

	public R transformTautology();
}
