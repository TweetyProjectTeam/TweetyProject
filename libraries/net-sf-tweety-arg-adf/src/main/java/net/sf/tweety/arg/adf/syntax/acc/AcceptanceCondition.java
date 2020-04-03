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
import java.util.stream.Stream;

import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * An immutable representation of acceptance conditions for ADFs.
 * <p>
 * Mirrors the structure of {@link PlFormula}.
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
	 * Passes the topDownData to the visitor and returns the result of the
	 * visitors visit method, performs no modifications on them.
	 * 
	 * @param <U>
	 * @param <D>
	 * @param visitor
	 * @param topDownData
	 * @return
	 */
	<U, D> U accept(Visitor<U, D> visitor, D topDownData);
	
	default boolean contains(Argument arg) {
		return getChildren()
				.stream()
				.anyMatch(x -> x.contains(arg));
	}
	
	static Builder from(AcceptanceCondition acc) {
		return new Builder(acc);
	}
	
	public static final class Builder {
		
		private AcceptanceCondition left;
		
		private Builder(AcceptanceCondition left) {
			this.left = left;
		}
		
//		public Builder and(AcceptanceCondition acc) {
//			this.left = new ConjunctionAcceptanceCondition(left, acc);
//			return this;
//		}
//		
//		public Builder and(AcceptanceCondition... accs) {
//			this.left = new ConjunctionAcceptanceCondition(left, new ConjunctionAcceptanceCondition(accs));
//			return this;
//		}
//		
//		public Builder or(AcceptanceCondition acc) {
//			this.left = new DisjunctionAcceptanceCondition(left, acc);
//			return this;
//		}
//		
//		public Builder or(AcceptanceCondition... accs) {
//			this.left = new DisjunctionAcceptanceCondition(left, new DisjunctionAcceptanceCondition(accs));
//			return this;
//		}
//		
//		public Builder implies(AcceptanceCondition acc) {
//			this.left = new ImplicationAcceptanceCondition(left, acc);
//			return this;
//		}
//		
//		public Builder iff(AcceptanceCondition acc) {
//			this.left = new EquivalenceAcceptanceCondition(left, acc);
//			return this;
//		}
//		
//		public Builder xor(AcceptanceCondition acc) {
//			this.left = new ExclusiveDisjunctionAcceptanceCondition(left, acc);
//			return this;
//		}
//		
//		public Builder neg() {
//			this.left = new NegationAcceptanceCondition(left);
//			return this;
//		}
		
		public AcceptanceCondition build() {
			return left;
		}
	}
}
