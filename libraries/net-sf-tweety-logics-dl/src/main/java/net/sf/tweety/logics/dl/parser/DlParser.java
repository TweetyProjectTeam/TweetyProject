/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2018 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.dl.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.dl.syntax.AtomicConcept;
import net.sf.tweety.logics.dl.syntax.AtomicRole;
import net.sf.tweety.logics.dl.syntax.BottomConcept;
import net.sf.tweety.logics.dl.syntax.Complement;
import net.sf.tweety.logics.dl.syntax.ConceptAssertion;
import net.sf.tweety.logics.dl.syntax.DlAxiom;
import net.sf.tweety.logics.dl.syntax.DlBeliefSet;
import net.sf.tweety.logics.dl.syntax.ComplexConcept;
import net.sf.tweety.logics.dl.syntax.DlSignature;
import net.sf.tweety.logics.dl.syntax.EquivalenceAxiom;
import net.sf.tweety.logics.dl.syntax.ExistentialRestriction;
import net.sf.tweety.logics.dl.syntax.Individual;
import net.sf.tweety.logics.dl.syntax.Intersection;
import net.sf.tweety.logics.dl.syntax.RoleAssertion;
import net.sf.tweety.logics.dl.syntax.TopConcept;
import net.sf.tweety.logics.dl.syntax.Union;
import net.sf.tweety.logics.dl.syntax.UniversalRestriction;

/**
 * This class implements a parser for the description logic ALC (attributive
 * concept language with complements). The input syntax is based on the input
 * syntax of the
 * <a href="https://www.ifis.uni-luebeck.de/~moeller/racer/index.html">Racer<a/>
 * reasoner.
 * <br>
 * Note: Racer is built for the description logic SRIQ(D) (= the DL that
 * corresponds to the OWL2 language), which is more expressive than ALC.
 * SRIQ(D)-exclusive elements like inverse roles, role hierarchies and at-least
 * restrictions are not supported by this parser. Furthermore, the syntax of
 * universal and existential restrictions is different for reasons of
 * simplification (see BNF).
 * <br>
 * <br> The input syntax for an ALC knowledge base is given by the following BNF
 * (starting symbol is KB):
 * <br> KB ::== SIGNATURE FORMULAS 
 * <br> SIGNATURE ::== "signature(" (CONCEPT_DECLAR)? (ROLE_DECLAR)? (INDIVIDUAL_DECLAR)? ")" 
 * <br> CONCEPT_DECLAR ::== ("\n")* "atomic-concepts(" (CONCEPTNAME)* ")" 
 * <br> ROLE_DECLAR ::== ("\n")* "roles(" (ROLENAME)* ")" 
 * <br> INDIVIDUAL_DECLAR ::== ("\n")* "individuals(" (CONSTANTNAME)* ")" 
 * <br> AXIOMS ::== ("\n" AXIOM)* 
 * <br> AXIOM ::== "instance " " " CONSTANTNAME " " CONCEPT | "related " CONSTANTNAME
 * 		" " CONSTANTNAME " " ROLENAME | 
 * <br> "implies " " " CONCEPT " " CONCEPT | "equivalent" CONCEPT " " CONCEPT 
 * <br> CONCEPT ::== "(" CONCEPT ")" | CONCEPTNAME | "not " CONCEPT | "*top*" |
 * 		"*bottom*" | "top" | "bottom" | <br>
 * 		"and " CONCEPT " " CONCEPT | "or " CONCEPT " " CONCEPT | <br>
 * 		"forall " ROLENAME " " CONCEPT | "exists " ROLENAME " " CONCEPT 
 * <br>
 * <br> where CONCEPTNAME, ROLENAME, CONSTANTNAME are sequences of 
 * <br> symbols from {a,...,z,A,...,Z,0,...,9} with a letter at the beginning 
 * <br> excluding {"top","bottom"}.
 * 
 * @author Anna Gessler
 * @author Matthias Thimm
 *
 */
public class DlParser extends Parser<DlBeliefSet, DlAxiom> {

	/**
	 * Keeps track of the signature.
	 */
	private DlSignature signature = new DlSignature();

	/**
	 * A flag that is used for adding an additional formula to represent a (true)
	 * equivalence axiom.
	 */
	private Boolean addTrueEquivalence = false;

	@Override
	public DlBeliefSet parseBeliefBase(Reader reader) throws IOException, ParserException {
		DlBeliefSet beliefSet = new DlBeliefSet();
		String s = "";

		// For keeping track of the section of the file:
		// 0 means signature declaration
		// 2 means formula section, i.e. terminological and assertional axioms
		int section = 0;

		// Read formulas and separate them with "\n" (ascii code 10)
		try {
			for (int c = reader.read(); c != -1; c = reader.read()) {
				if (c == 10) {
					s = s.trim();
					if (!s.equals("")) {
						if (s.startsWith("signature(") || s.startsWith(":"))
							section = 1;
						else if (section == 1)
							section = 2;

						if (section == 2) {
							DlAxiom axiom = this.parseFormula(new StringReader(s));
							beliefSet.add(axiom);
							if (addTrueEquivalence && axiom instanceof EquivalenceAxiom) {
								EquivalenceAxiom eq = (EquivalenceAxiom) axiom;
								beliefSet.add(new EquivalenceAxiom(eq.getFormulas().getSecond(),
										eq.getFormulas().getFirst()));
								addTrueEquivalence = false;
							}
						} else
							this.parseTypeDeclaration(s, this.signature);
					}
					s = "";
				} else
					s += (char) c;
			}
			s = s.trim();
			if (!s.equals("")) {
				DlAxiom axiom = this.parseFormula(new StringReader(s));
				beliefSet.add(axiom);
				if (addTrueEquivalence && axiom instanceof EquivalenceAxiom) {
					EquivalenceAxiom eq = (EquivalenceAxiom) axiom;
					beliefSet.add(new EquivalenceAxiom(eq.getFormulas().getSecond(), eq.getFormulas().getFirst()));
					addTrueEquivalence = false;
				}
			}

		} catch (Exception e) {
			throw new ParserException(e);
		}

		return beliefSet;
	}

	/**
	 * Parses an atomic concept declaration of the form "concept" "(" CONCEPTNAME
	 * ")" or an atomic role declaration of the form "role" "(" ROLENAME ")" and
	 * modifies the given signature accordingly.
	 * 
	 * @param s   a string
	 * @param signature a signature
	 */
	private void parseTypeDeclaration(String s, DlSignature signature) {
		String t = s;
		if (s.startsWith("signature("))
			t = s.substring(10, s.length()).trim();
		if (!t.contains(":"))
			throw new ParserException("Missing ':' in type declaration '" + s + "',");

		// type=0: string is individuals declaration
		// type=1: string is concepts declaration
		// type=2: string is roles declaration
		int type = 0;

		if (t.startsWith(":atomic-concepts")) {
			t = t.substring(17, t.length() - 1); // remove "atomic-concepts(" and closing parenthesis ")"
			type = 1;
		} else if (t.startsWith(":roles")) {
			t = t.substring(7, t.length() - 1); // remove "roles(" and closing parenthesis ")"
			type = 2;
		} else if (t.startsWith(":individuals"))
			t = t.substring(13, t.length() - 2); // remove "individuals(", closing parenthesis ")" and the final closing
													// parenthesis of "signature("
		else
			throw new ParserException("Unknown type declaration section " + s
					+ ", type declarations must begin with ':atomic-concepts', ':roles', or ':individuals'.");

		String[] spl = t.split("\\s");
		for (String xs : spl) {
			if (xs.matches("[a-z,A-Z][a-z,A-Z,0-9,_]*")) {
				if (type == 1)
					signature.add(new AtomicConcept(xs));
				else if (type == 2)
					signature.add(new AtomicRole(xs));
				else
					signature.add(new Individual(xs));
			} else
				throw new ParserException("Illegal characters in concept or role definition '" + xs
						+ "'; declaration must conform to [a-z,A-Z]([a-z,A-Z,0-9])*");
		}
	}

	@Override
	public DlAxiom parseFormula(Reader reader) throws IOException, ParserException {
		try {
			Stack<Object> stack = new Stack<Object>();
			for (int c = reader.read(); c != -1; c = reader.read())
				tokenize(stack, c);
			return this.parseAxiom(stack);
		} catch (Exception e) {
			throw new ParserException(e);
		}
	}

	/**
	 * This method reads one character from the given reader and appropriately 
	 * tokenizes it.
	 * 
	 * @param stack used for monitoring the read items
	 * @param c token from stream
	 */
	public void tokenize(Stack<Object> stack, int c) {
		String s = Character.toString((char) c);
		if (s.equals(")")) {
			if (!stack.contains("("))
				throw new ParserException("Missing opening parenthesis.");
			List<Object> l = new ArrayList<Object>();
			for (Object o = stack.pop(); !((o instanceof String) && ((String) o).equals("(")); o = stack.pop()) 
				l.add(0, o);
			stack.push(this.parseConcept(l)); //Parse contents of parentheses as complex concept
		}
		else if (s.equals(" ")) {
			stack.push(" "); //whitespace characters are added temporarily to separate tokens
		}
		else if (!stack.empty()) {
			Object top = stack.peek();
			//previous token is whitespace: begin new token with current character s, discard whitespace
			if (top.equals(" ")) {
				stack.pop();
				stack.push(s); 
			}
			//previous token is opening parenthesis: begin new token with current character s
			else if (top.equals("("))  
				stack.push(s); 
			//previous token is not whitespace or opening parenthesis: concatenate current character s with previous token
			else 
				stack.push(stack.pop() + s); 
		}
		else {
			stack.push(s);
		}
	}

	/**
	 * Parses an ALC axiom as a list of String tokens or formulas.
	 * @param l list of String tokens or description logic formulas
	 * @return a DLAxiom
	 * @throws ParserException if the list could not be parsed.
	 */
	private DlAxiom parseAxiom(List<Object> l) {
		String type = (String) l.get(0);
		if (type.equals("instance")) 
			return new ConceptAssertion(parseIndividual(l.get(1)),parseConcept(l.get(2)));
		else if (type.equals("related")) 
			return new RoleAssertion(parseIndividual(l.get(1)),parseIndividual(l.get(2)),parseRole(l.get(3)));
		else if (type.equals("implies")) 
			return new EquivalenceAxiom(parseConcept(l.get(1)),parseConcept(l.get(2)));
		else 
			throw new ParserException("Illegal Axiom identifier " + type);
	}
	
	/**
	 * Parses a complex concept as a list of String tokens or formulas.
	 * @param l list of String tokens or description logic formulas
	 * @return a ComplexConcept
	 * @throws ParserException if the list could not be parsed.
	 */
	private ComplexConcept parseConcept(List<Object> l) {
		if (l.size()==1)
			return parseConcept(l.get(0));
		if (l.get(0) instanceof ComplexConcept)
			return (ComplexConcept) l.get(0);
		
		String type = (String) l.get(0);

		if (type.equals("not")) {
			return new Complement(parseConcept(l.get(1)));
		} else if (type.equals("and")) {
			return new Intersection(parseConcept(l.get(1)),parseConcept(l.get(2)));
		} else if (type.equals("or")) {
			return new Union(parseConcept(l.get(1)),parseConcept(l.get(2)));
		} else if (type.equals("forall")) {
			return new UniversalRestriction(parseRole(l.get(1)),parseConcept(l.get(2)));
		} else if (type.equals("exists")) {
			return new ExistentialRestriction(parseRole(l.get(1)),parseConcept(l.get(2)));
		}
		else
			throw new ParserException("Unknown concept identifier " + type);
	}
	
	/**
	 * Parses an individual from a String token.
	 * @param s identifier of the individual
	 * @return an Individual
	 * @throws ParserException if the individual could not be parsed
	 */
	private Individual parseIndividual(Object s) {
		if (s instanceof String && this.signature.containsIndividual((String) s))
			return new Individual((String) s);
		else
			throw new ParserException("Unknown object " + s);
	}
	
	/**
	 * Parses an atomic concept, top concept or bottom concept from a String token.
	 * @param s String identifier of the concept
	 * @return a ComplexConcept
	 * @throws ParserException if the concept could not be parsed
	 */
	private ComplexConcept parseConcept(Object s) {
		if (s instanceof ComplexConcept)
			return (ComplexConcept) s;
		else if (s instanceof String && this.signature.containsConcept((String) s))
			return new AtomicConcept((String)s);
		else if (s.equals("*bottom*") | s.equals("bottom")) 
			return new BottomConcept();
		else if (s.equals("*top*") | s.equals("top")) 
			return new TopConcept();
		else
			throw new ParserException("Unknown object " + s);
	}

	/**
	 * Parses a role from a String token.
	 * @param s identifier of the role
	 * @return an AtomicRole
	 * @throws ParserException if the role could not be parsed
	 */
	private AtomicRole parseRole(Object s) {
		if (s instanceof String && this.signature.containsRole((String) s))
			return new AtomicRole((String) s);
		else
			throw new ParserException("Unknown object " + s);
	}

	/**
	 * Sets the signature for this parser.
	 * 
	 * @param signature a DL signature.
	 */
	public void setSignature(DlSignature signature) {
		this.signature = signature;
	}

	/**
	 * Returns the signature of this parser.
	 * 
	 * @return the signature of this parser.
	 */
	public DlSignature getSignature() {
		return this.signature;
	}

}
