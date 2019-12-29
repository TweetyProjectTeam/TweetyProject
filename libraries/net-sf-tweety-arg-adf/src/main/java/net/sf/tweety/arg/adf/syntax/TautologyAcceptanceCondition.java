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

public final class TautologyAcceptanceCondition extends AcceptanceCondition {

	@Override
	public Stream<Argument> arguments() {
		return Stream.empty();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.AcceptanceCondition#isTautology()
	 */
	@Override
	public boolean isTautology() {
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.AcceptanceCondition#transform(net.sf.tweety.arg.adf.syntax.Transform, java.util.function.Consumer)
	 */
	@Override
	protected <C, R> R transform(Transform<C, R> transform, Consumer<C> consumer, int polarity) {
		return transform.transformTautology(consumer, polarity);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.AcceptanceCondition#getName()
	 */
	@Override
	protected String getName() {
		return "c(v)";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof TautologyAcceptanceCondition;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return 13;
	}
}
