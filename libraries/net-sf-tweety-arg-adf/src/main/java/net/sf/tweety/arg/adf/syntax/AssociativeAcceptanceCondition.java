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

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.tweety.arg.adf.transform.Transform;

/**
 * @author Mathias Hofer
 *
 */
public abstract class AssociativeAcceptanceCondition extends AcceptanceCondition {

	private AcceptanceCondition[] subconditions;

	/**
	 * @param subconditions
	 */
	public AssociativeAcceptanceCondition(AcceptanceCondition[] subconditions) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.AcceptanceCondition#transform(net.sf.tweety.
	 * arg.adf.transform.Transform, java.util.function.Consumer, int)
	 */
	@Override
	protected <C, R> R transform(Transform<C, R> transform, Consumer<C> consumer, int polarity) {
		Collection<R> transformedSubconditions = Stream.of(subconditions)
				.map(acc -> acc.transform(transform, consumer, polarity)).collect(Collectors.toList());
		return transform(transform, consumer, transformedSubconditions, polarity);
	}

	protected abstract <C, R> R transform(Transform<C, R> transform, Consumer<C> consumer, Collection<R> subconditions,
			int polarity);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(getName());
		builder.append("(").append(subconditions[0]);
		for (int i = 1; i < subconditions.length; i++) {
			builder.append(",").append(subconditions[i]);
		}
		builder.append(")");
		return builder.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(subconditions);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssociativeAcceptanceCondition other = (AssociativeAcceptanceCondition) obj;
		if (!Arrays.equals(subconditions, other.subconditions))
			return false;
		return true;
	}
}
