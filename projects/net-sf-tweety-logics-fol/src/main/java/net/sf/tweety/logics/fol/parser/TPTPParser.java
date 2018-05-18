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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.fol.parser;

import java.io.IOException;
import java.io.Reader;
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
import net.sf.tweety.logics.commons.syntax.FunctionalTerm;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Contradiction;
import net.sf.tweety.logics.fol.syntax.Disjunction;
import net.sf.tweety.logics.fol.syntax.Equivalence;
import net.sf.tweety.logics.fol.syntax.ExistsQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.syntax.ForallQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.Implication;
import net.sf.tweety.logics.fol.syntax.Negation;
import net.sf.tweety.logics.fol.syntax.Tautology;

/**
 * This class implements a parser for the TPTP syntax. Parses single first-order
 * logic formulas and (TODO) knowledge bases.
 *
 * <p> Basic Operators:</p>
 * <ul>
 * <li> Negation: ~ formula					</li>
 * <li> Conjunction: formula & formula		</li>
 * <li> Disjunction: formula | formula		</li>
 * <li> Implication: formula => formula		</li>
 * <li> Equivalence: formula <=> formula	</li>
 * <li> Universal quantifier: ! [Variable1,Variable2,...] : formula	</li> (Currently only works if formula is in parentheses)
 * <li> Existential quantifier:	? [Variable1,Variable2,...] : formula	</li> (Currently only works if formula is in parentheses)
 * <li> Tautology: $true					</li>
 * <li> Contradiction: $false 				</li>
 * </ul>
 * 
 * @author Anna Gessler
 */
public class TPTPParser extends Parser<FolBeliefSet> {
	/**
	 * Keeps track of the signature.
	 */
	private FolSignature signature = new FolSignature();
	
	/**
	 * Keeps track of variables defined.
	 */
	private Map<String,Variable> variables;

	@Override
	public FolBeliefSet parseBeliefBase(Reader reader) throws IOException, ParserException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Formula parseFormula(Reader reader) throws IOException, ParserException {
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

	private void consumeToken(Stack<Object> stack, int c) {
		try{
			String s = Character.toString((char) c);
			if(s.equals(")")){
				if(!stack.contains("("))
					throw new ParserException("Missing opening parentheses.");				
				List<Object> l = new ArrayList<Object>();
				for(Object o = stack.pop(); !((o instanceof String) && ((String)o).equals("(")); o = stack.pop() )
					l.add(0, o);
				// if the preceding token is in {a,...,z,A,...,Z,0,...,9} then treat the 
				// list as a term list, otherwise treat it as a quantification
				if(stack.size()>0 && stack.lastElement() instanceof String && ((String)stack.lastElement()).matches("[a-z,A-Z,0-9]"))
					stack.push(this.parseTermlist(l));
				else stack.push(this.parseQuantification(l));
			} 
			else if(s.equals(">")){
				if(stack.lastElement().equals("=")){
					if (stack.size() >=2 && stack.get(stack.size()-2).equals("<")) {
						stack.pop();
						stack.pop();
						stack.push("<=>");
					}
					else {
					stack.pop();
					stack.push("=>"); } 	
				} else stack.push(s);
			}
			else if(s.equals("[") | s.equals("]") | s.equals(" ")){
			}
			else stack.push(s);
		} catch(Exception e){
			throw new ParserException(e);
		}		
		
	}

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
		
		Variable bVar = null;
		for(Variable v: formula.getUnboundVariables()){
			if(v.get().equals(var)){
				bVar = v;
				break;
			}
		}
		
		if(bVar == null)
			throw new ParserException("Variable '" + var + "' not found in quantification.");
		Set<Variable> vars = new HashSet<Variable>();
		vars.add(bVar);
		this.variables.remove(var);
		
		FolFormula result;
		if (l.get(0).equals("?")) 
			result = new ExistsQuantifiedFormula(formula,vars);
		else 
			result = new ForallQuantifiedFormula(formula,vars);
		
		//Add additional conjuncts/disjuncts to the right of the quantification (if applicable)
		if (l.size() > 4) {
			if (l.get(idx+2).equals("&")) 
				return new Conjunction(result, parseQuantification(new ArrayList<Object>(l.subList(idx+3, l.size()))));
			else if (l.get(idx+2).equals("|")) 
				return new Disjunction(result, parseQuantification(new ArrayList<Object>(l.subList(idx+3, l.size()))));
			else if (l.get(idx+2).equals("<=>")) 
				return new Equivalence(result, parseQuantification(new ArrayList<Object>(l.subList(idx+3, l.size()))));
			else if (l.get(idx+2).equals("=>"))
				return new Implication(result, parseQuantification(new ArrayList<Object>(l.subList(idx+3, l.size()))));
			else 
				throw new ParserException("Unrecognized symbol " + l.get(idx+2));
		}
		return result;	
	}
	
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
	
	private FolFormula parseImplication(List<Object> l) {
		if(l.isEmpty())
			throw new ParserException("Empty parentheses.");
		if(!(l.contains("=>")))
			return this.parseDisjunction(l);	
	
		List<Object> left = new ArrayList<Object>(); 
		List<Object> right = new ArrayList<Object>(); 
		boolean isRightFormula = false;
		for(Object o: l){
			if((o instanceof String) && ((String)o).equals("=>") )
				isRightFormula = true;
			else if (isRightFormula) 
				right.add(o);
			else
				left.add(o);
		}	
		return new Implication(parseQuantification(left),parseQuantification(right));	
	
	}
	
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
							if(!this.variables.get(((Variable)t).get()).getSort().equals(p.getArgumentTypes().get(i)))
								throw new ParserException("Variable '" + t + "' has wrong sort.");
							args.add(this.variables.get(((Variable)t).get()));
						}else{
							Variable v = new Variable(((Variable)t).get(),p.getArgumentTypes().get(i));
							args.add(v);
							this.variables.put(v.get(), v);
						}								
					}else if(!t.getSort().equals(p.getArgumentTypes().get(i)))
						throw new ParserException("Term '" + t + "' has the wrong sort.");
					else args.add(t);
				}
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

	public Map<String, Variable> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, Variable> variables) {
		this.variables = variables;
	}

}
