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
import java.util.stream.Stream;

import net.sf.tweety.arg.adf.transform.Transform;

/**
 * @author Mathias Hofer
 *
 */
public abstract class UnaryAcceptanceCondition extends AcceptanceCondition {

	private AcceptanceCondition sub;

	/**
	 * @param sub
	 */
	public UnaryAcceptanceCondition(AcceptanceCondition sub) {
		this.sub = sub;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.AcceptanceCondition#arguments()
	 */
	@Override
	public Stream<Argument> arguments() {
		return sub.arguments();
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
		R transformedSub = sub.transform(transform, consumer, subPolarity(polarity));
		return transform(transform, consumer, transformedSub, polarity);
	}

	protected abstract <C, R> R transform(Transform<C, R> transform, Consumer<C> consumer, R sub, int polarity);

	protected abstract int subPolarity(int polarity);

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.AcceptanceCondition#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder(getName()).append("(").append(sub).append(")").toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sub == null) ? 0 : sub.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		UnaryAcceptanceCondition other = (UnaryAcceptanceCondition) obj;
		if (sub == null) {
			if (other.sub != null)
				return false;
		} else if (!sub.equals(other.sub))
			return false;
		return true;
	}
}
