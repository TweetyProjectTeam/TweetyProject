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
		return "FORALL";
	}
	public static String EXISTSQUANTIFIER() {
		return "EXISTS";
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
}
