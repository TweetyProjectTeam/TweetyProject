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
package net.sf.tweety.logics.pl.parser;

import java.io.*;
import java.util.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.commons.LogicalSymbols;
import net.sf.tweety.logics.pl.syntax.*;

/**
 * This class implements a parser for propositional logic. The BNF for a
 * propositional belief set is given by (starting symbol is FORMULASET) <br>
 * <br>
 * FORMULASET ::== FORMULA ( "\n" FORMULA )* <br>
 * FORMULA ::== PROPOSITION | "(" FORMULA ")" | FORMULA "&gt;&gt;" FORMULA | FORMULA
 * "||" FORMULA | FORMULA "=&gt;" FORMULA | FORMULA "&lt;=&gt;" FORMULA | "!" FORMULA |
 * "+" | "-" <br>
 * <br>
 * PROPOSITION is a sequence of characters excluding |,&amp;,!,(,),=,&lt;,&gt; and whitespace
 * characters.
 * 
 * @author Matthias Thimm
 * @author Bastian Wolf
 */
public class PlParser extends Parser<PlBeliefSet, PlFormula> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.kr.Parser#parseBeliefBase(java.io.Reader)
	 */
	@Override
	public PlBeliefSet parseBeliefBase(Reader reader) throws ParserException {
		PlBeliefSet beliefSet = new PlBeliefSet();
		String s = "";
		// read from the reader and separate formulas by "\n"
		try {
			for (int c = reader.read(); c != -1; c = reader.read()) {
				if (c == 10) {
					if (!s.equals(""))
						beliefSet.add(this.parseFormula(new StringReader(s)));
					s = "";
				} else {
					s += (char) c;
				}
			}
			if (!s.equals(""))
				beliefSet.add(this.parseFormula(new StringReader(s)));
		} catch (Exception e) {
			throw new ParserException(e);
		}
		return beliefSet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.kr.Parser#parseFormula(java.io.Reader)
	 */
	@Override
	public PlFormula parseFormula(Reader reader) throws ParserException {
		Stack<Object> stack = new Stack<Object>();
		try {
			for (int c = reader.read(); c != -1; c = reader.read())
				this.consumeToken(stack, c);
			return this.parseEquivalence(stack);
		} catch (Exception e) {
			throw new ParserException(e);
		}
	}

	/**
	 * This method reads one token from the given reader and appropriately
	 * constructs a propositional formula from the stream.
	 * 
	 * @param stack a stack used for monitoring the read items.
	 * @param c     a token from a stream.
	 * @throws ParserException in case of parser errors.
	 */
	private void consumeToken(Stack<Object> stack, int c) throws ParserException {
		try {
			String s = Character.toString((char) c);
			if (s.equals(" ")) {
				// do nothing
			} else if (s.equals(")")) {
				if (!stack.contains("("))
					throw new ParserException("Missing opening parentheses.");
				List<Object> l = new ArrayList<Object>();
				for (Object o = stack.pop(); !((o instanceof String) && ((String) o).equals("(")); o = stack.pop())
					l.add(0, o);
				stack.push(this.parseEquivalence(l));
				// If two consecutive "|" or two consecutive "&" have been read,
				// remove them from the stack and re-add them as a single string.
			} else if (s.equals("|")) {
				if (stack.lastElement().equals("|")) {
					stack.pop();
					stack.push("||");
				} else
					stack.push(s);
			} else if (s.equals("&")) {
				if (stack.lastElement().equals("&")) {
					stack.pop();
					stack.push("&&");
				} else
					stack.push(s);
			} else if (s.equals(">")) {
				if (stack.lastElement().equals("=")) {
					if (stack.size() >= 2 && stack.get(stack.size() - 2).equals("<")) {
						stack.pop();
						stack.pop();
						stack.push("<=>");
					} else {
						stack.pop();
						stack.push("=>");
					}
				} else
					stack.push(s);
			} else
				stack.push(s);
		} catch (Exception e) {
			throw new ParserException(e);
		}
	}
	
	/**
	 * Parses a simple formula as a list of String tokens or formulas into a
	 * propositional formula. This method expects no parentheses in the list and as
	 * such treats the formula as a equivalence.
	 * 
	 * @param l a list objects, either String tokens or objects of type PlFormula.
	 * @return a PlFormula.
	 * @throws ParserException if the list could not be parsed.
	 */
	private PlFormula parseEquivalence(List<Object> l) {
		if(l.isEmpty())
			throw new ParserException("Empty parentheses.");
		if(!(l.contains(LogicalSymbols.EQUIVALENCE())))
			return this.parseImplication(l);	
		
		List<Object> left = new ArrayList<Object>(); 
		List<Object> right = new ArrayList<Object>(); 
		boolean isRightFormula = false;
		for(Object o: l){
			if((o instanceof String) && ((String)o).equals(LogicalSymbols.EQUIVALENCE()) )
				isRightFormula = true;
			else if (isRightFormula) 
				right.add(o);
			else
				left.add(o);
		}	
		return new Equivalence(parseEquivalence(left),parseEquivalence(right));	
	}
	
	/**
	 * Parses a simple formula as a list of String tokens or formulas into a
	 * propositional formula. This method expects no parentheses in the list and as
	 * such treats the formula as a implication.
	 * 
	 * @param l a list objects, either String tokens or objects of type PlFormula.
	 * @return a PlFormula.
	 * @throws ParserException if the list could not be parsed.
	 */
	private PlFormula parseImplication(List<Object> l) {
		if(l.isEmpty())
			throw new ParserException("Empty parentheses.");
		if(!(l.contains(LogicalSymbols.IMPLICATION())))
			return this.parseDisjunction(l);	
		
		List<Object> left = new ArrayList<Object>(); 
		List<Object> right = new ArrayList<Object>(); 
		boolean isRightFormula = false;
		for(Object o: l){
			if((o instanceof String) && ((String)o).equals(LogicalSymbols.IMPLICATION()) )
				isRightFormula = true;
			else if (isRightFormula) 
				right.add(o);
			else
				left.add(o);
		}	
		return new Implication(parseEquivalence(left),parseEquivalence(right));	
	}

	/**
	 * Parses a simple formula as a list of String tokens or formulas into a
	 * propositional formula. This method expects no parentheses in the list and as
	 * such treats the formula as a disjunction.
	 * 
	 * @param l a list objects, either String tokens or objects of type
	 *          PropositionalFormula.
	 * @return a propositional formula.
	 * @throws ParserException if the list could not be parsed.
	 */
	protected PlFormula parseDisjunction(List<Object> l) throws ParserException {
		if (l.isEmpty())
			throw new ParserException("Empty parentheses.");
		if (!(l.contains(LogicalSymbols.DISJUNCTION())))
			return this.parseConjunction(l);
		Disjunction d = new Disjunction();
		List<Object> tmp = new ArrayList<Object>();
		for (Object o : l) {
			if ((o instanceof String) && ((String) o).equals(LogicalSymbols.DISJUNCTION())) {
				d.add(this.parseConjunction(tmp));
				tmp = new ArrayList<Object>();
			} else
				tmp.add(o);
		}
		d.add(this.parseConjunction(tmp));
		if (d.size() > 1)
			return d;
		throw new ParserException("General parsing exception.");
	}

	/**
	 * Parses a simple formula as a list of String tokens or formulas into a
	 * propositional formula. This method expects no parentheses and no disjunctions
	 * in the list and as such treats the formula as a conjunction.
	 * 
	 * @param l a list objects, either String tokens or objects of type
	 *          PropositionalFormula.
	 * @return a propositional formula.
	 * @throws ParserException if the list could not be parsed.
	 */
	private PlFormula parseConjunction(List<Object> l) throws ParserException {
		if (l.isEmpty())
			throw new ParserException("General parsing exception.");
		if (!(l.contains(LogicalSymbols.CONJUNCTION())))
			return this.parseNegation(l);
		Conjunction c = new Conjunction();
		List<Object> tmp = new ArrayList<Object>();
		for (Object o : l) {
			if ((o instanceof String) && ((String) o).equals(LogicalSymbols.CONJUNCTION())) {
				c.add(this.parseNegation(tmp));
				tmp = new ArrayList<Object>();
			} else
				tmp.add(o);
		}
		c.add(this.parseNegation(tmp));
		if (c.size() > 1)
			return c;
		throw new ParserException("General parsing exception.");
	}

	/**
	 * Parses a simple formula as a list of String tokens or formulas into a
	 * propositional formula. This method expects no parentheses, no disjunctions,
	 * and no conjunctions in the list and as such treats the formula as a negation.
	 * 
	 * @param l a list objects, either String tokens or objects of type
	 *          PropositionalFormula.
	 * @return a propositional formula.
	 * @throws ParserException if the list could not be parsed.
	 */
	private PlFormula parseNegation(List<Object> l) throws ParserException {
		if (l.get(0).equals(LogicalSymbols.CLASSICAL_NEGATION())) {
			l.remove(0);
			return new Negation(this.parseAtomic(l));
		}
		return this.parseAtomic(l);
	}

	/**
	 * Parses a simple formula as a list of String tokens or formulas into a
	 * propositional formula. This method expects no parentheses, no disjunctions,
	 * no conjunctions, and no negation in the list and as such treats the formula
	 * as an atomic construct, either a contradiction, a tautology, or a
	 * proposition.
	 * 
	 * @param l a list objects, either String tokens or objects of type
	 *          PropositionalFormula.
	 * @return a propositional formula.
	 * @throws ParserException if parsing fails
	 */
	private PlFormula parseAtomic(List<Object> l) throws ParserException {
		if (l.size() == 1) {
			Object o = l.get(0);
			if (o instanceof PlFormula)
				return (PlFormula) o;
			if (o instanceof String) {
				String s = (String) o;
				if (s.equals(LogicalSymbols.CONTRADICTION()))
					return new Contradiction();
				if (s.equals(LogicalSymbols.TAUTOLOGY()))
					return new Tautology();
				// Allow all characters for proposition names except |,&,!,(,) and whitespace
				// characters.
				if (s.matches("[^|&!\\s\\(\\)\\<\\>\\=]"))
					return new Proposition(s);
			}
			throw new ParserException("Unknown object " + o);
		} else {
			// List l should be a list of String
			String s = "";
			for (Object o : l) {
				if (!(o instanceof String))
					throw new ParserException("Unknown object " + o);
				s += (String) o;
			}
			if (s.matches("([^|&!\\s\\(\\)\\<\\>\\=])+"))
				return new Proposition(s);
			throw new ParserException("General parsing error.");
		}
	}

}
