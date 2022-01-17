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

import java.util.Set;
import java.util.stream.Stream;

import org.tweetyproject.arg.adf.syntax.Argument;

/**
 * An immutable representation of acceptance conditions for ADFs.
 * <p>
 * Mirrors the structure of org.tweetyproject.logics.pl.syntax.PlFormula.
 * 
 * @author Mathias Hofer
 *
 */
public interface AcceptanceCondition {
	
	/**
	 * This constant is an alias for {@link ContradictionAcceptanceCondition#INSTANCE}, its only purpose is readability.
	 * <p>
	 * It is guaranteed that there is only one instance of the logical constant, therefore it is safe to perform == checks.
	 */
	static final ContradictionAcceptanceCondition CONTRADICTION = ContradictionAcceptanceCondition.INSTANCE;
	
	/**
	 * This constant is an alias for {@link TautologyAcceptanceCondition#INSTANCE}, its only purpose is readability.
	 * <p>
	 * It is guaranteed that there is only one instance of the logical constant, therefore it is safe to perform == checks.
	 */
	static final TautologyAcceptanceCondition TAUTOLOGY = TautologyAcceptanceCondition.INSTANCE;	
	
	/**
	 * 
	 * @return recursively computes all the arguments of this acceptance condition
	 */
	default Stream<Argument> arguments() {
		return getChildren()
				.stream()
				.flatMap(AcceptanceCondition::arguments);
	}
	
	/**
	 * 
	 * @return an unmodifiable set of children
	 */
	Set<AcceptanceCondition> getChildren();
	
	/**
	 * Passes the topDownData to the right visit method and returns the result of the visit method, performs no modifications on them.
	 * <p>
	 * This allows for type-safe traversal through the acceptance condition structure.
	 * 
	 * @param <U> the bottom-up data
	 * @param <D> the top-down data
	 * @param visitor the visitor
	 * @param topDownData the data which is passed from the root of the acceptance condition to the leaf
	 * @return the result of the visit method
	 */
	<U, D> U accept(Visitor<U, D> visitor, D topDownData);
	
	/**
	 * Checks if the given argument is contained in this acceptance condition.
	 * <p>
	 * Note that this relation is reflexive, hence each argument contains itself.
	 * 
	 * @param arg some argument
	 * @return true if the argument is contained
	 */
	default boolean contains(Argument arg) {
		return getChildren()
				.stream()
				.anyMatch(x -> x.contains(arg));
	}
	
	/**
	 * Returns a left-associative builder.
	 * 
	 * @param acc the base acceptance condition, e.g. an argument
	 * @return a builder
	 */
	static Builder builder(AcceptanceCondition acc) {
		return new Builder(acc);
	}
	/**
	 * 
	 * @author Sebastian
	 *
	 */
	static final class Builder {
		
		private AcceptanceCondition left;
		
		private Builder(AcceptanceCondition left) {
			this.left = left;
		}
		/**
		 * 
		 * @param acc acc
		 * @return Builder and
		 */
		public Builder and(AcceptanceCondition acc) {
			this.left = new ConjunctionAcceptanceCondition(left, acc);
			return this;
		}
	/**
	 * 	
	 * @param accs accs
	 * @return Builder and
	 */
		public Builder and(AcceptanceCondition... accs) {
			for (AcceptanceCondition acc : accs) {
				this.left = new ConjunctionAcceptanceCondition(left, acc);				
			}
			return this;
		}
	/**
	 * 	
	 * @param acc acc
	 * @return Builder or
	 */
		public Builder or(AcceptanceCondition acc) {
			this.left = new DisjunctionAcceptanceCondition(left, acc);
			return this;
		}
		/**
		 * 
		 * @param accs accs
		 * @return Builder or
		 */
		public Builder or(AcceptanceCondition... accs) {
			for (AcceptanceCondition acc : accs) {
				this.left = new DisjunctionAcceptanceCondition(left, acc);				
			}
			return this;
		}
		/**
		 * 
		 * @param acc acc
		 * @return Builder implies
		 */
		public Builder implies(AcceptanceCondition acc) {
			this.left = new ImplicationAcceptanceCondition(left, acc);
			return this;
		}
	/**
	 * 	
	 * @param acc acc
	 * @return Builder equiv
	 */
		public Builder equiv(AcceptanceCondition acc) {
			this.left = new EquivalenceAcceptanceCondition(left, acc);
			return this;
		}
		/**
		 * 
		 * @param accs accs
		 * @return Builder equiv
		 */
		public Builder equiv(AcceptanceCondition... accs) {
			for (AcceptanceCondition acc : accs) {
				this.left = new EquivalenceAcceptanceCondition(left, acc);				
			}
			return this;
		}
/**
 * 		
 * @param acc acc
 * @return Builder xor
 */
		public Builder xor(AcceptanceCondition acc) {
			this.left = new ExclusiveDisjunctionAcceptanceCondition(left, acc);
			return this;
		}
		/**
		 * 
		 * @return Builder neg
		 */
		public Builder neg() {
			this.left = new NegationAcceptanceCondition(left);
			return this;
		}
		/**
		 * 
		 * @return AcceptanceCondition build
		 */
		public AcceptanceCondition build() {
			return left;
		}
	}
}
