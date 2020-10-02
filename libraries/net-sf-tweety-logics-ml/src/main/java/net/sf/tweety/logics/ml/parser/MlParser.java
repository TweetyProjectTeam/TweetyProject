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
package net.sf.tweety.logics.ml.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.LogicalSymbols;
import net.sf.tweety.logics.commons.syntax.FunctionalTerm;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Contradiction;
import net.sf.tweety.logics.fol.syntax.Disjunction;
import net.sf.tweety.logics.fol.syntax.EqualityPredicate;
import net.sf.tweety.logics.fol.syntax.Equivalence;
import net.sf.tweety.logics.fol.syntax.ExclusiveDisjunction;
import net.sf.tweety.logics.fol.syntax.ExistsQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.FolAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.syntax.ForallQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.Implication;
import net.sf.tweety.logics.fol.syntax.InequalityPredicate;
import net.sf.tweety.logics.fol.syntax.Negation;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.fol.syntax.Tautology;
import net.sf.tweety.logics.ml.syntax.MlBeliefSet;
import net.sf.tweety.logics.ml.syntax.MlFormula;
import net.sf.tweety.logics.ml.syntax.Necessity;
import net.sf.tweety.logics.ml.syntax.Possibility;

/**
 * This class implements a parser for modal logic. 
 * 
 * The BNF for a modal knowledge base is given by (starting symbol is KB)
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
 * <br>                  "[]" "(" FORMULA ")" | "&lt;&gt;" "(" FORMULA ")" | FORMULA "^^" FORMULA
 * <br> ATOM		::== PREDICATENAME ("(" TERM ("," TERM)* ")")?
 * <br> TERM		::== VARIABLENAME | CONSTANTNAME | FUNCTORNAME "(" (TERM ("," TERM)*)?  ")" 
 * <br> 
 * <br> where SORTNAME, PREDICATENAME, CONSTANTNAME, and FUNCTORNAME are sequences of
 * <br> symbols from {a,...,z,A,...,Z,0,...,9} with a letter at the beginning and VARIABLENAME
 * <br> is a sequence of symbols from {a,...,z,A,...,Z,0,...,9} with an uppercase letter at the beginning.
 * <br>
 * <br> Note: Equality/Inequality predicates ("==" and "/==") can only be parsed if the parser is given a FolSignature 
 * <br> with equality (which is not the case by default).
 * 
 * @author Matthias Thimm
 * @author Anna Gessler
 */

public class MlParser extends Parser<MlBeliefSet,RelationalFormula> {

	/**
	 * First-order logic parser used for parsing sorts and type declaration.
	 */
	FolParser folparser; 
	
	public MlParser() {
		folparser = new FolParser();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Parser#parseBeliefBase(java.io.Reader)
	 */
	@Override
	public MlBeliefSet parseBeliefBase(Reader reader) throws IOException, ParserException {
		MlBeliefSet beliefSet = new MlBeliefSet();
		String s = "";
		// For keeping track of the section of the file:
		// 0 means sorts declaration
		// 1 means type declaration, i.e. functor/predicate declaration
		// 2 means formula section
		int section = 0; 
		
		try{
			for(int c = reader.read(); c != -1; c = reader.read()){
				if(c == 10 || c == 13){ // read from the reader and separate formulas by "\n" (ascii code 10)
					s = s.trim();
					if(!s.equals("")){
						if(s.startsWith("type")) section = 1;
						else if(section == 1) section = 2; //A type declaration section has been parsed previously, 
														   //therefore only the formula section remains.
						if(section == 2)
							beliefSet.add((RelationalFormula)this.parseFormula(new StringReader(s)));
						else if(section == 1)
							this.folparser.parseTypeDeclaration(s,this.folparser.getSignature());
						else this.folparser.parseSortDeclaration(s,this.folparser.getSignature()); //No type declaration or formula section has been parsed previously,
																		       					   //therefore this part is treated as the sorts declaration section.
					}
					s = "";
				}else{
					s += (char) c;
				}
			}	
			s = s.trim();
			if(!s.equals("")) beliefSet.add((RelationalFormula) this.parseFormula(new StringReader(s)));
		}catch(Exception e){
			throw new ParserException(e);
		}
		return beliefSet;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Parser#parseFormula(java.io.Reader)
	 */
	@Override
	public RelationalFormula parseFormula(Reader reader) throws IOException, ParserException {
		Stack<Object> stack = new Stack<Object>();
		try{
			this.folparser.setVariables(new HashMap<String,Variable>());
			for(int c = reader.read(); c != -1; c = reader.read())
				this.consumeToken(stack, c);
			return this.parseQuantification(stack);
					}catch(Exception e){
			throw new ParserException(e);
		}
	}

	/**
	 * This method reads one token from the given reader and appropriately
	 * constructs a modal formula from the stream.
	 * @param stack a stack used for monitoring the read items.
	 * @param c a token from a stream.
	 * @throws ParserException in case of parser errors.
	 */
	private void consumeToken(Stack<Object> stack, int c) throws ParserException {
			try {
				String s = Character.toString((char) c); 
				if(s.equals(" ")){
					//If a whitespace is read and the last 6 consumed tokens spell "forall" or "exists", 
					//remove them from the stack and re-add them as a single string.
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
				}
			else if(s.equals(")")){
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
			}else if(s.equals("]")){
				if(stack.lastElement().equals("[")){
					stack.pop();
					stack.push("[]");
				}else stack.push(s);
			}else if(s.equals(">")){
				if(stack.lastElement().equals("<")){
					stack.pop();
					stack.push("<>"); }
				else if(stack.lastElement().equals("=")){
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
	 * @param l a list objects, either String tokens or objects of type List.
	 * @return a list of terms.
	 * @throws ParserException if the list could not be parsed.
	 */
	@SuppressWarnings("unchecked")
	private List<Term<?>> parseTermlist(List<Object> l) {
		List<Term<?>> terms = new ArrayList<Term<?>>();
		String current = "";
		for(Object o: l){
			if(o instanceof String || o instanceof List){
				if(o.equals(",") || o instanceof List){
					if(o instanceof List){
						if(!this.folparser.getSignature().containsFunctor(current))
							throw new ParserException("Functor '" + current + "' has not been declared.");
						// check correct sorts of terms
						Functor f = this.folparser.getSignature().getFunctor(current);
						List<Term<?>> args = new ArrayList<Term<?>>();
						if(f.getArity() != ((List<Term<?>>)o).size())
							throw new IllegalArgumentException("Functor '" + f + "' has arity '" + f.getArity() + "'.");
						for(int i = 0; i < f.getArity(); i++){
							Term<?> t = ((List<Term<?>>)o).get(i);
							if(t instanceof Variable){
								if(this.folparser.getVariables().containsKey(((Variable)t).get())){
									if(!this.folparser.getVariables().get(((Variable)t).get()).getSort().equals(f.getArgumentTypes().get(i)))
										throw new ParserException("Variable '" + t + "' has wrong sort.");
									args.add(this.folparser.getVariables().get(((Variable)t).get()));
								}else{
									Variable v = new Variable(((Variable)t).get(),f.getArgumentTypes().get(i));
									args.add(v);
									Map<String, Variable> map = this.folparser.getVariables();
									map.put(v.get(), v);
									this.folparser.setVariables(map);
								}								
							}else if(!t.getSort().equals(f.getArgumentTypes().get(i)))
								throw new ParserException("Term '" + t + "' has the wrong sort.");
							else args.add(t);
						}
						terms.add(new FunctionalTerm(f,args));
					}else if(!current.equals("") && current.substring(0, 1).matches("[a-z]"))
						if(!this.folparser.getSignature().containsConstant(current)) throw new ParserException("Constant '" + current + "' has not been declared.");
						else terms.add(this.folparser.getSignature().getConstant(current));
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
				if(!this.folparser.getSignature().containsConstant(current)) throw new ParserException("Constant '" + current + "' has not been declared.");
				else terms.add(this.folparser.getSignature().getConstant(current));
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
	private RelationalFormula parseQuantification(List<Object> l) throws ParserException{
		if(l.isEmpty())
			throw new ParserException("Empty parentheses.");
		if(!(l.contains(LogicalSymbols.EXISTSQUANTIFIER()) || l.contains(LogicalSymbols.FORALLQUANTIFIER())) || l.get(0).equals(LogicalSymbols.NECESSITY()) || l.get(0).equals(LogicalSymbols.POSSIBILITY())) 
			return this.parseModalization(l); 

		//If the quantification is not the first conjunct/disjunct/subformula of
		//the formula, split the list at position of first non-quantor operator
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
		
		Map<String, Variable> map = this.folparser.getVariables();
		map.remove(var);
		this.folparser.setVariables(map);
	
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
	 * Parses a formula containing at least one modal operator as a list of String tokens or formulas.
	 * @param l a list objects, either String tokens or objects of type FolFormula.
	 * @return a FolFormula.
	 * @throws ParserException if the list could not be parsed.
	 */
	private RelationalFormula parseModalization(List<Object> l) throws ParserException {
		if(l.isEmpty()) {
			throw new ParserException("Empty parentheses."); }
		if(!( l.contains(LogicalSymbols.NECESSITY()) || l.contains(LogicalSymbols.POSSIBILITY()) ) )  
			return this.parseEquivalence(l); 
		
		//If the modalized subformula is not the first conjunct/disjunct/subformula of
		//the formula, split the list at position of first non-quantor operator
		if (!(l.get(0).equals(LogicalSymbols.POSSIBILITY()) || l.get(0).equals(LogicalSymbols.NECESSITY()))) { 
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
		MlFormula result;
		if (!(l.get(1) instanceof RelationalFormula))
			throw new ParserException("Unrecognized formula type " + l.get(1) + ". Probably caused by missing parentheses around modalized formula " + l.get(1) + ".");
		if (l.get(0).equals("[]")) 
			result = new Necessity((RelationalFormula) l.get(1));
		else if (l.get(0).equals("<>")) 
			result = new Possibility((RelationalFormula) l.get(1));
		else
			throw new ParserException("Unknown object " + l.get(0));
		
				//Add additional conjuncts/disjuncts to the right of the modalization (if applicable)
		if (l.size() > 2) {
			if (l.get(2).equals(LogicalSymbols.CONJUNCTION()))
				return new Conjunction(result, parseQuantification(new ArrayList<Object>(l.subList(3, l.size()))));
			else if (l.get(2).equals(LogicalSymbols.DISJUNCTION()))
				return new Disjunction(result, parseQuantification(new ArrayList<Object>(l.subList(3, l.size()))));
			else if (l.get(2).equals(LogicalSymbols.EQUIVALENCE())) 
				return new Equivalence(result, parseQuantification(new ArrayList<Object>(l.subList(3, l.size()))));
			else if (l.get(2).equals(LogicalSymbols.IMPLICATION()))
				return new Implication(result, parseQuantification(new ArrayList<Object>(l.subList(3, l.size()))));
			else 
				throw new ParserException("Unrecognized symbol " + l.get(2));
		}
		return result;	
	}
	
	/**
	 * Parses an equivalence as a list of String tokens or formulas into a fol formula.
	 * @param l a list objects, either String tokens or objects of type FolFormula.
	 * @return a FolFormula.
	 * @throws ParserException if the list could not be parsed.
	 */
	private RelationalFormula parseEquivalence(List<Object> l) {
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
	private RelationalFormula parseImplication(List<Object> l) {
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
	private RelationalFormula parseExclusiveDisjunction(List<Object> l){
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
	private RelationalFormula parseDisjunction(List<Object> l){
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
	private RelationalFormula parseConjunction(List<Object> l) throws ParserException{
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
	private RelationalFormula parseNegation(List<Object> l) throws ParserException{
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
	private RelationalFormula parseAtomic(List<Object> l) throws ParserException{
		if(l.size() == 1){
			Object o = l.get(0);
			if(o instanceof RelationalFormula) return (RelationalFormula) o;
			if(o instanceof String){
				String s = (String) o;
				if(s.equals(LogicalSymbols.CONTRADICTION()))
					return new Contradiction();
				if(s.equals(LogicalSymbols.TAUTOLOGY()))
					return new Tautology();
				if(this. folparser.getSignature().containsPredicate( s )) {
				  Predicate p = folparser.getSignature().getPredicate( s );
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
			
			if(folparser.getSignature().containsPredicate(s)){
				// check for zero-arity predicate
				if(terms == null)  
					terms = new LinkedList<Term<?>>(); 
				// check correct sorts of terms
				Predicate p = this.getSignature().getPredicate(s);
				List<Term<?>> args = new ArrayList<Term<?>>();
				if(p.getArity() != terms.size())
					throw new IllegalArgumentException("Predicate '" + p + "' has arity '" + p.getArity() + "'.");
				for(int i = 0; i < p.getArity(); i++){
					Term<?> t = terms.get(i);
					if(t instanceof Variable){
						if(this.folparser.getVariables().containsKey(((Variable)t).get())){
							Sort sortOfVariable = this.folparser.getVariables().get(((Variable)t).get()).getSort();
							if(!sortOfVariable.equals(p.getArgumentTypes().get(i))) 
								throw new ParserException("Variable '" + t + "," + t.getSort() + "' has wrong sort."); 
							args.add(this.folparser.getVariables().get(((Variable)t).get()));
						}else{
							Variable v;
							if (!(p instanceof EqualityPredicate || p instanceof InequalityPredicate)) 
								v = new Variable(((Variable)t).get(),p.getArgumentTypes().get(i));
							else
								v = new Variable(((Variable)t).get(),Sort.ANY);
									args.add(v);
									Map<String, Variable> map =this.folparser.getVariables();
									map.put(v.get(), v);
									this.folparser.setVariables(map); }						
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
	 * Sets the signature for this parser.
	 * @param signature a fol signature.
	 */
	public void setSignature(FolSignature signature){
		this.folparser.setSignature(signature);
	}
	
	/**
	 * Returns the signature of this parser.
	 * @return the signature of this parser.
	 */
	public FolSignature getSignature(){
		return this.folparser.getSignature();
	}
	
}
