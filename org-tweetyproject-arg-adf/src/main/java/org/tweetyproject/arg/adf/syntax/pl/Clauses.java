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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Mathias Hofer
 *
 */
class Clauses {

	enum Clause0 implements Clause {
		INSTANCE;

		@Override
		public Iterator<Literal> iterator() {
			return Collections.emptyIterator();
		}

		@Override
		public Stream<Literal> stream() {
			return Stream.empty();
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public String toString() {
			return "[]";
		}
	}

	static final class Clause1 implements Clause {

		private final Literal l1;

		Clause1(Literal l1) {
			this.l1 = Objects.requireNonNull(l1);
		}

		@Override
		public Iterator<Literal> iterator() {
			return Stream.of(l1).iterator();
		}

		@Override
		public Stream<Literal> stream() {
			return Stream.of(l1);
		}

		@Override
		public int size() {
			return 1;
		}
	}

	static final class Clause2 implements Clause {

		private final Literal l1;
		private final Literal l2;

		Clause2(Literal l1, Literal l2) {
			this.l1 = Objects.requireNonNull(l1);
			this.l2 = Objects.requireNonNull(l2);
		}

		@Override
		public Iterator<Literal> iterator() {
			return Stream.of(l1, l2).iterator();
		}

		@Override
		public Stream<Literal> stream() {
			return Stream.of(l1, l2);
		}

		@Override
		public int size() {
			return 2;
		}
	}

	static final class Clause3 implements Clause {

		private final Literal l1;
		private final Literal l2;
		private final Literal l3;

		Clause3(Literal l1, Literal l2, Literal l3) {
			this.l1 = Objects.requireNonNull(l1);
			this.l2 = Objects.requireNonNull(l2);
			this.l3 = Objects.requireNonNull(l3);
		}

		@Override
		public Iterator<Literal> iterator() {
			return Stream.of(l1, l2, l3).iterator();
		}

		@Override
		public Stream<Literal> stream() {
			return Stream.of(l1, l2, l3);
		}

		@Override
		public int size() {
			return 3;
		}
	}

	static final class ClauseN implements Clause {

		private final Literal[] literals;

		/**
		 * @param literals
		 */
		ClauseN(Literal[] literals) {
			this.literals = Objects.requireNonNull(literals);
		}

		@Override
		public Iterator<Literal> iterator() {
			return Stream.of(literals).iterator();
		}

		@Override
		public Stream<Literal> stream() {
			return Stream.of(literals);
		}

		@Override
		public int size() {
			return literals.length;
		}

		@Override
		public String toString() {
			return Arrays.toString(literals);
		}
	}

	static final class ExtendedClause implements Clause {

		private final Clause clause;

		private final Literal extension;

		/**
		 * @param clause
		 * @param extension
		 */
		ExtendedClause(Clause clause, Literal extension) {
			this.clause = Objects.requireNonNull(clause);
			this.extension = Objects.requireNonNull(extension);
		}

		@Override
		public Iterator<Literal> iterator() {
			return stream().iterator();
		}

		@Override
		public Stream<Literal> stream() {
			return Stream.concat(clause.stream(), Stream.of(extension));
		}

		@Override
		public int size() {
			return clause.size() + 1;
		}
	}
	
}
