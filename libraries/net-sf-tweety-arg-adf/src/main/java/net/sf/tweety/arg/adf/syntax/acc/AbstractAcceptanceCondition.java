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

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * @author Mathias Hofer
 *
 */
public abstract class AbstractAcceptanceCondition implements AcceptanceCondition {

	private final Set<AcceptanceCondition> children;
	
	/**
	 * 
	 * @param children the children of this acceptance condition
	 * @throws NullPointerException if children or one of its elements is null
	 */
	public AbstractAcceptanceCondition(Collection<AcceptanceCondition> children) {
		this.children = Set.copyOf(children); // implicit not-null check
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition#getChildren()
	 */
	@Override
	public Set<AcceptanceCondition> getChildren() {
		return children;
	}
	
	abstract String getName();

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(getName());
		builder.append("(");
		Iterator<AcceptanceCondition> iterator = children.iterator();
		builder.append(iterator.next());
		while (iterator.hasNext()) {
			builder.append(",");
			builder.append(iterator.next());
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
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AbstractAcceptanceCondition)) {
			return false;
		}
		AbstractAcceptanceCondition other = (AbstractAcceptanceCondition) obj;
		return Objects.equals(children, other.children);
	}

}
