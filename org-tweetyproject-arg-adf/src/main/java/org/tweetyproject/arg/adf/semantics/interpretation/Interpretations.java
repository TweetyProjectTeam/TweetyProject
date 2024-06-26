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
package org.tweetyproject.arg.adf.semantics.interpretation;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.util.UnionSetView;

/**
 * @author Mathias Hofer
 *
 */
final class Interpretations {

	static final class EmptyInterpretation implements Interpretation {

		private final Set<Argument> undecided;
		
		/**
		 * An empty interpretation relative to the given ADF
		 * 
		 * @param adf the contextual ADF
		 */
		EmptyInterpretation(AbstractDialecticalFramework adf) {
			this.undecided = Set.copyOf(adf.getArguments());
		}

		@Override
		public boolean satisfied(Argument arg) {
			return false;
		}

		@Override
		public boolean unsatisfied(Argument arg) {
			return false;
		}

		@Override
		public boolean undecided(Argument arg) {
			return undecided.contains(arg);
		}
		
		@Override
		public Set<Argument> arguments() {
			return undecided;
		}

		@Override
		public Set<Argument> satisfied() {
			return Set.of();
		}

		@Override
		public Set<Argument> unsatisfied() {
			return Set.of();
		}

		@Override
		public Set<Argument> undecided() {
			return undecided;
		}
		
		@Override
		public boolean isSubsetOf(Interpretation superset) {
			return true;
		}
		
		@Override
		public int numDecided() {
			return 0;
		}
		
		@Override
		public boolean isStrictSupersetOf(Interpretation subset) {
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(undecided);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Interpretation)) {
				return false;
			}
			Interpretation other = (Interpretation) obj;
			return Objects.equals(undecided, other.undecided());
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder("{");
			Iterator<Argument> iterator = undecided.iterator();
			builder.append("u(");
			builder.append(iterator.next());
			builder.append(")");
			while (iterator.hasNext()) {
				builder.append(" u(");
				builder.append(iterator.next());
				builder.append(")");
			}
			builder.append("}");
			return builder.toString();
		}
	}
	
	/**
	 * This implementation is backed by three sets which allows
	 * {@link #satisfied(Argument)}, {@link #unsatisfied(Argument)} and {@link #undecided(Argument)} to
	 * return in constant time.
	 * 
	 * @author Mathias Hofer
	 *
	 */
	static final class SetInterpretation implements Interpretation {

		private final Set<Argument> satisfied;

		private final Set<Argument> unsatisfied;

		private final Set<Argument> undecided;

		/**
		 * Note: Only wraps the parameters in unmodifiable views, it is therefore up to
		 * the caller that the given sets cannot be modified from somewhere else.
		 * 
		 * @param satisfied   the satisfied arguments
		 * @param unsatisfied the unsatisfied arguments
		 * @param undecided   the undecided arguments
		 */
		SetInterpretation(Set<Argument> satisfied, Set<Argument> unsatisfied, Set<Argument> undecided) {
			if (satisfied.isEmpty() && unsatisfied.isEmpty() && undecided.isEmpty()) {
				throw new IllegalArgumentException("There must be at least one non-empty set!");
			}
			this.satisfied = Collections.unmodifiableSet(satisfied);
			this.unsatisfied = Collections.unmodifiableSet(unsatisfied);
			this.undecided = Collections.unmodifiableSet(undecided);
		}

		@Override
		public boolean satisfied(Argument arg) {
			return satisfied.contains(arg);
		}

		@Override
		public boolean unsatisfied(Argument arg) {
			return unsatisfied.contains(arg);
		}

		@Override
		public boolean undecided(Argument arg) {
			return undecided.contains(arg);
		}

		@Override
		public Set<Argument> satisfied() {
			return satisfied;
		}
		
		@Override
		public Set<Argument> arguments() {
			return UnionSetView.of(satisfied, unsatisfied, undecided);
		}

		@Override
		public Set<Argument> unsatisfied() {
			return unsatisfied;
		}

		@Override
		public Set<Argument> undecided() {
			return undecided;
		}

		@Override
		public int hashCode() {
			return Objects.hash(satisfied, undecided, unsatisfied);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Interpretation)) {
				return false;
			}
			Interpretation other = (Interpretation) obj;
			return Objects.equals(satisfied, other.satisfied()) && Objects.equals(undecided, other.undecided())
					&& Objects.equals(unsatisfied, other.unsatisfied());
		}

		public String toString() {
			StringBuilder s = new StringBuilder("{");
			boolean first = true;
			for (Argument a : satisfied) {
				if (first) {
					s.append("t(" + a + ")");
					first = false;
				} else
					s.append(" t(" + a + ")");
			}
			for (Argument a : unsatisfied) {
				s.append(" f(" + a + ")");
			}
			for (Argument a : undecided) {
				s.append(" u(" + a + ")");
			}
			s.append("}");
			return s.toString();
		}
	}
	
	static final class SingleSatisfiedInterpretation implements Interpretation {

		private final Argument argument;

		SingleSatisfiedInterpretation(Argument argument) {
			this.argument = Objects.requireNonNull(argument);
		}

		@Override
		public boolean satisfied(Argument arg) {
			return arg.equals(argument); // implicit null-check
		}

		@Override
		public boolean unsatisfied(Argument arg) {
			return false;
		}

		@Override
		public boolean undecided(Argument arg) {
			return false;
		}

		@Override
		public Set<Argument> satisfied() {
			return Set.of(argument);
		}

		@Override
		public Set<Argument> unsatisfied() {
			return Set.of();
		}

		@Override
		public Set<Argument> undecided() {
			return Set.of();
		}
		
		@Override
		public int numDecided() {
			return 1;
		}

		@Override
		public Set<Argument> arguments() {
			return Set.of(argument);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(argument);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Interpretation)) {
				return false;
			}
			Interpretation other = (Interpretation) obj;
			return other.size() == 1 && other.satisfied(argument);
		}

		@Override
		public String toString() {
			return new StringBuilder("{t(").append("argument").append(")}").toString();
		}
	}
	
	static final class SingleUnsatisfiedInterpretation implements Interpretation {

		private final Argument argument;

		SingleUnsatisfiedInterpretation(Argument argument) {
			this.argument = Objects.requireNonNull(argument);
		}

		@Override
		public boolean satisfied(Argument arg) {
			return false;
		}

		@Override
		public boolean unsatisfied(Argument arg) {
			return arg.equals(argument);
		}

		@Override
		public boolean undecided(Argument arg) {
			return false;
		}

		@Override
		public Set<Argument> satisfied() {
			return Set.of();
		}

		@Override
		public Set<Argument> unsatisfied() {
			return Set.of(argument);
		}

		@Override
		public Set<Argument> undecided() {
			return Set.of();
		}
		
		@Override
		public int numDecided() {
			return 1;
		}

		@Override
		public Set<Argument> arguments() {
			return Set.of(argument);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(argument);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Interpretation)) {
				return false;
			}
			Interpretation other = (Interpretation) obj;
			return other.size() == 1 && other.unsatisfied(argument);
		}

		@Override
		public String toString() {
			return new StringBuilder("{f(").append("argument").append(")}").toString();
		}
	}
	
	static final class SingleUndecidedInterpretation implements Interpretation {

		private final Argument argument;

		SingleUndecidedInterpretation(Argument argument) {
			this.argument = Objects.requireNonNull(argument);
		}

		@Override
		public boolean satisfied(Argument arg) {
			return false;
		}

		@Override
		public boolean unsatisfied(Argument arg) {
			return false;
		}

		@Override
		public boolean undecided(Argument arg) {
			return arg.equals(argument);
		}

		@Override
		public Set<Argument> satisfied() {
			return Set.of();
		}

		@Override
		public Set<Argument> unsatisfied() {
			return Set.of();
		}

		@Override
		public Set<Argument> undecided() {
			return Set.of(argument);
		}
		
		@Override
		public int numDecided() {
			return 0;
		}

		@Override
		public Set<Argument> arguments() {
			return Set.of(argument);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(argument);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Interpretation)) {
				return false;
			}
			Interpretation other = (Interpretation) obj;
			return other.size() == 1 && other.undecided(argument);
		}

		@Override
		public String toString() {
			return new StringBuilder("{u(").append("argument").append(")}").toString();
		}
	}

}
