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
package net.sf.tweety.logics.qbf.parser;

import java.io.*;
import java.util.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.commons.LogicalSymbols;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.syntax.*;
import net.sf.tweety.logics.qbf.syntax.ExistsQuantifiedFormula;
import net.sf.tweety.logics.qbf.syntax.ForallQuantifiedFormula;

/**
 * This class implements a parser for quantified boolean formulas. The BNF for a
 * qbf belief set is given by (starting symbol is KB) <br>
 * <br>
 * KB		 ::== VARDECLAR FORMULAS  <br>
 * VARDECLAR ::== "{" (VARIABLENAME ("," VARIABLENAME)*)? "}"  <br>
 * FORMULAS  ::== FORMULA ( "\n" FORMULA )* <br>
 * FORMULA   ::== VARIABLENAME | "(" FORMULA ")" | FORMULA "&gt;&gt;" FORMULA | FORMULA
 * "||" FORMULA | FORMULA "=&gt;" FORMULA | FORMULA "&lt;=&gt;" FORMULA | "!" FORMULA |
 * "+" | "-" | "forall" VARIABLENAME ":" "(" FORMULA ")" | "exists" VARIABLENAME ":" "(" FORMULA ")" | <br>
 * <br>
 * VARIABLENAME is a sequence of characters excluding |,&amp;,!,(,),=,&lt;,&gt; and whitespace
 * characters. 
 * 
 * @author Anna Gessler
 * @author Matthias Thimm
 *
 */
public class QbfParser extends PlParser {
	/**
	 * Keeps track of variables defined.
	 */
	private PlSignature variables;
	
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

	public PlFormula parseFormula(Reader reader) throws ParserException {
		Stack<Object> stack = new Stack<Object>();
		try {
			this.variables = new PlSignature();
			for (int c = reader.read(); c != -1; c = reader.read())
				this.consumeToken(stack, c);
			return this.parseQuantification(stack);
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
				//If the last 6 consumed tokens spell "forall" or "exists", remove them from the stack 
				//and re-add them as a single string.
				if(stack.size() >= 6){					
					if(stack.get(stack.size()-6).equals("f") &&
							stack.get(stack.size()-5).equals("o") &&
							stack.get(stack.size()-4).equals("r") &&
							stack.get(stack.size()-3).equals("a") &&
							stack.get(stack.size()-2).equals("l") &&
							stack.get(stack.size()-1).equals("l")){
						stack.pop();
						stack.pop();
						stack.pop();
						stack.pop();
						stack.pop();
						stack.pop();
						stack.push("forall");
					}else if(stack.get(stack.size()-6).equals("e") &&
							stack.get(stack.size()-5).equals("x") &&
							stack.get(stack.size()-4).equals("i") &&
							stack.get(stack.size()-3).equals("s") &&
							stack.get(stack.size()-2).equals("t") &&
							stack.get(stack.size()-1).equals("s")){
						stack.pop();
						stack.pop();
						stack.pop();
						stack.pop();
						stack.pop();
						stack.pop();
						stack.push("exists");
					}					
				}
			} else if (s.equals(")")) {
				if (!stack.contains("("))
					throw new ParserException("Missing opening parentheses.");
				List<Object> l = new ArrayList<Object>();
				for (Object o = stack.pop(); !((o instanceof String) && ((String) o).equals("(")); o = stack.pop())
					l.add(0, o);
				stack.push(this.parseQuantification(l));
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
	 * Parses a quantification
	 * @param l list of terms
	 * @return the formula
	 */
	private PlFormula parseQuantification(List<Object> l) {
		if(l.isEmpty())
			throw new ParserException("Empty parentheses.");
		if(!(l.contains(LogicalSymbols.EXISTSQUANTIFIER()) || l.contains(LogicalSymbols.FORALLQUANTIFIER()))) 
			return this.parseEquivalence(l); 
		
		//If the quantification is not the first conjunct/disjunct/subformula of
		//the formula, split list at position of first non-quantor operator
		if (!(l.get(0).equals(LogicalSymbols.EXISTSQUANTIFIER())||l.get(0).equals(LogicalSymbols.FORALLQUANTIFIER()))) {
			int i1 = l.indexOf(LogicalSymbols.CONJUNCTION());
			int i2 = l.indexOf(LogicalSymbols.DISJUNCTION());
			int i3 = l.indexOf(LogicalSymbols.EQUIVALENCE());
			int i4 = l.indexOf(LogicalSymbols.IMPLICATION());
			int[] indices = {i1,i2,i3,i4};
			Arrays.sort(indices);
			
			for (int i = 0; i < indices.length; i++) {
				if (indices[i]!=-1) {
					List<Object> leftl = new ArrayList<Object>(l.subList(0, indices[i]));
					List<Object> rightl = new ArrayList<Object>(l.subList(indices[i]+1, l.size()));
					if (indices[i]==i1) 
						return new Conjunction(parseQuantification(leftl), parseQuantification(rightl));
					else if (indices[i]==i2) 
						return new Disjunction(parseQuantification(leftl), parseQuantification(rightl));
					else if (indices[i]==i3) 
						return new Equivalence(parseQuantification(leftl), parseQuantification(rightl));
					else if (indices[i]==i4) 
						return new Implication(parseQuantification(leftl), parseQuantification(rightl));
					else
						throw new ParserException("Unrecognized formula type '" + indices[i] + "'."); 
				}
			}	
		}
	
		String var = "";
		int idx = 1;
		while(!l.get(idx).equals(":")){
			var += (String) l.get(idx);
			idx++;
		}

		PlFormula formula;
		if (l.get(idx+1) instanceof PlFormula) 
			formula = (PlFormula) l.get(idx+1);
		else  
			throw new ParserException("Unrecognized formula type '" + l.get(idx+1) + "'."); 
		
		List<Proposition> bVars = new ArrayList<Proposition>();;
		for(Proposition v: formula.getAtoms()){
			if(v.getName().equals(var))
					bVars.add(v);
		}
		
		if(bVars.isEmpty())
			throw new ParserException("Variable(s) '" + var + "' not found in quantification.");
		
		Set<Proposition> vars = new HashSet<Proposition>();
		
		int j = 0; //This index is used later to determine if there are more elements in the list to the right of the quantified formula
		for (int i = 0; i < bVars.size(); i++) {
			vars.add(bVars.get(i)); 
			j += (bVars.get(i).getName().length());
		}
		j += bVars.size();
		
		this.variables.remove(var);
	
		PlFormula result;
		if (l.get(0).equals(LogicalSymbols.EXISTSQUANTIFIER())) 
			result = new ExistsQuantifiedFormula(formula,vars);
		else 
			result = new ForallQuantifiedFormula(formula,vars);
		
		//Add additional conjuncts/disjuncts to the right of the quantification (if applicable)
		if (l.size() > 2+j) {
			if (l.get(2+j).equals(LogicalSymbols.CONJUNCTION())) 
				return new Conjunction(result, parseQuantification(new ArrayList<Object>(l.subList(3+j, l.size()))));
			else if (l.get(2+j).equals(LogicalSymbols.DISJUNCTION())) 
				return new Disjunction(result, parseQuantification(new ArrayList<Object>(l.subList(3+j, l.size()))));
			else if (l.get(2+j).equals(LogicalSymbols.EQUIVALENCE()))
				return new Equivalence(result, parseQuantification(new ArrayList<Object>(l.subList(3+j, l.size()))));
			else if (l.get(2+j).equals(LogicalSymbols.IMPLICATION()))
				return new Implication(result, parseQuantification(new ArrayList<Object>(l.subList(3+j, l.size()))));
			else 
				throw new ParserException("Unrecognized symbol " + l.get(idx+2));
		}
		return result;	
	}
	
	private PlFormula parseEquivalence(List<Object> l) {
		if (l.isEmpty())
			throw new ParserException("Empty parentheses.");
		if (!(l.contains(LogicalSymbols.EQUIVALENCE())))
			return this.parseImplication(l);

		List<Object> left = new ArrayList<Object>();
		List<Object> right = new ArrayList<Object>();
		boolean isRightFormula = false;
		for (Object o : l) {
			if ((o instanceof String) && ((String) o).equals(LogicalSymbols.EQUIVALENCE()))
				isRightFormula = true;
			else if (isRightFormula)
				right.add(o);
			else
				left.add(o);
		}
		return new Equivalence(parseQuantification(left), parseQuantification(right));
	}
	
	private PlFormula parseImplication(List<Object> l) {
		if (l.isEmpty())
			throw new ParserException("Empty parentheses.");
		if (!(l.contains(LogicalSymbols.IMPLICATION())))
			return super.parseDisjunction(l);

		List<Object> left = new ArrayList<Object>();
		List<Object> right = new ArrayList<Object>();
		boolean isRightFormula = false;
		for (Object o : l) {
			if ((o instanceof String) && ((String) o).equals(LogicalSymbols.IMPLICATION()))
				isRightFormula = true;
			else if (isRightFormula)
				right.add(o);
			else
				left.add(o);
		}
		return new Implication(parseQuantification(left), parseQuantification(right));
	}

}
