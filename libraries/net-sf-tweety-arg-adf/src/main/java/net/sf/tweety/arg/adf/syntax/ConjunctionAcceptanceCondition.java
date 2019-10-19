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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConjunctionAcceptanceCondition extends AcceptanceCondition {

	private AcceptanceCondition[] subconditions;

	/**
	 * @param subconditions the sub conditions
	 */
	public ConjunctionAcceptanceCondition(AcceptanceCondition... subconditions) {
		this.subconditions = subconditions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.AcceptanceCondition#arguments()
	 */
	@Override
	public Stream<Argument> arguments() {
		return Stream.of(subconditions).flatMap(x -> x.arguments());
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.AcceptanceCondition#transform(net.sf.tweety.arg.adf.syntax.Transform, java.util.function.Consumer)
	 */
	@Override
	protected <C, R> R transform(Transform<C, R> transform, Consumer<C> consumer, int polarity) {
		Collection<R> transformedSubconditions = Stream.of(subconditions).map(acc -> acc.transform(transform, consumer, polarity)).collect(Collectors.toList());
		return transform.transformConjunction(consumer, transformedSubconditions, polarity);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		boolean first = true;
		StringBuilder builder = new StringBuilder("and(");
		for (AcceptanceCondition sub : subconditions) {
			if (first) {
				builder.append(sub.toString());
				first = false;
			} else {
				builder.append("," + sub.toString());
			}
		}
		builder.append(")");
		return builder.toString();
	}
}
