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

public final class NegationAcceptanceCondition implements AcceptanceCondition {

	private final AcceptanceCondition child;
	
	/**
	 * 
	 * @param child the child of the negation
	 */
	public NegationAcceptanceCondition(AcceptanceCondition child) {
		this.child = Objects.requireNonNull(child);
	}
	
	/**
	 * @return the child
	 */
	public AcceptanceCondition getChild() {
		return child;
	}
	
	@Override
	public Set<AcceptanceCondition> getChildren() {
		return Set.of(child);
	}

	@Override
	public <U, D> U accept(Visitor<U, D> visitor, D topDownData) {
		return visitor.visit(this, topDownData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(child);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		NegationAcceptanceCondition other = (NegationAcceptanceCondition) obj;
		return Objects.equals(child, other.child);
	}
	
	@Override
	public String toString() {
		return new StringBuilder("neg(")
				.append(child)
				.append(")")
				.toString();
	}

}
