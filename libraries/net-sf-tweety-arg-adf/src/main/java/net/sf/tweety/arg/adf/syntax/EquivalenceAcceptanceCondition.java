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

import java.util.function.Consumer;

import net.sf.tweety.arg.adf.transform.Transform;

public final class EquivalenceAcceptanceCondition extends BinaryAcceptanceCondition {

	/**
	 * @param left
	 * @param right
	 */
	public EquivalenceAcceptanceCondition(AcceptanceCondition left, AcceptanceCondition right) {
		super(left, right);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.BinaryAcceptanceCondition#leftPolarity(int)
	 */
	@Override
	protected int leftPolarity(int polarity) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.BinaryAcceptanceCondition#rightPolarity(int)
	 */
	@Override
	protected int rightPolarity(int polarity) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.BinaryAcceptanceCondition#transform(net.sf.tweety.arg.adf.transform.Transform, java.util.function.Consumer, java.lang.Object, java.lang.Object, int)
	 */
	@Override
	protected <C, R> R transform(Transform<C, R> transform, Consumer<C> consumer, R left, R right, int polarity) {
		return transform.transformEquivalence(consumer, left, right, polarity);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.AcceptanceCondition#getName()
	 */
	@Override
	protected String getName() {
		return "iff";
	}
}
