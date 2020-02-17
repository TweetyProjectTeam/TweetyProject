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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.commons;

/**
 * This class provides some String constants for logical symbols and allows to
 * modify them at runtime. This functionality is currently used to switch between
 * different constants for classical negation and contradiction.
 * 
 * @author Matthias Thimm, Sebastian Homann
 */
public class LogicalSymbols {
	private static String classical_negation = "!";
	private static String contradiction = "-";
	
	public static void setClassicalNegationSymbol(String sym) {
		LogicalSymbols.classical_negation = sym;
	}
	
	public static void setContradictionSymbol(String sym) {
		LogicalSymbols.contradiction = sym;
	}
		
	public static String CLASSICAL_NEGATION() {
		return classical_negation;
	}
	public static String DISJUNCTION() {
		return "||";
	}
	public static String CONJUNCTION() {
		return "&&";
	}
	public static String FORALLQUANTIFIER() {
		return "forall";
	}
	public static String EXISTSQUANTIFIER() {
		return "exists";
	}
	public static String IMPLICATION() {
		return "=>";
	}
	public static String EQUIVALENCE() {
		return "<=>";
	}
	public static String TAUTOLOGY() {
		return "+";
	}
	public static String CONTRADICTION() {
		return contradiction;
	}
	public static String PARENTHESES_LEFT() {
		return "(";
	}
	public static String PARENTHESES_RIGHT() {
		return ")";
	}
	public static String NECESSITY() {
		return "[]";
	}
	public static String POSSIBILITY() {
		return "<>";
	}
	public static String EQUALITY() {
		return "==";
	}
	public static String INEQUALITY() {
		return "/==";
	}
	public static String EXCLUSIVEDISJUNCTION() {
		return "^^";
	}
}
