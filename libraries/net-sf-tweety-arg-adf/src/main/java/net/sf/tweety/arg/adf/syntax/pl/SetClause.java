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
package net.sf.tweety.arg.adf.syntax.pl;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Backed by a set of literals.
 * 
 * @author Mathias Hofer
 *
 */
final class SetClause implements Clause {

	private final Set<Literal> literals;
	
	/**
	 * @param literals
	 */
	public SetClause(Set<Literal> literals) {
		this.literals = Objects.requireNonNull(literals);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return literals.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Literal> iterator() {
		return literals.iterator();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.pl.Clause#stream()
	 */
	@Override
	public Stream<Literal> stream() {
		return literals.stream();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.pl.Clause#size()
	 */
	@Override
	public int size() {
		return literals.size();
	}

}
