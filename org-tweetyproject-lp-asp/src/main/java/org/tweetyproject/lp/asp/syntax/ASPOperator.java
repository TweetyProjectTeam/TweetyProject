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

/**
 * This class collects some common operators used in ASP terms
 * as well as the possible function names for aggregates.
 * 
 * @author Anna Gessler
 */
public class ASPOperator {
	
	/**
	 * The arithmetic operators that are supported by the ASP-Core-2 standard
	 * and by Tweety: PLUS (+), MINUS (-), TIMES (*), DIV (/)
	 * 
	 * @author Anna Gessler
	 */
	public enum ArithmeticOperator {
		 PLUS,MINUS,TIMES,DIV;  
		
		@Override
		public String toString() {
			if (this.equals(ArithmeticOperator.PLUS)) 
				return "+";
			if (this.equals(ArithmeticOperator.MINUS)) 
				return "-";
			if (this.equals(ArithmeticOperator.TIMES)) 
				return "*";
			else
				return "/";
		}
	}
	
	/**
	 * The binary comparative operators that are supported by the ASP-Core-2 standard
	 * and by Tweety: LT (&lt;), LEQ (&lt;=), EQ (==), NEQ (!=), GT (&gt;), GEQ (&gt;=)
	 * 
	 * @author Anna Gessler
	 */
	public enum BinaryOperator {
		LT,LEQ,EQ,NEQ,GT,GEQ;  
		
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
	 * <br> - #count: Number of elements, used to represent cardinality constraints.
	 * <br> - #sum: Sum of weights, used to represent weight constraints. 
	 * <br> - #max: Maximum weight
	 * <br> - #min: Minimum weight
	 * 
	 * Clingo additionally includes the #sum+ aggregate function, which represents
	 * the sum of positive weights.
	 * 
	 * @author Anna Gessler
	 */
	public enum AggregateFunction {
		COUNT,MAX,MIN,SUM,SUM_PLUS;
		
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
			else
				return "#sum+";
		}
	}
	
	public enum OptimizeFunction {
		MINIMIZE, MAXIMIZE;
		
		@Override
		public String toString() {
			if (this.equals(OptimizeFunction.MINIMIZE)) 
				return "#minimize";
			else
				return "#maximize";
		}
		
	}
	
}
