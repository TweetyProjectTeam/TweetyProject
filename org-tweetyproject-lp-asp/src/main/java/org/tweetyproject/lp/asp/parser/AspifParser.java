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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.lp.asp.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.logics.commons.syntax.NumberTerm;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.lp.asp.syntax.ASPAtom;
import org.tweetyproject.lp.asp.syntax.ASPBodyElement;
import org.tweetyproject.lp.asp.syntax.ASPHead;
import org.tweetyproject.lp.asp.syntax.ASPLiteral;
import org.tweetyproject.lp.asp.syntax.ASPOperator;
import org.tweetyproject.lp.asp.syntax.ASPRule;
import org.tweetyproject.lp.asp.syntax.AggregateAtom;
import org.tweetyproject.lp.asp.syntax.AggregateElement;
import org.tweetyproject.lp.asp.syntax.ChoiceElement;
import org.tweetyproject.lp.asp.syntax.ChoiceHead;
import org.tweetyproject.lp.asp.syntax.ClassicalHead;
import org.tweetyproject.lp.asp.syntax.DefaultNegation;
import org.tweetyproject.lp.asp.syntax.Program;

/**
 * This class is a parser for the aspif (ASP Intermediate Format) format, the output language of gringo 
 * (<a href="https://potassco.org/clingo/">https://potassco.org/clingo/</a>).
 * 
 * <br> Note that parsing is only supported for rules, optimize statements and output statements, but not for advanced
 * statement types such as #external statements, #heuristic directives or theory terms.
 * 
 * A description of the format can be found in the appendix of
 * [Gebser, Kaminski, Kaufmann, Ostrowski, Schaub, Wanko. 
 * Theory Solving Made Easy with Clingo 5 (Extended Version). 2016].
 * 
 * @author Anna Gessler
 *
 */
public class AspifParser {
	
	/**
	 * Create a new AspifParser.
	 */
	public AspifParser() {
		mappings = new HashMap<Integer, ASPAtom>();
	}
	
	/**
	 * This map is used to keep track of the mappings between integers
	 * and the atoms they represent.
	 */
	private Map<Integer, ASPAtom> mappings;
	
	/**
	 * Parses output in the aspif format 
	 * and returns the resulting program.
	 *
	 * @param output parseProgram
	 * @return ASP Program
	 * @throws ParseException parseProgram
	 */
	public Program parseProgram(String output) throws ParseException {
		if (!output.strip().endsWith("0"))
			throw new ParseException("Invalid aspif file: aspif programs need to end with '0'");
		
		Program result = new Program();
		ASPParser parser = new ASPParser(new StringReader(""));
		
		//statements in aspif files are separated by newlines
		String[] lines = output.split("\n");
		
		//In a first pass, parse int-to-literal mappings and output statements
		Set<String> rules = new HashSet<String>();
		for (String l : lines) {
			l = l.strip();
			//Ignore empty lines, header line, and final line
			if (l.isBlank() || l.startsWith("asp") || l.equals("0"))
				continue;
			
			//elements in aspif statements are separated by spaces
			String[] elements = l.split("\\s");
			if (elements[0].equals("4")) {
				String name = elements[2];
				parser.ReInit(new StringReader(name));
				ASPLiteral a = (ASPLiteral) parser.ClassicalLiteral().jjtAccept(new InstantiateVisitor(), null);
				if (elements[3].equals("0")) {
					//add fact to output unconditionally 
					result.add(new ASPRule(a));
					result.addToOutputWhitelist(a.getPredicate());
				} else if (elements[3].equals("1")) {
					//add fact to output if the corresponding literal is true
					int alias = Integer.parseInt(elements[4]);
					mappings.put(alias, (ASPAtom) a);
					result.addToOutputWhitelist(a.getPredicate());
				} else {
					System.err.println("Warning: #show statements with multiple literals are not currently supported, ignoring this line: " + l);
				}
			} else
				rules.add(l);
		}

		//In a second pass, parse rules
		for (String l : rules) {
			//elements in aspif statements are separated by spaces
			String[] elements = l.split("\\s");
			if (elements[0].equals("1")) {
				//line is a rule
				result.add(parseRule(elements));
			}
			else if (elements[0].equals("2")) {
				//line is a minimize statement
				result.add(parseMinimizeStatement(elements));
			}
			else if (elements[0].equals("3")) {
				throw new ParseException("#project directives are not currently supported by GringoGrounder");
			}
			else if (elements[0].equals("5")) {
				throw new ParseException("#external statements are not currently supported by GringoGrounder");
			}
			else if (elements[0].equals("6")) {
				throw new ParseException("Assumption statements are not currently supported by GringoGrounder");
			}
			else if (elements[0].equals("7")) {
				throw new ParseException("#heuristic directives are not currently supported by GringoGrounder");
			}
			else if (elements[0].equals("7")) {
				throw new ParseException("#edge statements are not currently supported by GringoGrounder");
			}
			else if (elements[0].equals("8")) {
				throw new ParseException("Theory terms (clingo[DL] / QF-IDL) are not currently supported by GringoGrounder");
			}
			else if (elements[0].equals("10")) {
				//line is a comment
				continue;
			}
			else
				throw new ParseException("Invalid aspif line " + l);
		}
		return result;
	}
	

	/**
	 * Parses the file of the given filename in the aspif format 
	 * and returns the resulting program.
	 * 
	 * @param filename the name of a file
	 * @return a belief base
	 * @throws FileNotFoundException if the file is not found
	 * @throws IOException           if some IO issue occurred.
	 * @throws ParseException parseProgramFile      
	 */
	public Program parseProgramFile(String filename) throws FileNotFoundException, IOException, ParseException {
		String content = Files.readString(Path.of(filename));
		return this.parseProgram(content);
	}
	
	/**
	 * Parses an aspif rule (i.e. a line beginning with "1").
	 * 
	 * @param elements a string array that contains the elements of the aspif rule statement in order
	 * @return program
	 * @throws ParseException 
	 */
	private ASPRule parseRule(String[] elements) throws ParseException {
		boolean isChoice = Integer.parseInt(elements[1]) == 1;
		int nOfHeadElements = Integer.parseInt(elements[2]);

		//Parse head of rule
		//The head is either a disjunction (ASPHead) or a choice (ChoiceHead)
		List<ASPLiteral> literals = new ArrayList<ASPLiteral>();
		for (int i = 3; i < 3+nOfHeadElements; i++) {
			int name = Integer.parseInt(elements[i]);
			if (name < 0) 
				throw new ParseException("Head literals cannot be naf-negated");
			if (!mappings.containsKey(name)) 
				mappings.put(name, generateAtomMapping(name));
			literals.add(mappings.get(name));
		}

		ASPHead head;
		if (isChoice) {
			List<ChoiceElement> cs = new ArrayList<ChoiceElement>();
			for (ASPLiteral lit : literals) 
				cs.add(new ChoiceElement(lit));
			head = new ChoiceHead(cs);
		} else
			head = new ClassicalHead(literals);
		
		//Parse body of rule
		int bodyStart = 3 + nOfHeadElements;
		if (bodyStart < elements.length) {
			List<ASPBodyElement> bodyElements = new ArrayList<ASPBodyElement>();
		
			//The body is a normal body
			if (elements[bodyStart].equals("0")) {
				for (int j = bodyStart+2; j < elements.length; j++) {
					int name = Integer.parseInt(elements[j]);
					boolean isNaf = false;
					if (name < 0) {
						isNaf = true;
						name = -name; 
					}
					if (!mappings.containsKey(name)) 
						mappings.put(name, generateAtomMapping(name));
					if (isNaf)
						bodyElements.add(new DefaultNegation(mappings.get(name)));
					else
						bodyElements.add(mappings.get(name));
			}
			return new ASPRule(head, bodyElements);
			
			//The body is a weight body
			} else if (elements[bodyStart].equals("1")) {
				int lowerBound = Integer.parseInt(elements[bodyStart+1]);
				List<AggregateElement> aggregateElements = new ArrayList<AggregateElement>();
				for (int j = bodyStart+3; j+1 < elements.length; j+=2) {
					int literal = Integer.parseInt(elements[j]);
					if (!mappings.containsKey(literal)) 
						mappings.put(literal, generateAtomMapping(literal));
					ASPLiteral literalAtom = mappings.get(literal);
					int term = Integer.parseInt(elements[j+1]);
					aggregateElements.add(new AggregateElement(new NumberTerm(term),literalAtom));
				}
				ASPOperator.AggregateFunction op = ASPOperator.AggregateFunction.SUM;
				AggregateAtom a = new AggregateAtom(op, aggregateElements);
				a.setLeftGuard(new NumberTerm(lowerBound));
				a.setLeftOperator(ASPOperator.BinaryOperator.LEQ);
				return new ASPRule(head,a);
			}
			else
				throw new ParseException("Invalid aspif rule body");
		}
		return new ASPRule(head); 
	}
	
	
	/**
	 * Parses an aspif minimize statement (i.e. a line beginning with "2").
	 * 
	 * @param elements a string array that contains the elements of the aspif minimize statement in order
	 * @return program, a collection of weak constraints
	 */
	private Program parseMinimizeStatement(String[] elements) {
		Program result = new Program(); 
		
		//All elements of an aspif minimize statement share the same priority
		int priority = Integer.parseInt(elements[1]);

		for (int i = 3; i+1 < elements.length; i+=2) {
			int literal = Integer.parseInt(elements[i]);
			int term = Integer.parseInt(elements[i+1]);
			boolean isNaf = false;
			if (literal < 0) {
				isNaf = true;
				literal = -literal; 
			}
			if (!mappings.containsKey(literal)) 
				mappings.put(literal, generateAtomMapping(literal));
			
			ASPLiteral lit2 = mappings.get(literal);
			@SuppressWarnings("unchecked")
			List<Term<?>> terms = (List<Term<?>>) lit2.getArguments();
			
			ASPRule r = new ASPRule(new ClassicalHead(), mappings.get(literal));
			if (isNaf)
				r = new ASPRule(new ClassicalHead(), new DefaultNegation( mappings.get(literal)));
			r.setLevel(new NumberTerm(priority));
			r.setWeight(new NumberTerm(term));
			r.setConstraintTerms(terms);
			result.add(r);
		}
		
		return result;
	}

	/**
	 * Generates names for auxiliary atoms added by gringo
	 * and returns an instance of ASPAtom that uses the generated
	 * name.
	 * 
	 * <br> Note: AspifParser automatically adds all non-auxiliary atoms
	 * to the output predicates whitelist (#show statements) of
	 * the parsed program, therefore the auxiliary atoms can be
	 * hidden from clingo models by enabling the use of the output
	 * predicates whitelist.
	 * 
	 * @param name
	 * @return atom with the generated name
	 */
	private ASPAtom generateAtomMapping(int name) {
		return new ASPAtom("temp_" + name + "_");
	}

}
