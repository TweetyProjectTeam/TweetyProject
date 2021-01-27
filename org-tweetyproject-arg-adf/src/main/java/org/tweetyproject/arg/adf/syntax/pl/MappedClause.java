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
package org.tweetyproject.arg.adf.syntax.pl;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Lazily applies a mapping to a given clause.
 * 
 * @author Mathias Hofer
 *
 */
final class MappedClause implements Clause {
	
	private final Clause clause;
	
	private final Function<? super Literal, ? extends Literal> mapping;

	/**
	 * 
	 * @param clause
	 * @param mapping
	 */
	public MappedClause(Clause clause, Function<? super Literal, ? extends Literal> mapping) {
		this.clause = Objects.requireNonNull(clause);
		this.mapping = Objects.requireNonNull(mapping);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.adf.syntax.pl.Clause#stream()
	 */
	@Override
	public Stream<Literal> stream() {
		return clause.stream().map(mapping);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Literal> iterator() {
		return stream().iterator();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.adf.syntax.pl.Clause#size()
	 */
	@Override
	public int size() {
		return clause.size();
	}
	
	
}
