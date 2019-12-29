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
import java.util.function.Consumer;

import net.sf.tweety.arg.adf.transform.Transform;

public final class DisjunctionAcceptanceCondition extends AssociativeAcceptanceCondition {


	/**
	 * @param subconditions the sub conditions
	 */
	public DisjunctionAcceptanceCondition(AcceptanceCondition... subconditions) {
		super(subconditions);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.AssociativeAcceptanceCondition#transform(net.sf.tweety.arg.adf.transform.Transform, java.util.function.Consumer, java.util.Collection, int)
	 */
	@Override
	protected <C, R> R transform(Transform<C, R> transform, Consumer<C> consumer, Collection<R> subconditions,
			int polarity) {
		return transform.transformDisjunction(consumer, subconditions, polarity);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.AcceptanceCondition#getName()
	 */
	@Override
	protected String getName() {
		return "or";
	}


	
}
