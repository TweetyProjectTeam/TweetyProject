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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.lp.asp.syntax;

import java.util.Set;

import org.tweetyproject.logics.commons.syntax.Predicate;

/**
 * This class collects some common operators used in ASP terms as well as the
 * possible function names for aggregates.
 * 
 * @author Anna Gessler
 */
public class ASPOperator {

	/**
	 * The arithmetic operators that are supported by the ASP-Core-2 standard: PLUS
	 * (+), MINUS (-), TIMES (*), DIV (/) 
	 * <br> In addition, the following operators from
	 * Clingo and DLV are supported: MODULO (\)
	 * 
	 * @author Anna Gessler
	 */
	public enum ArithmeticOperator {
		PLUS, MINUS, TIMES, DIV, MODULO;

		@Override
		public String toString() {
			if (this.equals(ArithmeticOperator.PLUS))
				return "+";
			if (this.equals(ArithmeticOperator.MINUS))
				return "-";
			if (this.equals(ArithmeticOperator.TIMES))
				return "*";
			if (this.equals(ArithmeticOperator.DIV))
				return "/";
			if (this.equals(ArithmeticOperator.MODULO))
				return "\\";
			else
				throw new IllegalArgumentException("Unknown arithmetic operator");
		}
		
	}

	/**
	 * The binary comparative operators that are supported by the ASP-Core-2
	 * standard and by Tweety: LT (&lt;), LEQ (&lt;=), EQ (==), NEQ (!=), GT (&gt;),
	 * GEQ (&gt;=)
	 * 
	 * @author Anna Gessler
	 */
	public enum BinaryOperator {
		LT, LEQ, EQ, NEQ, GT, GEQ;

		@Override
		public String toString() {
			if (this.equals(BinaryOperator.LT))
				return "<";
			if (this.equals(BinaryOperator.LEQ))
				return "<=";
			if (this.equals(BinaryOperator.EQ))
				return "==";
			if (this.equals(BinaryOperator.NEQ))
				return "!=";
			if (this.equals(BinaryOperator.GT))
				return ">";
			else
				return ">=";
		}
	}

	/**
	 * The following aggregate functions are supported by the ASP-Core-2 standard
	 * and by Tweety. 'weight' in this context is the first element of an aggregate
	 * element (term tuple).
	 * 
	 * <br>
	 * - #count: Number of elements, used to represent cardinality constraints. <br>
	 * - #sum: Sum of weights, used to represent weight constraints. <br>
	 * - #max: Maximum weight <br>
	 * - #min: Minimum weight
	 * 
	 * <br> Clingo additionally includes the #sum+ aggregate function, which represents
	 * the sum of positive weights. 
	 * <br> DLV additionally includes the #times aggregate
	 * function. #times is similar to #sum, but computes the product of the first local variable to be aggregated over 
	 * in the symbolic set. For the empty set, #times returns 1. 
	 * 
	 * @author Anna Gessler
	 */
	public enum AggregateFunction {
		COUNT, MAX, MIN, SUM, SUM_PLUS, TIMES;

		@Override
		public String toString() {
			if (this.equals(AggregateFunction.MAX))
				return "#max";
			if (this.equals(AggregateFunction.MIN))
				return "#min";
			if (this.equals(AggregateFunction.COUNT))
				return "#count";
			if (this.equals(AggregateFunction.SUM))
				return "#sum";
			if (this.equals(AggregateFunction.SUM_PLUS))
				return "#sum+";
			if (this.equals(AggregateFunction.TIMES))
				return "#times";
			else
				throw new IllegalArgumentException("Unknown aggregate function");
		}
	}

	public enum OptimizeFunction {
		MINIMIZE, MAXIMIZE;

		@Override
		public String toString() {
			if (this.equals(OptimizeFunction.MINIMIZE))
				return "#minimize";
			if (this.equals(OptimizeFunction.MAXIMIZE))
				return "#maximize";
			else
				throw new IllegalArgumentException("Unknown optimize function");
		}

	}
	

	/**
	 * Additional special predicates of the DLV syntax that have no direct 
	 * representation in the Clingo or ASP-Core-2 format.
	 * 
	 * @author Anna Gessler
	 */
	public static class DLVPredicate extends Predicate {
		final Set<String> NAMES = Set.of("#int", "#rand", "#absdiff", "#append", "#delnth", "#flatten", "#getnth",
				"#head", "#insLast", "#insnth", "#last", "#length", "#member", "#reverse", "#subList", "#tail");

		public DLVPredicate(String name, int arity) {
			super(name,arity);
			if (!NAMES.contains(name))
				throw new IllegalArgumentException(name + " is not a known DLVPredicate.");
			if (arity == 0 || arity > 3)
				throw new IllegalArgumentException("Arity for DLVPredicates is expected to be between 1 and 3, given arity is " + arity);
		}
	}
	
	/**
	 * Additional special predicates of the clingo syntax that have no direct 
	 * representation in the DLV or ASP-Core-2 format.
	 * 
	 * @author Anna Gessler
	 */
	public static class ClingoPredicate extends Predicate {
		final Set<String> NAMES = Set.of("#true", "#false");
		
		public ClingoPredicate(String name, int arity) {
			super(name,arity);
			if (!NAMES.contains(name))
				throw new IllegalArgumentException(name + " is not a known DLVPredicate.");
			if (arity != 0)
				throw new IllegalArgumentException("Arity for ClingoPredicates is expected to be 0, given arity is " + arity);
		}

		public ClingoPredicate(String name) {
			super(name);
			if (!NAMES.contains(name))
				throw new IllegalArgumentException(name + " is not a known ClingoPredicate.");
		}
	}

}
