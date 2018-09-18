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
package net.sf.tweety.lp.asp.syntax;

/**
 * This class collects some common operators used in ASP terms
 * as well as the possible function names for aggregates.
 * 
 * @author Anna Gessler
 *
 */
public class ASPOperator {
	
	/**
	 * The arithmetic operators that are supported by the ASP-Core-2 standard
	 * and by Tweety.
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
	 * and by Tweety.
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
	 * The aggregate functions that are supported by the ASP-Core-2 standard
	 * and by Tweety.
	 * 
	 * @author Anna Gessler
	 */
	public enum AggregateFunction {
		COUNT,MAX,MIN,SUM;
		
		@Override
		public String toString() {
			if (this.equals(AggregateFunction.MAX)) 
				return "#max";
			if (this.equals(AggregateFunction.MIN)) 
				return "#min";
			if (this.equals(AggregateFunction.COUNT)) 
				return "#count";
			else
				return "#sum";
		}
	}
	
}
