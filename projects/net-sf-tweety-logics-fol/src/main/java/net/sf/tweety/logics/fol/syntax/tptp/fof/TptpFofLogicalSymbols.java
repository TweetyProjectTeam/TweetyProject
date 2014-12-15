/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logics.fol.syntax.tptp.fof;

import net.sf.tweety.logics.commons.LogicalSymbols;

/**
 * This class alters the standard logic symbols to be used with the TPTP-Format,
 * which uses symbols different from those used in LogicalSymbols. Currently
 * only the "fof" formulae syntax is implemented.
 * 
 * @author Bastian Wolf
 */

public class TptpFofLogicalSymbols extends LogicalSymbols {

	private static String tptp_negation = "~";

	// private static void setTPTPNegationSymbol(String sym){
	// TPTPLogicalSymbols.tptp_negation = sym;
	// }

	public static String TPTP_NEGATION() {
		return tptp_negation;
	}

	public static String DISJUNCTION() {
		return "|";
	}

	public static String CONJUNCTION() {
		return "&";
	}

	public static String FORALLQUANTIFIER() {
		return "!";
	}

	public static String EXISTSQUANTIFIER() {
		return "?";
	}

	public static String GENTZEN_ARROW_RIGHT() {
		return "-->";
	}

	// public static String GENTZEN_ARROW_LEFT(){
	// return "<--";
	// }
	// public static String TAUTOLOGY() {
	// return "+";
	// }
	// public static String CONTRADICTION() {
	// return contradiction;
	// }
	// public static String PARENTHESES_LEFT() {
	// return "(";
	// }
	// public static String PARENTHESES_RIGHT() {
	// return ")";
	// }
}
