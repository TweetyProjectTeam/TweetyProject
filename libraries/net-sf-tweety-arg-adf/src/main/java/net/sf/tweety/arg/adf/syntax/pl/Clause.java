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

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Mathias Hofer
 *
 */
public interface Clause extends Iterable<Literal> {
	
	static Clause of(Set<Literal> literals) {
		return new SetClause(literals);
	}
	
	static Clause of(Collection<? extends Literal> literals, Literal... additional) {
		Literal[] array = new Literal[literals.size() + additional.length];
		copyTo(array, literals, 0);
		copyTo(array, additional, literals.size());
		return new ArrayClause(array);
	}
	
	static Clause of(Clause clause, Literal... additional) {
		Literal[] array = new Literal[clause.size() + additional.length];
		copyTo(array, clause, 0);
		copyTo(array, additional, clause.size());		
		return new ArrayClause(array);
	}
	
	static Clause of() {
		return EmptyClause.INSTANCE;
	}
	
	static Clause of(Literal l) {
		return new SingletonClause(l);
	}
	
	static Clause of(Literal l1, Literal l2) {
		Objects.requireNonNull(l1);
		Objects.requireNonNull(l2);
		return new ArrayClause(new Literal[] {l1, l2});
	}
	
	static Clause of(Literal l1, Literal l2, Literal l3) {
		Objects.requireNonNull(l1);
		Objects.requireNonNull(l2);
		Objects.requireNonNull(l3);
		return new ArrayClause(new Literal[] {l1, l2, l3});
	}
	
	static Clause lazyMapping(Clause clause, Function<Literal, Literal> mapping) {
		return new MappedClause(clause, mapping);
	}
	
	Stream<Literal> stream();
	
	int size();
	
	private static void copyTo(Literal[] dst, Iterable<? extends Literal> src, int offset) {
		for (Literal literal : src) {
			dst[offset++] = literal;
		}
	}
	
	private static void copyTo(Literal[] dst, Literal[] src, int offset) {
		for (Literal literal : src) {
			dst[offset++] = literal;
		}
	}
	
}
