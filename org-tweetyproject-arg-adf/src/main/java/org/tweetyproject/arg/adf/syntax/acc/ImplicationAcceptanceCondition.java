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

public final class ImplicationAcceptanceCondition extends AbstractAcceptanceCondition {

	private final AcceptanceCondition left;

	private final AcceptanceCondition right;

	/**
	 * @param left the left side of the implication
	 * @param right the right side of the implication
	 */
	public ImplicationAcceptanceCondition(AcceptanceCondition left, AcceptanceCondition right) {
		super(Set.of(left, right));
		this.left = left;
		this.right = right;
	}

	/**
	 * @return the left
	 */
	public AcceptanceCondition getLeft() {
		return left;
	}

	/**
	 * @return the right
	 */
	public AcceptanceCondition getRight() {
		return right;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition#accept(org.tweetyproject
	 * .arg.adf.syntax.acc.Visitor, java.lang.Object)
	 */
	@Override
	public <U, D> U accept(Visitor<U, D> visitor, D topDownData) {
		return visitor.visit(this, topDownData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition#getName()
	 */
	@Override
	public String getName() {
		return "impl";
	}

	/*
	 * We implement hashcode() and equals() again because the order matter for
	 * implication, and the super implementation is based on Set.
	 */
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ImplicationAcceptanceCondition)) {
			return false;
		}
		ImplicationAcceptanceCondition other = (ImplicationAcceptanceCondition) obj;
		return Objects.equals(left, other.left) && Objects.equals(right, other.right);
	}

}
