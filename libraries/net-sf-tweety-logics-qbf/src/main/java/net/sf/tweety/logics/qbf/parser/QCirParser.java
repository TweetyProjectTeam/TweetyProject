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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.qbf.parser;

import java.io.*;
import java.util.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.pl.syntax.*;
import net.sf.tweety.logics.qbf.syntax.ExistsQuantifiedFormula;
import net.sf.tweety.logics.qbf.syntax.ForallQuantifiedFormula;

/**
 * This class implements a parser for the QCIR (Quantified CIRcuit) format. See
 * <a href="http://www.qbflib.org/qcir.pdf">http://www.qbflib.org/qcir.pdf</a> for more information. <br>
 * The BNF for a QCir file is given by (starting symbol is QCIR-FILE): <br>
 * <br>
 * QCIR-FILE ::== FORMAT-ID QBLOCK-STMT OUTPUT-STMT (GATE-STMT)* <br>
 * FORMAT-ID ::== "#QCIR-G14" [INTEGER] "\n" <br>
 * QBLOCK-STMT ::== ["free(" VAR-LIST ")\n"] QBLOCK-QUANT* <br>
 * QBLOCK-QUANT ::== QUANT "(" VAR-LIST ")"\n" <br>
 * VAR-LIST ::== (VAR,)* VAR <br>
 * LIT-LIST ::== (LIT,)* LIT <br>
 * OUTPUT-STMT ::== "output(" LIT ")\n" <br>
 * GATE-STMT ::== GVAR "=" "and" "(" LIT-LIST ")" | GVAR "=" "or" "(" LIT-LIST
 * ")" | GVAR "=" "xor(" LIT "," LIT ")" | GVAR "=" "ite(" LIT "," LIT "," LIT
 * ")" | GVAR = QUANT "(" VAR-LIST ";" LIT ")" <br>
 * QUANT ::== "exists" | "forall" <br>
 * LIT ::== VAR | "-" VAR | GVAR | "-" GVAR <br>
 * <br>
 * ite stands for if-then-else. <br>
 * INTEGER are integer numbers. <br>
 * VAR and GVAR are sequences of characters of symbols from
 * {a,...,z,A,...,Z,0,...,9,_}. <br>
 * Additionally, lines starting with "#" (comments) will be ignored.
 * 
 * @author Anna Gessler
 */
public class QCirParser extends Parser<PlBeliefSet, PlFormula> {
	/**
	 * Keeps track of the formulas that are assigned to specific gate variables.
	 */
	private Map<String, PlFormula> gate_variables = new HashMap<String, PlFormula>();

	/**
	 * Keeps track of universally quantified variables.
	 */
	private PlSignature forall_quantified_variables = new PlSignature();

	/**
	 * Keeps track of existentially quantified variables.
	 */
	private PlSignature exists_quantified_variables = new PlSignature();

	/**
	 * The name of the output gate variable of the problem.
	 */
	private Proposition output;

	/**
	 * @return the output gate of this QCir problem.
	 */
	public PlFormula getOutputVariable() {
		PlFormula result = this.output;
		if (this.gate_variables.containsKey(this.output.getName()))
			result = this.gate_variables.get(this.output.getName());

		//add quantifiers from quantifier block
		for (Proposition v : this.forall_quantified_variables) {
			if (result.getSignature().contains(v)) {
				Set<Proposition> vars = new HashSet<Proposition>();
				vars.add(v);
				if (result instanceof ForallQuantifiedFormula) {
					vars.addAll(((ForallQuantifiedFormula) result).getQuantifierVariables());
					result = new ForallQuantifiedFormula(((ForallQuantifiedFormula) result).getFormula(), vars);
				} else
					result = new ForallQuantifiedFormula(result, vars);
			}
		}

		for (Proposition v : this.exists_quantified_variables) {
			if (result.getSignature().contains(v)) {
				Set<Proposition> vars = new HashSet<Proposition>();
				vars.add(v);
				if (result instanceof ExistsQuantifiedFormula) {
					vars.addAll(((ExistsQuantifiedFormula) result).getQuantifierVariables());
					result = new ExistsQuantifiedFormula(((ExistsQuantifiedFormula) result).getFormula(), vars);
				} else
					result = new ExistsQuantifiedFormula(result, vars);
			}
		}
		return result;
	}

	@Override
	public PlBeliefSet parseBeliefBase(Reader reader) throws IOException, ParserException {
		PlBeliefSet beliefSet = new PlBeliefSet();
		String s = "";
		int section = 0;
		// Parse each non-comment line as a formula
		try {
			for (int c = reader.read(); c != -1; c = reader.read()) {
				if (c == 10 || c == 13) {
					s = s.trim();
					if (!s.equals("") && !s.startsWith("#")) {
						if (section == 1) // parse gate formulas
							beliefSet.add((PlFormula) this.parseFormula(new StringReader(s)));
						else if (s.startsWith("output")) { // parse output line
							String output_inner_formula = new String(s.substring(7, s.length() - 1));
							if (output_inner_formula.matches("[A-Za-z0-9_]+"))
								this.output = new Proposition(output_inner_formula);
							else
								throw new ParserException("Illegal output variable name " + output_inner_formula
										+ ", variables can contain only letters, numbers, underscores.");
							section = 1;
						} else if (section == 0) // parse quantifier block
							this.parseQuantifierBlock(s);
					}
					s = "";
				} else
					s += (char) c;
			}
			s = s.trim();
		} catch (Exception e) {
			throw new ParserException(e);
		}

		// Add the quantifications that were defined in the quantifier block
		PlBeliefSet beliefSet_forall_quantified = new PlBeliefSet();
		for (PlFormula f : beliefSet) {
			PlFormula temp = f;
			for (Proposition v : this.forall_quantified_variables) {
				if (f.getSignature().contains(v)) {
					Set<Proposition> vars = new HashSet<Proposition>();
					vars.add(v);
					if (temp instanceof ForallQuantifiedFormula) {
						vars.addAll(((ForallQuantifiedFormula) temp).getQuantifierVariables());
						temp = new ForallQuantifiedFormula(((ForallQuantifiedFormula) temp).getFormula(), vars);
					} else
						temp = new ForallQuantifiedFormula(temp, vars);
				}
			}
			beliefSet_forall_quantified.add(temp);
		}
		PlBeliefSet beliefSet_exists_quantified = new PlBeliefSet();
		for (PlFormula f : beliefSet_forall_quantified) {
			PlFormula temp = f;
			for (Proposition v : this.exists_quantified_variables) {
				if (f.getSignature().contains(v)) {
					Set<Proposition> vars = new HashSet<Proposition>();
					vars.add(v);
					if (temp instanceof ExistsQuantifiedFormula) {
						vars.addAll(((ExistsQuantifiedFormula) temp).getQuantifierVariables());
						temp = new ExistsQuantifiedFormula(((ExistsQuantifiedFormula) temp).getFormula(), vars);
					} else
						temp = new ExistsQuantifiedFormula(temp, vars);
				}
			}
			beliefSet_exists_quantified.add(temp);
		}
		return beliefSet_exists_quantified;
	}

	/**
	 * Parses the quantifier block at the beginning of the file. The free variables
	 * declaration is ignored as free variables are not currently saved in this
	 * parser.
	 * 
	 * @param s string containing the quantifier block
	 */
	private void parseQuantifierBlock(String s) {
		if (s.toLowerCase().startsWith("forall") || s.toLowerCase().startsWith("exists")) {
			String var = "";
			for (int i = 7; i < s.length(); i++) {
				if (s.charAt(i) == ',' || s.charAt(i) == ')') {
					var = var.strip();
					if (var.matches("[A-Za-z0-9_]+")) {
						if (s.toLowerCase().startsWith("forall"))
							this.forall_quantified_variables.add(new Proposition(var));
						else
							this.exists_quantified_variables.add(new Proposition(var));
					} else
						throw new ParserException("Illegal variable name " + var
								+ ", variables can contain only letters, numbers, underscores.");
					var = "";
				} else if (s.charAt(i) != '(')
					var += s.charAt(i);
			}

		} else if (!s.toLowerCase().startsWith("free("))
			throw new ParserException("Illegal quantifier in line " + s);
	}

	@Override
	public PlFormula parseFormula(Reader reader) throws IOException, ParserException {
		try {
			String s = "";
			for (int c = reader.read(); c != -1; c = reader.read())
				s += Character.toString((char) c);
			return this.parseGateFormula(s);
		} catch (Exception e) {
			throw new ParserException(e);
		}
	}

	/**
	 * This method parses a gate formula, meaning a conjunction, disjunction,
	 * quantification, xor-construct or if-then-else-construct.
	 * 
	 * @param formula the input formula as a String
	 * @return the parsed quantified boolean formula
	 */
	private PlFormula parseGateFormula(String formula) {
		// left part of formula
		String gate_var = new String(formula.substring(0, formula.indexOf("=")).strip());
		if (this.gate_variables.containsKey(gate_var))
			throw new ParserException("Redefintions of gate variables are not allowed.");

		// right part of formula, starts with a prefix like "and(..." or "forall(..."
		// converted to lower case because keywords like "and" are case-insensitive
		String prefix = formula.substring(formula.indexOf("=") + 1, formula.indexOf("(")).strip().toLowerCase();

		// inner formula of the right part, without the parentheses
		String innerFormula = new String(formula.substring(formula.indexOf("(") + 1, formula.length() - 1));

		PlFormula result = null;
		if (prefix.startsWith("and") || prefix.startsWith("or"))
			result = parseAssociativeFormula(prefix, innerFormula);
		else if (prefix.startsWith("forall") || prefix.startsWith("exists"))
			result = parseQuantification(prefix, innerFormula);
		else if (prefix.startsWith("xor"))
			result = parseXor(innerFormula);
		else if (prefix.startsWith("ite"))
			result = parseIfThenElse(innerFormula);
		else
			throw new ParserException("Unknown formula type " + innerFormula);

		// save this formula in association with its gate variable
		// so that it can be substituted if the gate variable appears
		// in another formula later
		this.gate_variables.put(gate_var, result);

		return result;
	}

	/**
	 * Parses variables and their negations.
	 * 
	 * @param literal            the input variable as a string
	 * @param parseGateVariables if set to true, gate variables are also parsed,
	 *                           meaning they are replaced with the formulas that
	 *                           they represent.
	 * @return the parsed quantified boolean formula
	 */
	private PlFormula parseLiteral(String literal, boolean parseGateVariables) {
		boolean negated = false;
		literal = literal.strip();
		if (literal.startsWith("-")) {
			literal = new String(literal.substring(1));
			negated = true;
		}
		if (!literal.matches("[A-Za-z0-9_]+"))
			throw new ParserException(
					"Illegal variable name " + literal + ", variables can contain only letters, numbers, underscores.");
		else if (parseGateVariables && this.gate_variables.containsKey(literal))
			if (negated)
				return new Negation(this.gate_variables.get(literal));
			else
				return this.gate_variables.get(literal);
		else if (negated)
			return new Negation(new Proposition(literal));
		else
			return (new Proposition(literal));
	}

	/**
	 * Parses an associative formula, i.e. a conjunction or disjunction.
	 * 
	 * @param prefix       the full formula with its prefix "and" or "or"
	 * @param innerFormula the inner formula without the prefix or parentheses
	 * @return the parsed quantified boolean formula
	 */
	private PlFormula parseAssociativeFormula(String prefix, String innerFormula) {
		PlFormula result = null;
		String var = "";
		Set<PlFormula> juncts = new HashSet<PlFormula>();
		for (int i = 0; i <= innerFormula.length(); i++) {
			if (i == innerFormula.length() || innerFormula.charAt(i) == ',') {
				juncts.add(parseLiteral(var, true));
				var = "";
			} else
				var += innerFormula.charAt(i);
		}
		if (prefix.startsWith("and")) {
			if (juncts.isEmpty())
				result = new Tautology();
			else
				result = new Conjunction(juncts);
		} else {
			if (juncts.isEmpty())
				result = new Contradiction();
			else
				result = new Disjunction(juncts);
		}
		return result;
	}

	/**
	 * Parses a quantified formula.
	 * 
	 * @param prefix       the full formula with its prefix "forall" or "exists"
	 * @param innerFormula the inner formula without the prefix or parentheses
	 * @return the parsed quantified boolean formula
	 */
	private PlFormula parseQuantification(String prefix, String innerFormula) {
		String var = "";
		Set<Proposition> quantifier_vars = new HashSet<Proposition>();
		int i = 0;
		for (; i < innerFormula.length(); i++) {
			if (i != innerFormula.length() && innerFormula.charAt(i) == ';')
				break;
			if (i == innerFormula.length() || innerFormula.charAt(i) == ',') {
				quantifier_vars.add((Proposition) parseLiteral(var, false));
				var = "";
			} else
				var += innerFormula.charAt(i);
		}
		String quantifiedFormulaString = new String(innerFormula.substring(i + 1));
		PlFormula quantifiedFormula = parseLiteral(quantifiedFormulaString, true);
		if (prefix.startsWith("forall"))
			return new ForallQuantifiedFormula(quantifiedFormula, quantifier_vars);
		else if (prefix.startsWith("exists"))
			return new ExistsQuantifiedFormula(quantifiedFormula, quantifier_vars);
		else
			throw new ParserException("Unknown quantifier " + prefix);
	}

	/**
	 * Parses an if-then-else construct.
	 * 
	 * @param innerFormula the inner formula without the "ite" prefix or parentheses
	 * @return the parsed quantified boolean formula
	 */
	private PlFormula parseIfThenElse(String innerFormula) {
		List<PlFormula> formulas = new ArrayList<PlFormula>();
		String var = "";
		for (int i = 0; i <= innerFormula.length(); i++) {
			if (i == innerFormula.length() || innerFormula.charAt(i) == ',') {
				formulas.add(parseLiteral(var, true));
				var = "";
			} else
				var += innerFormula.charAt(i);
		}
		if (formulas.size() != 3)
			throw new ParserException("If-then-else construct " + innerFormula + " has " + formulas.size()
					+ " arguments, but should have exactly 3");

		// ite(x,y,z) is equivalent to (x && y) || (!x && z)
		Disjunction result = new Disjunction();
		result.add(new Conjunction(formulas.get(0), formulas.get(1)));
		result.add(new Conjunction(new Negation(formulas.get(0)), formulas.get(2)));
		return result;
	}

	/**
	 * Parses an xor construct.
	 * 
	 * @param innerFormula the inner formula without the "xor" prefix or parentheses
	 * @return the parsed quantified boolean formula
	 */
	private PlFormula parseXor(String innerFormula) {
		List<PlFormula> formulas = new ArrayList<PlFormula>();
		String var = "";
		for (int i = 0; i <= innerFormula.length(); i++) {
			if (i == innerFormula.length() || innerFormula.charAt(i) == ',') {
				formulas.add(parseLiteral(var, true));
				var = "";
			} else
				var += innerFormula.charAt(i);
		}
		if (formulas.size() != 2)
			throw new ParserException(
					"xor gate " + innerFormula + " has " + formulas.size() + " arguments, but should have exactly 2");

		// xor(x,y) is equivalent to (x && !y) || (!x && y)
		Disjunction result = new Disjunction();
		result.add(new Conjunction(formulas.get(0), new Negation(formulas.get(1))));
		result.add(new Conjunction(formulas.get(1), new Negation(formulas.get(0))));
		return result;
	}

}
