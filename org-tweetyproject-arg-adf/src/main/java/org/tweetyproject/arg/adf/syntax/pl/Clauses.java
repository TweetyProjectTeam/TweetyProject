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
 * Helper types for representing propositional clauses in ADF syntax.
 *
 * @author Mathias Hofer
 */
class Clauses {

	/** Creates the utility container. */
	private Clauses() {
		// utility class
	}

	/**
	 * Represents the empty clause.
	 */
	enum Clause0 implements Clause {
		/** the singleton instance */
		INSTANCE;

		@Override
		public Iterator<Literal> iterator() {
			return Collections.emptyIterator();
		}

		@Override
		public Stream<Literal> stream() {
			return Stream.empty();
		}

		/** Returns the number of literals in this clause. */
		@Override
		public int size() {
			return 0;
		}

		/** Returns the textual representation of this clause. */
		@Override
		public String toString() {
			return "()";
		}
	}

	/**
	 * Represents a clause with one literal.
	 */
	static final class Clause1 implements Clause {

		/** The single literal of the clause. */
		private final Literal l1;

		/**
		 * Creates a clause with one literal.
		 *
		 * @param l1 the literal of the clause
		 */
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

		/** Returns the number of literals in this clause. */
		@Override
		public int size() {
			return 1;
		}
		
		/** Returns the textual representation of this clause. */
		@Override
		public String toString() {
			return "(" + l1 + ")";
		}
	}

	/**
	 * Represents a clause with two literals.
	 */
	static final class Clause2 implements Clause {

		/** The first literal of the clause. */
		private final Literal l1;
		/** The second literal of the clause. */
		private final Literal l2;

		/**
		 * Creates a clause with two literals.
		 *
		 * @param l1 the first literal
		 * @param l2 the second literal
		 */
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

		/** Returns the number of literals in this clause. */
		@Override
		public int size() {
			return 2;
		}

		/** Returns the textual representation of this clause. */
		@Override
		public String toString() {
			return "(" + l1 + ", " + l2 + ")";
		}
		
	}

	/**
	 * Represents a clause with three literals.
	 */
	static final class Clause3 implements Clause {

		/** The first literal of the clause. */
		private final Literal l1;
		/** The second literal of the clause. */
		private final Literal l2;
		/** The third literal of the clause. */
		private final Literal l3;

		/**
		 * Creates a clause with three literals.
		 *
		 * @param l1 the first literal
		 * @param l2 the second literal
		 * @param l3 the third literal
		 */
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

		/** Returns the number of literals in this clause. */
		@Override
		public int size() {
			return 3;
		}

		/** Returns the textual representation of this clause. */
		@Override
		public String toString() {
			return "(" + l1 + ", " + l2 + ", " + l3 + ")";
		}
	}

	/**
	 * Represents a clause with an arbitrary number of literals.
	 */
	static final class ClauseN implements Clause {

		/** The literals of the clause. */
		private final Literal[] literals;

		/**
		 * Creates a clause with an arbitrary number of literals.
		 *
		 * @param literals the literals of the clause
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

		/** Returns the number of literals in this clause. */
		@Override
		public int size() {
			return literals.length;
		}

		/** Returns the textual representation of this clause. */
		@Override
		public String toString() {
			return Arrays.toString(literals);
		}
	}

	/**
	 * Represents a clause with an additional extension literal.
	 */
	static final class ExtendedClause implements Clause {

		/** The base clause. */
		private final Clause clause;

		/** The additional extension literal. */
		private final Literal extension;

		/**
		 * Creates a clause extended by one literal.
		 *
		 * @param clause the base clause
		 * @param extension the additional literal
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

		/** Returns the number of literals in this clause. */
		@Override
		public int size() {
			return clause.size() + 1;
		}
	}
	
}
