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
package net.sf.tweety.arg.adf.syntax.acc;

import java.util.Set;

public final class ExclusiveDisjunctionAcceptanceCondition extends AbstractAcceptanceCondition {

	private final AcceptanceCondition left;

	private final AcceptanceCondition right;

	/**
	 * @param left the left side of the xor
	 * @param right the right side of the xor
	 */
	public ExclusiveDisjunctionAcceptanceCondition(AcceptanceCondition left, AcceptanceCondition right) {
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
	 * net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition#accept(net.sf.tweety
	 * .arg.adf.syntax.acc.Visitor, java.lang.Object)
	 */
	@Override
	public <U, D> U accept(Visitor<U, D> visitor, D topDownData) {
		return visitor.visit(this, topDownData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition#getName()
	 */
	@Override
	public String getName() {
		return "xor";
	}

}
