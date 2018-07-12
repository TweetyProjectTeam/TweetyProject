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
package net.sf.tweety.logics.fol.parser;

import java.io.FileNotFoundException;
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

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.LogicalSymbols;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.FunctionalTerm;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Contradiction;
import net.sf.tweety.logics.fol.syntax.Disjunction;
import net.sf.tweety.logics.fol.syntax.EqualityPredicate;
import net.sf.tweety.logics.fol.syntax.Equivalence;
import net.sf.tweety.logics.fol.syntax.ExistsQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.syntax.ForallQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.Implication;
import net.sf.tweety.logics.fol.syntax.InequalityPredicate;
import net.sf.tweety.logics.fol.syntax.Negation;
import net.sf.tweety.logics.fol.syntax.Tautology;

/**
 * This class implements a parser for the TPTP syntax that parses single fol formulas and knowledge bases 
 * (TPTP problem files or axiom files). 
 * <p> TPTP files consist of any of the following in any order and separated by newlines:
 *  <ul>
 *  <li> formulas </li>
 *  <li> comments, i.e. lines starting with "%" </li>
 *  <li> includes of other TPTP files, i.e. lines like "include('path/to/file',[optional,formula,names])" </li>
 *  </ul>
 * </p>
 * 
 * <p> The syntax for first-order logic formulas is 'fof(name,role,formula,source,[useful_info]).':
 * <ul>
 * <li> name is the name of the formula </li>
 * <li> role is anything out of {axiom, hypothesis, definition, assumption, lemma, theorem, corollary
 * conjecture, negated_conjecture} </li>
 * <li> formula is the actual formula </li>
 * <li> The other arguments are optional and ignored by this parser. </li>
 * </ul>
 * </p>
 * 
 * <p>Supported operators and pre-defined predicates in formulas:
 * <br> Negation: ~ formula					
 * <br> Conjunction: formula & formula		
 * <br> Disjunction: formula | formula		
 * <br> Implication: formula => formula <br>formula <= formula   
 * <br> Equivalence: formula <=> formula	
 * <br> Universal quantifier: ! [Variable1,Variable2,...] : (formula)	</li> 
 * <br> Existential quantifier:	? [Variable1,Variable2,...] : (formula)	</li> 
 * <br> Tautology: $true 
 * <br> Contradiction: $false 
 * <br> Equality: = (TODO soon to be added)
 * <br> Inequality: != (TODO soon to be added)
 * </ul>
 * </p>
 * 
 * @author Anna Gessler
 */
public class TPTPParser extends Parser<FolBeliefSet> {
	/**
	 * Keeps track of the signature.
	 */
	private FolSignature signature = new FolSignature(true);
	
	/**
	 * Keeps track of variables defined.
	 */
	private Map<String,Variable> variables;
	
	/**
	 * Location of included files (optional). 
	 * This path will be prepended to the path given in the include('included_file') paths.
	 */
	private String includePath = "";
	
	/**
	 * Regular expression that restricts which formulas will be parsed. Formulas with
	 * names that do not match the expression will not be parsed. By default, all formulas 
	 * are parsed.
	 */
	private String formulaNames = ".+";
	
	/**
	 * Regular expression that restricts which formulas will be parsed. Formulas with
	 * roles that do not match the expression will not be parsed. By default, all formulas 
	 * are parsed.
	 */
	private String formulaRoles = "axiom|hypothesis|definition|assumption|lemma|theorem|corollary|conjecture|negated_conjecture";

	@Override
	public FolBeliefSet parseBeliefBase(Reader reader) throws IOException, ParserException {
		FolBeliefSet beliefSet = new FolBeliefSet();
		String s = "";
		boolean lineIsComment = false;
			
		//Parse each non-comment line as a formula
		try {
			for (int c = reader.read(); c != -1; c = reader.read()) {
				if (c==37)  //Ignore comments (lines starting with '%')
					lineIsComment = true;
				else if (c == 10){	
					s = s.trim();
					if (!lineIsComment && !s.isEmpty() && !(s.endsWith(".") || s.endsWith(".\n"))) //If formulas contain linebreaks, concatenate them
						continue;
					if (!lineIsComment && !s.equals("")) {
						if (s.startsWith("include")) 
							beliefSet.addAll(parseIncludedFiles(s)); //Parse formulas in included files
						else
							beliefSet.add((FolFormula)this.parseFormula(new StringReader(s)));}
					s = "";
					lineIsComment = false;
				} 
				else 
					s += (char) c; 
			}	
			s = s.trim();
			if (!lineIsComment && !s.equals(""))  {
				if (s.startsWith("include")) 
					beliefSet.addAll(parseIncludedFiles(s)); //Parse formulas in included files
				else
					beliefSet.add((FolFormula)this.parseFormula(new StringReader(s)));
				}
		} catch(Exception e){
			throw new ParserException(e);
		}
		beliefSet.remove(null);
		return beliefSet;
	}

	/**
	 * Parses formulas of an included TPTP problem file. If the given string 
	 * contains a list of formula names in addition to the include path, 
	 * only formulas of the given names will be parsed.
	 * 
	 * @param s String of format "include('path/to/file')." or "include('path/to/file',[formula,names,separated,by,commata])."
	 * @return a belief set containing the parsed included formulas
	 * @throws FileNotFoundException
	 * @throws ParserException
	 * @throws IOException
	 */
	private FolBeliefSet parseIncludedFiles(String s) throws FileNotFoundException, ParserException, IOException {
		String pathOfIncludedFile = s.substring(s.indexOf("'")+1, s.lastIndexOf("'"));
		
		String formulaNames = new String();
		if (s.length()>s.lastIndexOf("'")+3) { //Check for additional arguments specifying formulas names to be parsed
			formulaNames = s.substring(s.lastIndexOf("'")+3,s.length()-3);
			formulaNames = formulaNames.replaceAll(",", "|");
		}
		
		if (formulaNames.isEmpty())
			return this.parseBeliefBaseFromFile(this.includePath + pathOfIncludedFile);
		
		this.setFormulaNames(formulaNames);
		FolBeliefSet formulas = this.parseBeliefBaseFromFile(this.includePath + pathOfIncludedFile);
		this.resetFormulaNames();
		return formulas;
	}

	@Override
	public Formula parseFormula(Reader reader) throws IOException, ParserException {
		String str = "";
		for(int c = reader.read(); c != -1; c = reader.read()) 
			str += (char)c;
			
		if (!str.matches("fof\\(.+,.+,.+(,.*)*\\)\\.?"))
			throw new ParserException("Formula "+ str + " does not match fof syntax 'fof(name,role,formula,source,[useful_info])'.");
		
		//Split formula into different parts
		int index1 = str.indexOf(',');
		int index2 = 0;
		for (int i = index1+1; i < str.length(); i++) {
			if (str.charAt(i) == ',') {
				index2 = i;
				break;
			}
		}

		//TODO: Additional arguments (source,[useful_info])' will cause errors
		String name = str.substring(4,index1);
		String role = str.substring(index1+1,index2);
		String theFormula = str.substring(index2+1,str.length()-2);
		
		parseTypes(theFormula);

		//Ignore formulas of certain names or roles
		if (!name.matches(this.formulaNames) || !role.toLowerCase().matches(this.formulaRoles)) 
			return null;

		Stack<Object> stack = new Stack<Object>();
		try{
			this.variables = new HashMap<String,Variable>();
			for (int i = 0; i < theFormula.length(); i++)
				this.consumeToken(stack, theFormula.charAt(i));	
			return this.parseQuantification(stack);
		}catch(Exception e){
			throw new ParserException(e);
		}
	}
	
	/**
	 * Parse signature of a problem file. 
	 * If some or all of the symbols are already part of the signature,
	 * they will not be overwritten. 
	 * 
	 * @param a String of a formula
	 */
	private void parseTypes(String formula) {
		if ((formula.charAt(0)=='(') && (formula.charAt(formula.length()-1) == ')')) 
			formula = formula.substring(1,formula.length()-2); 
		
		String[]tokens = formula.split(",|\\s|\\(|\\)|\\[|\\]|&|\\||\\=\\>|\\<\\=\\>|~|\\<\\=]");
		List<String> possibleTypes = new LinkedList<String>();
		for (int i = 0; i < tokens.length; i++) {
			//In TPTP syntax, predicates and functors start with lowercase and contain alphanumerics
			//or are in 'single quotes'.
			if(tokens[i].matches("[a-z](\\w)*") || tokens[i].matches("'([^' ]+)'")) 
				possibleTypes.add(tokens[i]);
		}
		
		//Decide whether the found types are predicates or functors
		for (String pt : possibleTypes) {
			//Check if type has arguments
			if (formula.matches(".*(" + pt + "\\().*")) { 
				//Count number of arguments
				int arity = 1;
				int additionalOpenParentheses = 0;
				for (int i = formula.indexOf(pt)+pt.length()+1; i < formula.length(); i++) {
					if (formula.charAt(i) == '(')
						additionalOpenParentheses++;
					else if (formula.charAt(i) == ')') {
						if (additionalOpenParentheses>0) 
							additionalOpenParentheses--;
						else
							break; 
					}
					else if (formula.charAt(i) == ',')
						arity++;
				}

				if (formula.matches(".*([a-z](\\w)*\\(([^\\)]*,)*" + pt + ").*"))
					this.signature.add(new Functor(pt,arity));
				else 
					this.signature.add(new Predicate(pt,arity)); 
			}
			
			else {	
				if (formula.matches(".*([a-z](\\w|')*\\(([^\\)]*,)*" + pt + ").*"))
					this.signature.add(new Constant(pt));
				else
					this.signature.add(new Predicate(pt));
				}
		}
	}

	/**
	 * This method reads one token from the given reader and appropriately
	 * constructs a fol formula from the stream.
	 * @param stack a stack used for monitoring the read items.
	 * @param c a token from a stream.
	 * @throws ParserException in case of parser errors.
	 */
	private void consumeToken(Stack<Object> stack, int c) {
		try{
			String s = Character.toString((char) c);
			if(s.equals(")")){
				if(!stack.contains("("))
					throw new ParserException("Missing opening parentheses.");				
				List<Object> l = new ArrayList<Object>();
				for(Object o = stack.pop(); !((o instanceof String) && ((String)o).equals("(")); o = stack.pop() )
					l.add(0, o);
				// if the preceding token is in {a,...,z,A,...,Z,0,...,9,_,'} then treat the 
				// list as a term list, otherwise treat it as a quantification
				if(stack.size()>0 && stack.lastElement() instanceof String && ((String)stack.lastElement()).matches("[\\w']"))
					stack.push(this.parseTermlist(l));
				else stack.push(this.parseQuantification(l));
			} 
			else if (stack.size() >=1 && stack.lastElement().equals("=")) {
				if (s.equals(">")) {
					if (stack.size() >=2 && stack.get(stack.size()-2).equals("<")) {
						stack.pop();
						stack.pop();
						stack.push("<=>");
					}
					else {
						stack.pop();
						stack.push("=>"); } 
				}
				else if (stack.size() >=2 && stack.get(stack.size()-2).equals("<")) {
					stack.pop();
					stack.pop();
					stack.push("<="); }
				else stack.push(s);
			}
			else if (s.equals("=") && stack.size() >= 1 && stack.lastElement().equals("!")) {
				stack.pop();
				stack.push("!="); 
			}
			else if(s.equals("[") | s.equals("]") | s.equals(" ")){
			}
			else stack.push(s);
		} catch(Exception e){
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
	private List<Term<?>> parseTermlist(List<Object> l) {
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
						if(!signature.containsConstant(current)) throw new ParserException("Constant '" + current + "' has not been declared.");
						else terms.add(this.signature.getConstant(current));
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
				if(!signature.containsConstant(current)) throw new ParserException("Constant '" + current + "' has not been declared.");
				else terms.add(this.signature.getConstant(current));
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
	private FolFormula parseQuantification(List<Object> l) {
		if(l.isEmpty())
			throw new ParserException("Empty parentheses.");
		if(!(l.contains("?") || l.contains("!"))) 
			return this.parseEquivalence(l); 
		
		//If the quantification is not the first conjunct/disjunct/subformula of
		//the formula, split list at position of first non-quantor operator
		if (!(l.get(0).equals("?")||l.get(0).equals("!"))) {
			int i1 = l.indexOf("&");
			int i2 = l.indexOf("|");
			int i3 = l.indexOf("<=>");
			int i4 = l.indexOf("=>");
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
		
		List<Variable> bVars = new ArrayList<Variable>();
		String[] variables = var.split(",");
		for(Variable v: formula.getUnboundVariables()){
			for (String sv : variables) {
				if(v.get().equals(sv)){
					bVars.add(v);
				} 
			}
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
		
		for (String sv : variables) 
			this.variables.remove(sv);
		
		FolFormula result;
		if (l.get(0).equals("?")) 
			result = new ExistsQuantifiedFormula(formula,vars);
		else 
			result = new ForallQuantifiedFormula(formula,vars);
		
		//Add additional conjuncts/disjuncts to the right of the quantification (if applicable)
		if (l.size() > 2+j) {
			if (l.get(2+j).equals("&")) 
				return new Conjunction(result, parseQuantification(new ArrayList<Object>(l.subList((3+j), l.size()))));
			else if (l.get(2+j).equals("|")) 
				return new Disjunction(result, parseQuantification(new ArrayList<Object>(l.subList(3+j, l.size()))));
			else if (l.get(2+j).equals("<=>")) 
				return new Equivalence(result, parseQuantification(new ArrayList<Object>(l.subList(3+j, l.size()))));
			else if (l.get(2+j).equals("=>"))
				return new Implication(result, parseQuantification(new ArrayList<Object>(l.subList(3+j, l.size()))));
			else 
				throw new ParserException("Unrecognized symbol " + l.get(2+j));
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
		if(!(l.contains("<=>")))
			return this.parseImplication(l);	
		
		List<Object> left = new ArrayList<Object>(); 
		List<Object> right = new ArrayList<Object>(); 
		boolean isRightFormula = false;
		for(Object o: l){
			if((o instanceof String) && ((String)o).equals("<=>") )
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
	 * Reverse implications like 'A <= B' will be parsed as 'B => A'.
	 * @param l a list objects, either String tokens or objects of type FolFormula.
	 * @return a FolFormula.
	 * @throws ParserException if the list could not be parsed.
	 */
	private FolFormula parseImplication(List<Object> l) {
		if(l.isEmpty())
			throw new ParserException("Empty parentheses.");
		boolean isReverseImplication = false;
		if(l.contains("<="))
			isReverseImplication = true;
		else if(!(l.contains("=>")))
			return this.parseDisjunction(l);	
	
		List<Object> left = new ArrayList<Object>(); 
		List<Object> right = new ArrayList<Object>(); 
		boolean isRightFormula = false;
		for(Object o: l){
			if((o instanceof String) && ((String)o).equals("=>") | ((String)o).equals("<=") )
				isRightFormula = true;
			else if (isRightFormula) 
				right.add(o);
			else
				left.add(o);
		}	
		if (isReverseImplication)
			return new Implication(parseQuantification(right),parseQuantification(left));	
		return new Implication(parseQuantification(left),parseQuantification(right));	
	
	}
	
	/**
	 * Parses a disjunction as a list of String tokens or formulas into a fol formula.
	 * @param l a list objects, either String tokens or objects of type FolFormula.
	 * @return a FolFormula.
	 * @throws ParserException if the list could not be parsed.
	 */
	private FolFormula parseDisjunction(List<Object> l) {
		if(l.isEmpty())
			throw new ParserException("Empty parentheses.");
		if(!(l.contains("|")))
			return this.parseConjunction(l);		
		Disjunction d = new Disjunction();
		List<Object> tmp = new ArrayList<Object>(); 
		for(Object o: l){
			if((o instanceof String) && ((String)o).equals("|") ){
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
	 * Parses a conjunction as a list of String tokens or formulas into a fol formula.
	 * @param l a list objects, either String tokens or objects of type FolFormula.
	 * @return a FolFormula.
	 * @throws ParserException if the list could not be parsed.
	 */
	private FolFormula parseConjunction(List<Object> l) {
		if(l.isEmpty())
			throw new ParserException("General parsing exception.");
		if(!(l.contains("&")))
			return this.parseNegation(l);		
		Conjunction c = new Conjunction();
		List<Object> tmp = new ArrayList<Object>(); 
		for(Object o: l){
			if((o instanceof String) && ((String)o).equals("&") ){
				c.add(this.parseNegation(tmp));
				tmp = new ArrayList<Object>();
			}else tmp.add(o);
		}		
		c.add(this.parseNegation(tmp));
		if(c.size() > 1)	
			return c;
		throw new ParserException("General parsing exception.");
	}
	
	private FolFormula parseNegation(List<Object> l) {
		if(l.get(0).equals("~")){
			l.remove(0);
			return new Negation(this.parseNegation(l));			
		}
		return this.parseAtomic(l);	
	}
	
	@SuppressWarnings("unchecked")
	private FolFormula parseAtomic(List<Object> l) {
		if(l.size() == 1){
			Object o = l.get(0);
			if(o instanceof FolFormula) return (FolFormula) o;
			if(o instanceof String){
				String s = (String) o;
				if(this.signature.containsPredicate( s )) {
				  Predicate p = this.signature.getPredicate( s );
				  if(p.getArity() > 0)
				    throw new IllegalArgumentException("Predicate '" + p + "' has arity '" + p.getArity() + "'.");
				  return new FOLAtom(p);
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
			if ((s.contains("=") || s.contains("!=")) 
					&& !(s.substring(0,2).equals(("=")) || s.substring(0,3).equals(("!=")) )) {
				String op = "!=";
				if (s.indexOf("!=") == -1) 
					op = "="; 

				List<String> parts = new ArrayList<String>(Arrays.asList(s.split(op + "|\\s")));
				parts.removeAll(Arrays.asList("", " ", null));
				List<Object> newterms = new LinkedList<Object>();
				for (int i = 0; i < parts.get(0).length(); i++)  
				    newterms.add(String.valueOf(parts.get(0).charAt(i)));
				newterms.add(",");
				for (int i = 0; i < parts.get(1).length(); i++)  
					 newterms.add(String.valueOf(parts.get(1).charAt(i)));
				terms = this.parseTermlist(newterms);
				
				if (op.equals("="))
					s = LogicalSymbols.EQUALITY(); 
				else
					s = LogicalSymbols.INEQUALITY();
			}
			
			if(s.equals("$false"))
				return new Contradiction();
			if(s.equals("$true"))
				return new Tautology();	
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
							if(!sortOfVariable.equals(p.getArgumentTypes().get(i)) && !sortOfVariable.equals(Sort.ANY) && !p.getArgumentTypes().get(i).equals(Sort.ANY)) 
								throw new ParserException("Variable '" + t + "," + t.getSort() + "' has wrong sort."); 
							args.add(this.variables.get(((Variable)t).get()));
						}else{
							Variable v = new Variable(((Variable)t).get(),p.getArgumentTypes().get(i));
							args.add(v);
							this.variables.put(v.get(), v);
						}								
					}else if(!(t.getSort().equals(p.getArgumentTypes().get(i))) && !(t.getSort().equals(Sort.ANY)) && !(p.getArgumentTypes().get(i).equals(Sort.ANY))) 
						throw new ParserException("Term '" + t + "' has the wrong sort.");
					else args.add(t);
				}
				if (p.getName().equals("==")) 
					return new FOLAtom(new EqualityPredicate(),args); 
				else if (p.getName().equals("/==")) 
					return new FOLAtom(new InequalityPredicate(),args); 
				return new FOLAtom(p,args);
			}
			throw new ParserException("Predicate '" + s + "' has not been declared.");
		}	
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

	/**
	 * Set the regular expression that restricts which formulas will be parsed. Formulas with
	 * names that do not match the expression will not be parsed. 
	 */
	public void setFormulaNames(String formulaNames) {
		this.formulaNames = formulaNames;
	}

	/**
	 * Set the regular expression that restricts which formulas will be parsed. Formulas with
	 * roles that do not match the expression will not be parsed. 
	 */
	public void setFormulaRoles(String formulaRoles) {
		this.formulaRoles = formulaRoles;
	}
	
	/**
	 * Reset the regular expression that restricts which formulas will be parsed to the default value,
	 * meaning formulas of any TPTP roles will be parsed. Possible TPTP roles are
	 * axiom, hypothesis, definition, assumption, lemma, theorem, corollary, conjecture and negated_conjecture.
	 */
	public void resetFormulaRoles() {
		this.formulaRoles = "axiom|hypothesis|definition|assumption|lemma|theorem|corollary|conjecture|negated_conjecture";
	}
	
	/**
	 * Reset the regular expression that restricts which formulas will be parsed to the default value,
	 * meaning formulas of all names will be parsed.
	 */
	public void resetFormulaNames() {
		this.formulaNames = ".+";
	}
	
	
	/**
	 * Set the location of included files. 
	 * @param path that will be prepended to the paths of all included problem files.
	 */
	public void setIncludePath(String includePath) {
		this.includePath = includePath + "/";
	}

}
