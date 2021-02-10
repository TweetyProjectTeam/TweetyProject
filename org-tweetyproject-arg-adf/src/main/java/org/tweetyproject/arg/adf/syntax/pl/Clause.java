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

import java.util.Collection;
import java.util.stream.Stream;

import org.tweetyproject.arg.adf.syntax.pl.Clauses.Clause0;
import org.tweetyproject.arg.adf.syntax.pl.Clauses.Clause1;
import org.tweetyproject.arg.adf.syntax.pl.Clauses.Clause2;
import org.tweetyproject.arg.adf.syntax.pl.Clauses.Clause3;
import org.tweetyproject.arg.adf.syntax.pl.Clauses.ClauseN;
import org.tweetyproject.arg.adf.syntax.pl.Clauses.ExtendedClause;

/**
 * @author Mathias Hofer
 *
 */
public interface Clause extends Iterable<Literal> {

	Stream<Literal> stream();

	int size();

	static Clause of() {
		return Clause0.INSTANCE;
	}

	static Clause of(Literal l) {
		return new Clause1(l);
	}

	static Clause of(Literal l1, Literal l2) {
		return new Clause2(l1, l2);
	}

	static Clause of(Literal l1, Literal l2, Literal l3) {
		return new Clause3(l1, l2, l3);
	}

	static Clause of(Clause c, Literal l) {
		return new ExtendedClause(c, l);
	}

	static Clause of(Collection<? extends Literal> literals) {
		return new ClauseN(literals.toArray(new Literal[0]));
	}

	static Clause of(Collection<? extends Literal> literals, Literal l) {
		Literal[] array = literals.toArray(new Literal[literals.size() + 1]);
		array[array.length - 1] = l;
		return new ClauseN(array);
	}

	static Clause of(Collection<? extends Literal> literals, Literal l1, Literal l2) {
		Literal[] array = literals.toArray(new Literal[literals.size() + 2]);
		array[array.length - 2] = l1;
		array[array.length - 1] = l2;
		return new ClauseN(array);
	}

	static Clause of(Collection<? extends Literal> literals, Literal l1, Literal l2, Literal l3) {
		Literal[] array = literals.toArray(new Literal[literals.size() + 3]);
		array[array.length - 3] = l1;
		array[array.length - 2] = l2;
		array[array.length - 1] = l3;
		return new ClauseN(array);
	}

}
