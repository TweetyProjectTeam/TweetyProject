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
package org.tweetyproject.logics.fol.parser;

import java.io.*;
import java.util.*;

import org.tweetyproject.commons.*;
import org.tweetyproject.logics.commons.LogicalSymbols;
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.FunctionalTerm;
import org.tweetyproject.logics.commons.syntax.Functor;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.Sort;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.*;



/**
 * This class implements a parser for first-order logic. The BNF for a first-order
 * knowledge base is given by (starting symbol is KB)
 * <br>
 * <br> KB 			::== SORTSDEC DECLAR FORMULAS
 * <br> DECLAR		::== (FUNCTORDEC | PREDDEC)*
 * <br> SORTSDEC    ::== ( SORTNAME "=" "{" (CONSTANTNAME ("," CONSTANTNAME)*)? "}" "\n" )*
 * <br> PREDDEC		::== "type" "(" PREDICATENAME ("(" SORTNAME ("," SORTNAME)* ")")? ")" "\n"
 * <br> FUNCTORDEC	::== "type" "(" SORTNAME "=" FUNCTORNAME "(" (SORTNAME ("," SORTNAME)*)? ")" ")" "\n"
 * <br> FORMULAS    ::== ( "\n" FORMULA)*
 * <br> FORMULA     ::== ATOM | "forall" VARIABLENAME ":" "(" FORMULA ")" | "exists" VARIABLENAME ":" "(" FORMULA ")" |
 * <br>					 "(" FORMULA ")" | FORMULA "&amp;&amp;" FORMULA | FORMULA "||" FORMULA | "!" FORMULA | "+" | "-" |
 * <br>					 FORMULA "=&gt;" FORMULA | FORMULA "&lt;=&gt;" FORMULA | FORMULA "==" FORMULA | FORMULA "/==" FORMULA |
 * <br>					 FORMULA "^^" FORMULA
 * <br> ATOM		::== PREDICATENAME ("(" TERM ("," TERM)* ")")?
 * <br> TERM		::== VARIABLENAME | CONSTANTNAME | FUNCTORNAME "(" (TERM ("," TERM)*)?  ")" 
 * <br> 
 * <br> where SORTNAME, PREDICATENAME, CONSTANTNAME and FUNCTORNAME are sequences of
 * <br> symbols from {a,...,z,A,...,Z,0,...,9} with a letter at the beginning and VARIABLENAME
 * <br> is a sequence of symbols from {a,...,z,A,...,Z,0,...,9} with an uppercase letter at the beginning.
 * <br>
 * <br> Note: Equality/Inequality predicates (== and /==) can only be parsed if the parser is given a FolSignature 
 * <br> with equality (which is not the case by default).
 * 
 * @author Matthias Thimm
 * @author Anna Gessler
 */
public class FolParser extends Parser<FolBeliefSet,FolFormula> {
	/**
	 * Keeps track of the signature.
	 */
	private FolSignature signature = new FolSignature();
	
	/**
	 * Keeps track of variables defined.
	 */
	private Map<String,Variable> variables;
	
	/** Do not raise exceptions when encountering new constants, all new constants are treated
	 * as THING	 */
	private boolean ignoreUndeclaredConstants;
	
	/**
	 * Creates a new FolParser
	 */
	public FolParser() {
		this(false);
	}
	
	/**
	 * Creates a new FolParser
	 * @param ignoreUndeclaredConstants Do not raise exceptions when encountering new constants, all new constants are treated
	 * as THING
	 */
	public FolParser(boolean ignoreUndeclaredConstants) {
		this.ignoreUndeclaredConstants = ignoreUndeclaredConstants;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.kr.Parser#parseBeliefBase(java.io.Reader)
	 */
	@Override
	public FolBeliefSet parseBeliefBase(Reader reader) throws IOException, ParserException {
		FolBeliefSet beliefSet = new FolBeliefSet();
		String s = "";
		// for keeping track of the section of the file:
		// 0 means sorts declaration
		// 1 means type declaration, i.e. functor/predicate declaration
		// 2 means formula section
		int section = 0; 
		// Read formulas and separate them with "\n" (ascii code 10)
		try{
			for(int c = reader.read(); c != -1; c = reader.read()){
				if(c == 10 || c == 13){
					s = s.trim();
					if(!s.equals("")){
						if(s.startsWith("type")) section = 1;
						else if(section == 1) section = 2; //A type declaration section has been parsed previously, 
														   //therefore only the formula section remains.
						if(section == 2)
							beliefSet.add((FolFormula)this.parseFormula(new StringReader(s)));
						else if(section == 1) 
							this.parseTypeDeclaration(s,this.signature);
						else this.parseSortDeclaration(s,this.signature); //No type declaration or formula section has been parsed previously,
																		  //therefore this part is treated as the sorts declaration section.
					}
					s = "";
				} else 
					s += (char) c; 
			}	
			s = s.trim();
			if(!s.equals("")) beliefSet.add((FolFormula)this.parseFormula(new StringReader(s)));
		}catch(Exception e){
			throw new ParserException(e);
		}
		return beliefSet;
	}

	/**
	 * Parses a sort declaration of the form "SORTNAME "=" "{" (CONSTANTNAME ("," CONSTANTNAME)*)? "}""
	 * and modifies the given signature accordingly. 
	 * @param s a string
	 * @param sig a signature
	 * @throws ParserException if parsing fails
	 */
	public void parseSortDeclaration(String s, FolSignature sig) throws ParserException{
		if(!s.contains("=")) throw new ParserException("Missing '=' in sort declaration '" + s + "'.");
		String sortname = s.substring(0, s.indexOf("=")).trim();
		if(sig.containsSort(sortname)) throw new ParserException("Multiple declarations of sort '" + sortname + "'.");
		Sort theSort = new Sort(sortname);
		if(sortname.matches("[a-z,A-Z]([a-z,A-Z,0-9])*"))
			sig.add(theSort);
		else throw new ParserException("Illegal characters in sort definition '" + sortname + "'; declaration must conform to [a-z,A-Z]([a-z,A-Z,0-9])*");
		
		if(!s.contains("{")) throw new ParserException("Missing '{' in sort declaration '" + s + "',");
		if(!s.contains("}")) throw new ParserException("Missing '}' in sort declaration '" + s + "',");		
		String constants = s.substring(s.indexOf("{")+1, s.lastIndexOf("}"));
		if(constants.contains("{")) throw new ParserException("Multiple '{'s in sort declaration '" + s + "'.");
		if(constants.contains("}")) throw new ParserException("Multiple '}'s in sort declaration '" + s + "'.");
		String[] tokens = constants.split(",");
		for(String token: tokens){
			String c = token.trim();
			if(sig.containsConstant(c))
				throw new ParserException("Constant '" + c + "' has already been defined to be of sort '" + sig.getConstant(c).getSort() + "'.");
			if(c.matches("[a-z,A-Z]([^|&!<=>\\[\\]\\s\\(\\)\\^])*"))
				sig.add(new Constant(c, theSort));
			else throw new ParserException("Illegal characters in constant definition '" + c + "'; declaration must conform to [a-z,A-Z]([a-z,A-Z,0-9])*");
		}		
	}
	
	/**
	 * Parses a predicate declaration of the form "type" "(" PREDICATENAME "(" (SORTNAME ("," SORTNAME)*)? ")" ")"
	 * or a functor declaration of the form "type" "(" SORTNAME "=" FUNCTORNAME "(" (SORTNAME ("," SORTNAME)*)? ")" ")"
	 * and modifies the given signature accordingly.
	 * @param s a string
	 * @param sig a signature
	 * @throws ParserException if parsing fails
	 */
	public void parseTypeDeclaration(String s, FolSignature sig) throws ParserException{
		if(!s.startsWith("type")) throw new ParserException("Type declaration has to start with 'type'.");
		if(!s.contains("(")) throw new ParserException("Missing '(' in type declaration.");
		if(!s.contains(")")) throw new ParserException("Missing ')' in type declaration.");
		String dec = s.substring(s.indexOf("(")+1,s.lastIndexOf(")"));
		Sort targetSort = null;
		if(dec.contains("=")){
			String sortname = dec.substring(0, dec.indexOf("=")).trim();
			if(!sig.containsSort(sortname))
				throw new ParserException("Sort '" + sortname + "' has not been declared before.");
			targetSort = sig.getSort(sortname);
		} else if(!dec.contains( "(" )) {
		  if(dec.contains( ")")) throw new ParserException("Unexpected ')' in type declaration.");
      String name = dec.trim();
      if(name.matches("[a-z,A-Z]([a-z,A-Z,0-9])*"))
    	  sig.add( new Predicate(name) );
      else throw new ParserException("Illegal characters in predicate definition '" + name + "'; declaration must conform to [a-z,A-Z]([a-z,A-Z,0-9])*");
      
      return;
		}
		if(!dec.contains("(")) throw new ParserException("Missing '(' in type declaration.");
		if(!dec.contains(")")) throw new ParserException("Missing ')' in type declaration.");
		String name = dec.substring(0, dec.indexOf("(")).trim();
		String sorts = dec.substring(dec.indexOf("(")+1,dec.lastIndexOf(")"));
    List<Sort> theSorts = new ArrayList<Sort>();
		if(!sorts.trim().equals( "" )) {
  		String[] tokens = sorts.split(",");
	  		for(String token: tokens){
	  			String sort = token.trim();
	  			if(!sig.containsSort(sort))
	  				throw new ParserException("Sort '" + sort + "' has not been declared before.");
	  			theSorts.add(sig.getSort(sort));
	  		}
		}
		if(targetSort == null) { 
			if(name.matches("[a-z,A-Z]([a-z,A-Z,0-9])*"))
				sig.add(new Predicate(name,theSorts));
			else throw new ParserException("Illegal characters in predicate definition '" + name + "'; declaration must conform to [a-z,A-Z]([a-z,A-Z,0-9])*");
		}
		else{
			name = name.substring(name.indexOf("=")+1,name.length());	
			if(name.matches("[a-z,A-Z]([a-z,A-Z,0-9])*"))
				sig.add(new Functor(name,theSorts,targetSort));
			else throw new ParserException("Illegal characters in functor definition '" + name + "'; declaration must conform to [a-z,A-Z]([a-z,A-Z,0-9])*");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.Parser#parseFormula(java.io.Reader)
	 */
	@Override
	public FolFormula parseFormula(Reader reader) throws IOException, ParserException {
		Stack<Object> stack = new Stack<Object>();
		try{
			this.variables = new HashMap<String,Variable>();
			for(int c = reader.read(); c != -1; c = reader.read()) 
				this.consumeToken(stack, c);
			return this.parseQuantification(stack);
		}catch(Exception e){
			throw new ParserException(e);
		}
	}
	
	/**
	 * This method reads one token from the given reader and appropriately
	 * constructs a fol formula from the stream.
	 * @param stack a stack used for monitoring the read items.
	 * @param c a token from a stream.
	 * @throws ParserException in case of parser errors.
	 */
	private void consumeToken(Stack<Object> stack, int c) throws ParserException{
		try{
			String s = Character.toString((char) c);
			if(s.equals(" ")){
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
			}else if(s.equals(")")){
				if(!stack.contains("("))
					throw new ParserException("Missing opening parentheses.");				
				List<Object> l = new ArrayList<Object>();
				for(Object o = stack.pop(); !((o instanceof String) && ((String)o).equals("(")); o = stack.pop() )
					l.add(0, o);
				// if the preceding token is in {a,...,z,A,...,Z,0,...,9,==,/==} then treat the 
				// list as a term list, otherwise treat it as a quantification
				if(stack.size()>0 && stack.lastElement() instanceof String && ((String)stack.lastElement()).matches("[a-z,A-Z,0-9]|==|/=="))
					stack.push(this.parseTermlist(l));
				else stack.push(this.parseQuantification(l));
			//If two consecutive "|" or two consecutive "&" have been read, 
			//add them to the stack them as a single string.
			}else if(s.equals("|")){
				if(stack.lastElement().equals("|")){
					stack.pop();
					stack.push("||");
				}else stack.push(s);					
			}else if(s.equals("&")){
				if(stack.lastElement().equals("&")){
					stack.pop();
					stack.push("&&"); 
				}else stack.push(s);
			}else if(s.equals("^")){
				if(stack.lastElement().equals("^")){
					stack.pop();
					stack.push("^^"); 
				}else stack.push(s);
			}else if(s.equals(">")){
				if(stack.lastElement().equals("=")){
					if (stack.size() >=2 && stack.get(stack.size()-2).equals("<")) {
						stack.pop();
						stack.pop();
						stack.push("<=>");
					}
					else {
						stack.pop();
						stack.push("=>"); } 	
				}else stack.push(s);
			}
			else if(s.equals("=")){
				if(stack.size() >=1 && stack.lastElement().equals("=")){
					if (stack.size() >=2 && stack.get(stack.size()-2).equals("/")) {
						stack.pop();
						stack.pop();
						stack.push("/==");
					}
					else {
						stack.pop();
						stack.push("=="); } 	
				}else stack.push(s);
			} else stack.push(s);
		}catch(Exception e){
			throw new ParserException(e);
		}		
	}

	/**
	 * Parses a term list as a list of String tokens or terms into a list of terms.
	 * @param l a list of objects, either String tokens or objects of type List.
	 * @return a list of terms.
	 * @throws ParserException if the list could not be parsed.
	 */
	@SuppressWarnings("unchecked")
	private List<Term<?>> parseTermlist(List<Object> l) throws ParserException{
		List<Term<?>> terms = new ArrayList<Term<?>>();
		String current = "";
		for(Object o: l){
			if(o instanceof String || o instanceof List){
				if(o.equals(",") || o instanceof List){
					if(o instanceof List){
						if(!this.signature.containsFunctor(current))
							throw new ParserException("Functor '" + current + "' has not been declared.");
						// check correct sorts of terms
						Functor f = this.signature.getFunctor(current);
						List<Term<?>> args = new ArrayList<Term<?>>();
						if(f.getArity() != ((List<Term<?>>)o).size())
							throw new IllegalArgumentException("Functor '" + f + "' has arity '" + f.getArity() + "'.");
						for(int i = 0; i < f.getArity(); i++){
							Term<?> t = ((List<Term<?>>)o).get(i);
							if(t instanceof Variable){
								if(this.variables.containsKey(((Variable)t).get())){
									if(!this.variables.get(((Variable)t).get()).getSort().equals(f.getArgumentTypes().get(i)))
										throw new ParserException("Variable '" + t + "' has wrong sort.");
									args.add(this.variables.get(((Variable)t).get()));
								}else{
									Variable v = new Variable(((Variable)t).get(),f.getArgumentTypes().get(i));
									args.add(v);
									this.variables.put(v.get(), v);
								}								
							}else if(!t.getSort().equals(f.getArgumentTypes().get(i)))
								throw new ParserException("Term '" + t + "' has the wrong sort.");
							else args.add(t);
						}
						terms.add(new FunctionalTerm(f,args));
					}else if(!current.equals("") && current.substring(0, 1).matches("[a-z]"))
						if(!signature.containsConstant(current) && !ignoreUndeclaredConstants)
							throw new ParserException("Constant '" + current + "' has not been declared.");
						else if(!signature.containsConstant(current) && ignoreUndeclaredConstants) {							
							Constant c = new Constant(current);
							this.signature.add(c);
							terms.add(c);
						}else terms.add(this.signature.getConstant(current));
					else if(!current.equals("") && current.substring(0, 1).matches("[A-Z]"))
						terms.add(new Variable(current));
					else if(!current.equals(""))
						throw new ParserException("'" + current + "' could not be parsed.");
					current = "";
				}else{
					current += (String) o;
					continue;
				}					
			}else if(o instanceof Term)
				terms.add((Term<?>)o);
			else throw new ParserException("Unrecognized token '" + o + "'.");
		}
		//parse the last element
		if(!current.equals("")){
			if(current.substring(0, 1).matches("[a-z]"))
				if(!signature.containsConstant(current) && !ignoreUndeclaredConstants)
					throw new ParserException("Constant '" + current + "' has not been declared.");
				else if(!signature.containsConstant(current) && ignoreUndeclaredConstants) {							
					Constant c = new Constant(current);
					this.signature.add(c);
					terms.add(c);
				}else terms.add(this.signature.getConstant(current));
			else if(current.substring(0, 1).matches("[A-Z]"))
				terms.add(new Variable(current));
			else if(!current.equals(""))
				throw new ParserException("'" + current + "' could not be parsed.");
		}
		return terms;
	}
	
	/**
	 * Parses a quantified formula as a list of String tokens or formulas.
	 * @param l a list objects, either String tokens or objects of type FolFormula.
	 * @return a FolFormula.
	 * @throws ParserException if the list could not be parsed.
	 */
	private FolFormula parseQuantification(List<Object> l) throws ParserException{
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

		FolFormula formula;
		if (l.get(idx+1) instanceof FolFormula) 
			formula = (FolFormula) l.get(idx+1);
		else  
			throw new ParserException("Unrecognized formula type '" + l.get(idx+1) + "'."); 
		
		List<Variable> bVars = new ArrayList<Variable>();;
		for(Variable v: formula.getUnboundVariables()){
			if(v.get().equals(var))
					bVars.add(v);
		}
		
		if(bVars.isEmpty())
			throw new ParserException("Variable(s) '" + var + "' not found in quantification.");
		
		Set<Variable> vars = new HashSet<Variable>();
		
		int j = 0; //This index is used later to determine if there are more elements in the list to the right of the quantified formula
		for (int i = 0; i < bVars.size(); i++) {
			vars.add(bVars.get(i)); 
			j += (bVars.get(i).get().length());
		}
		j += bVars.size();
		
		this.variables.remove(var);
	
		FolFormula result;
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
	
	/**
	 * Parses an equivalence as a list of String tokens or formulas into a fol formula.
	 * @param l a list objects, either String tokens or objects of type FolFormula.
	 * @return a FolFormula.
	 * @throws ParserException if the list could not be parsed.
	 */
	private FolFormula parseEquivalence(List<Object> l) {
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
		return new Equivalence(parseQuantification(left),parseQuantification(right));	
	}
	
	/**
	 * Parses an implication as a list of String tokens or formulas into a fol formula.
	 * @param l a list objects, either String tokens or objects of type FolFormula.
	 * @return a FolFormula.
	 * @throws ParserException if the list could not be parsed.
	 */
	private FolFormula parseImplication(List<Object> l) {
		if(l.isEmpty())
			throw new ParserException("Empty parentheses.");
		if(!(l.contains(LogicalSymbols.IMPLICATION())))
			return this.parseExclusiveDisjunction(l);	
	
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
		return new Implication(parseQuantification(left),parseQuantification(right));	
	}
	
	/**
	 * Parses an exclusive disjunction as a list of String tokens or formulas into a fol formula.
	 * This method expects no parentheses in the list and as such treats the formula as a disjunction.
	 * @param l a list objects, either String tokens or objects of type FolFormula.
	 * @return a FolFormula.
	 * @throws ParserException if the list could not be parsed.
	 */
	private FolFormula parseExclusiveDisjunction(List<Object> l){
		if(l.isEmpty())
			throw new ParserException("Empty parentheses.");
		if(!(l.contains(LogicalSymbols.EXCLUSIVEDISJUNCTION())))
			return this.parseDisjunction(l);		
		ExclusiveDisjunction d = new ExclusiveDisjunction();
		List<Object> tmp = new ArrayList<Object>(); 
		for(Object o: l){
			if((o instanceof String) && ((String)o).equals(LogicalSymbols.EXCLUSIVEDISJUNCTION()) ){
				d.add(this.parseDisjunction(tmp));
				tmp = new ArrayList<Object>();
			}else tmp.add(o);
		}		
		d.add(this.parseDisjunction(tmp));
		if(d.size() > 1)	
			return d;
		throw new ParserException("General parsing exception.");
	}
	
		
	/**
	 * Parses a disjunction as a list of String tokens or formulas into a fol formula.
	 * This method expects no parentheses in the list and as such treats the formula as a disjunction.
	 * @param l a list objects, either String tokens or objects of type FolFormula.
	 * @return a FolFormula.
	 * @throws ParserException if the list could not be parsed.
	 */
	private FolFormula parseDisjunction(List<Object> l){
		if(l.isEmpty())
			throw new ParserException("Empty parentheses.");
		if(!(l.contains(LogicalSymbols.DISJUNCTION())))
			return this.parseConjunction(l);		
		Disjunction d = new Disjunction();
		List<Object> tmp = new ArrayList<Object>(); 
		for(Object o: l){
			if((o instanceof String) && ((String)o).equals(LogicalSymbols.DISJUNCTION()) ){
				d.add(this.parseConjunction(tmp));
				tmp = new ArrayList<Object>();
			}else tmp.add(o);
		}		
		d.add(this.parseConjunction(tmp));
		if(d.size() > 1)	
			return d;
		throw new ParserException("General parsing exception.");
	}
	
	/**
	 * Parses a simple conjunction as a list of String tokens or formulas into a fol formula.
	 * @param l a list objects, either String tokens or objects of type FolFormula.
	 * @return a FolFormula.
	 * @throws ParserException if the list could not be parsed.
	 */
	private FolFormula parseConjunction(List<Object> l) throws ParserException{
		if(l.isEmpty())
			throw new ParserException("General parsing exception.");
		if(!(l.contains(LogicalSymbols.CONJUNCTION())))
			return this.parseNegation(l);		
		Conjunction c = new Conjunction();
		List<Object> tmp = new ArrayList<Object>(); 
		for(Object o: l){
			if((o instanceof String) && ((String)o).equals(LogicalSymbols.CONJUNCTION()) ){
				c.add(this.parseNegation(tmp));
				tmp = new ArrayList<Object>();
			}else tmp.add(o);
		}		
		c.add(this.parseNegation(tmp));
		if(c.size() > 1)	
			return c;
		throw new ParserException("General parsing exception.");
	}
	
	/**
	 * Parses a simple formula as a list of String tokens or formulas into a fol formula.
	 * This method expects no parentheses, no disjunctions, and no conjunctions in the list and as such treats the formula as a negation.
	 * @param l a list objects, either String tokens or objects of type FolFormula.
	 * @return a fol formula.
	 * @throws ParserException if the list could not be parsed.
	 */
	private FolFormula parseNegation(List<Object> l) throws ParserException{
		if(l.get(0).equals(LogicalSymbols.CLASSICAL_NEGATION())){
			l.remove(0);
			return new Negation(this.parseNegation(l));			
		}
		return this.parseAtomic(l);		
	}
	
	/**
	 * Parses a simple formula as a list of String tokens or formulas into a fol formula.
	 * This method expects no parentheses, no disjunctions, no conjunctions, and no negation in the list
	 * and as such treats the formula as an atomic construct, either a contradiction, a tautology, or an atom (a predicate
	 * with a list of terms).
	 * @param l a list objects, either String tokens or objects of type folFormula.
	 * @return a fol formula.
	 * @throws ParserException if parsing fails
	 */
	@SuppressWarnings("unchecked")
	private FolFormula parseAtomic(List<Object> l) throws ParserException{
		if(l.size() == 1){
			Object o = l.get(0);
			if(o instanceof FolFormula) return (FolFormula) o;
			if(o instanceof String){
				String s = (String) o;
				if(s.equals(LogicalSymbols.CONTRADICTION()))
					return new Contradiction();
				if(s.equals(LogicalSymbols.TAUTOLOGY()))
					return new Tautology();
				if(this.signature.containsPredicate( s )) {
				  Predicate p = this.signature.getPredicate( s );
				  if(p.getArity() > 0)
				    throw new IllegalArgumentException("Predicate '" + p + "' has arity '" + p.getArity() + "'.");
				  return new FolAtom(p);
				}
			}
			throw new ParserException("Unknown object " + o);
		}else{
			// List l should be a list of String followed by a List<Term>
			String s = "";
			List<Term<?>> terms = null;
			for(Object o : l){
				if(!(o instanceof String))
					if(o instanceof List && l.lastIndexOf(o) == l.size()-1) 
						terms = (List<Term<?>>) o; 

					else throw new ParserException("Unknown object " + o);
				else s += (String) o;
			}
			
			//Check if the formula is an equality predicate or inequality predicate written in the alternate
			//syntax (a==b) or (a/==b) and parse it accordingly
			if ((s.contains(LogicalSymbols.EQUALITY()) || s.contains(LogicalSymbols.INEQUALITY())) 
					&& !(s.substring(0,2).equals((LogicalSymbols.EQUALITY())) || s.substring(0,3).equals((LogicalSymbols.INEQUALITY())) )) {
				String op = LogicalSymbols.INEQUALITY();
				if (s.indexOf(LogicalSymbols.INEQUALITY()) == -1)
					op = LogicalSymbols.EQUALITY();
				String[] parts = s.split(op);
				
				List<Object> newterms = new LinkedList<Object>();
				for (int i = 0; i < parts[0].length(); i++)  
				    newterms.add(String.valueOf(parts[0].charAt(i)));
				newterms.add(",");
				for (int i = 0; i < parts[1].length(); i++)  
					 newterms.add(String.valueOf(parts[1].charAt(i)));
				terms = this.parseTermlist(newterms);
				s=op;
			}
			
			if(this.signature.containsPredicate(s)){
			  // check for zero-arity predicate
			  if(terms == null) 
			    terms = new LinkedList<Term<?>>(); 
				// check correct sorts of terms
				Predicate p = this.signature.getPredicate(s);
				List<Term<?>> args = new ArrayList<Term<?>>();
				if(p.getArity() != terms.size())
					throw new IllegalArgumentException("Predicate '" + p + "' has arity '" + p.getArity() + "'.");
				for(int i = 0; i < p.getArity(); i++){
					Term<?> t = terms.get(i);
					if(t instanceof Variable){
						if(this.variables.containsKey(((Variable)t).get())){
							Sort sortOfVariable = this.variables.get(((Variable)t).get()).getSort();
							if(!sortOfVariable.equals(p.getArgumentTypes().get(i))) 
									throw new ParserException("Variable '" + t + "," + t.getSort() + "' has wrong sort."); 
							args.add(this.variables.get(((Variable)t).get()));
						}else{
							Variable v;
							if (!(p instanceof EqualityPredicate || p instanceof InequalityPredicate)) 
								v = new Variable(((Variable)t).get(),p.getArgumentTypes().get(i));
							else
								v = new Variable(((Variable)t).get(),Sort.ANY);
							args.add(v);
							this.variables.put(v.get(), v);}						
					}else if(!(t.getSort().equals(p.getArgumentTypes().get(i)))) {
						throw new ParserException("Term '" + t + "' has the wrong sort."); }
					else args.add(t);
				}	
				if (p.getName().equals("==")) 
					return new FolAtom(new EqualityPredicate(),args); 
				else if (p.getName().equals("/==")) 
					return new FolAtom(new InequalityPredicate(),args); 
				return new FolAtom(p,args); 
			}
			if (s.equals(LogicalSymbols.EQUALITY()) || s.equals(LogicalSymbols.INEQUALITY()))
				throw new ParserException("Equality/Inequality predicate " + s + " is not part of this parser's FolSignature.");
			throw new ParserException("Predicate '" + s + "' has not been declared.");
		}		
	}
	
	/**
	 * This function parses only the sorts declaration and type declaration parts
	 * of a belief base.
	 * @param s the signature as a string
	 * 
	 * @return the parsed fol signature
	 */
	public FolSignature parseSignature(String s) {
		this.setSignature(new FolSignature());
		String[] lines = s.split("\n");
		for (String l : lines) {
			l = l.trim();
			if (l.contains("=")) 
				parseSortDeclaration(l,this.getSignature());
			else if (l.startsWith("type"))
				parseTypeDeclaration(l, this.getSignature());
		}
		return this.getSignature();
	}
	
	/**
	 * Sets the signature for this parser.
	 * @param signature a fol signature.
	 */
	public void setSignature(FolSignature signature){
		this.signature = signature;
	}
	
	/**
	 * Returns the signature of this parser.
	 * @return the signature of this parser.
	 */
	public FolSignature getSignature(){
		return this.signature;
	}

	public Map<String, Variable> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, Variable> variables) {
		this.variables = variables;
	}
}
