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
package org.tweetyproject.arg.adf.syntax.acc;

import java.util.Objects;
import java.util.Set;

import org.tweetyproject.arg.adf.syntax.Argument;

public enum TautologyAcceptanceCondition implements AcceptanceCondition {
	INSTANCE;
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition#contains(org.tweetyproject.arg.adf.syntax.Argument)
	 */
	@Override
	public boolean contains(Argument arg) {
		Objects.requireNonNull(arg);
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition#children()
	 */
	@Override
	public Set<AcceptanceCondition> getChildren() {
		return Set.of();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition#accept(org.tweetyproject.arg.adf.syntax.acc.Visitor, java.lang.Object)
	 */
	@Override
	public <U, D> U accept(Visitor<U, D> visitor, D topDownData) {
		return visitor.visit(this, topDownData);
	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return "T";
	}

}
