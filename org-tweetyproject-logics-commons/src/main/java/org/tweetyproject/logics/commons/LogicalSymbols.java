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
package org.tweetyproject.logics.commons;

/**
 * This class provides some String constants for logical symbols and allows to
 * modify them at runtime. This functionality is currently used to switch between
 * different constants for classical negation and contradiction.
 *
 * @author Matthias Thimm, Sebastian Homann
 */
public class LogicalSymbols {
   /** The symbol used for classical negation, default is "!". */
   private static String classical_negation = "!";

   /** The symbol used for contradiction, default is "-". */
   private static String contradiction = "-";

   /**
	* Sets the symbol for classical negation.
	*
	* @param sym the new symbol to use for classical negation.
	*/
   public static void setClassicalNegationSymbol(String sym) {
	   LogicalSymbols.classical_negation = sym;
   }

   /**
	* Sets the symbol for contradiction.
	*
	* @param sym the new symbol to use for contradiction.
	*/
   public static void setContradictionSymbol(String sym) {
	   LogicalSymbols.contradiction = sym;
   }

   /**
	* Returns the symbol for classical negation.
	*
	* @return the symbol for classical negation.
	*/
   public static String CLASSICAL_NEGATION() {
	   return classical_negation;
   }

   /**
	* Returns the symbol for disjunction.
	*
	* @return the symbol for disjunction, which is "||".
	*/
   public static String DISJUNCTION() {
	   return "||";
   }

   /**
	* Returns the symbol for conjunction.
	*
	* @return the symbol for conjunction, which is "&&".
	*/
   public static String CONJUNCTION() {
	   return "&&";
   }

   /**
	* Returns the symbol for the universal quantifier.
	*
	* @return the symbol for the universal quantifier, which is "forall".
	*/
   public static String FORALLQUANTIFIER() {
	   return "forall";
   }

   /**
	* Returns the symbol for the existential quantifier.
	*
	* @return the symbol for the existential quantifier, which is "exists".
	*/
   public static String EXISTSQUANTIFIER() {
	   return "exists";
   }

   /**
	* Returns the symbol for implication.
	*
	* @return the symbol for implication, which is "=>".
	*/
   public static String IMPLICATION() {
	   return "=>";
   }

   /**
	* Returns the symbol for equivalence.
	*
	* @return the symbol for equivalence, which is "<=>".
	*/
   public static String EQUIVALENCE() {
	   return "<=>";
   }

   /**
	* Returns the symbol for tautology.
	*
	* @return the symbol for tautology, which is "+".
	*/
   public static String TAUTOLOGY() {
	   return "+";
   }

   /**
	* Returns the symbol for contradiction.
	*
	* @return the symbol for contradiction.
	*/
   public static String CONTRADICTION() {
	   return contradiction;
   }

   /**
	* Returns the symbol for the left parenthesis.
	*
	* @return the symbol for the left parenthesis, which is "(".
	*/
   public static String PARENTHESES_LEFT() {
	   return "(";
   }

   /**
	* Returns the symbol for the right parenthesis.
	*
	* @return the symbol for the right parenthesis, which is ")".
	*/
   public static String PARENTHESES_RIGHT() {
	   return ")";
   }

   /**
	* Returns the symbol for necessity.
	*
	* @return the symbol for necessity, which is "[]".
	*/
   public static String NECESSITY() {
	   return "[]";
   }

   /**
	* Returns the symbol for possibility.
	*
	* @return the symbol for possibility, which is "<>".
	*/
   public static String POSSIBILITY() {
	   return "<>";
   }

   /**
	* Returns the symbol for equality.
	*
	* @return the symbol for equality, which is "==".
	*/
   public static String EQUALITY() {
	   return "==";
   }

   /**
	* Returns the symbol for inequality.
	*
	* @return the symbol for inequality, which is "/==".
	*/
   public static String INEQUALITY() {
	   return "/==";
   }

   /**
	* Returns the symbol for exclusive disjunction.
	*
	* @return the symbol for exclusive disjunction, which is "^^".
	*/
   public static String EXCLUSIVEDISJUNCTION() {
	   return "^^";
   }
}
