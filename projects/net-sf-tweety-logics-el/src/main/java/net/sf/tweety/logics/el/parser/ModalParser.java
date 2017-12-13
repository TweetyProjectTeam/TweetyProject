package net.sf.tweety.logics.el.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.LogicalSymbols;
import net.sf.tweety.logics.commons.syntax.FunctionalTerm;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.el.ModalBeliefSet;
import net.sf.tweety.logics.el.syntax.ModalFormula;
import net.sf.tweety.logics.el.syntax.Necessity;
import net.sf.tweety.logics.el.syntax.Possibility;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Contradiction;
import net.sf.tweety.logics.fol.syntax.Disjunction;
import net.sf.tweety.logics.fol.syntax.ExistsQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.syntax.ForallQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.Negation;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;
import net.sf.tweety.logics.fol.syntax.Tautology;

/**
 * This class implements a parser for modal logic. 
 * TODO: Does not support quantified modal formulas (QuantifiedFormulas can only range over first-order formulas).
 * 
 * The BNF for a modal knowledge base is given by (starting symbol is KB)
 * <br>
 * <br> KB 			::== SORTSDEC DECLAR FORMULAS
 * <br> DECLAR		::== (FUNCTORDEC | PREDDEC)*
 * <br> SORTSDEC    ::== ( SORTNAME "=" "{" (CONSTANTNAME ("," CONSTANTNAME)*)? "}" "\n" )*
 * <br> PREDDEC		::== "type" "(" PREDICATENAME ("(" SORTNAME ("," SORTNAME)* ")")? ")" "\n"
 * <br> FUNCTORDEC	::== "type" "(" SORTNAME "=" FUNCTORNAME "(" (SORTNAME ("," SORTNAME)*)? ")" ")" "\n"
 * <br> FORMULAS    ::== ( "\n" FORMULA)*
 * <br> FORMULA     ::== ATOM  | "forall" VARIABLENAME ":" "(" FORMULA ")" | "exists" VARIABLENAME ":" "(" FORMULA ")" |
 * <br> 				"(" FORMULA ")" | "[]" "(" FORMULA ")" | "<>" "(" FORMULA ")" |
 * <br>  				FORMULA "&&" FORMULA | FORMULA "||" FORMULA | "!" FORMULA | "+" | "-"
 * <br> ATOM		::== PREDICATENAME ("(" TERM ("," TERM)* ")")?
 * <br> TERM		::== VARIABLENAME | CONSTANTNAME | FUNCTORNAME "(" (TERM ("," TERM)*)?  ")" 
 * <br> 
 * <br> where SORTNAME, PREDICATENAME, CONSTANTNAME, VARIABLENAME, and FUNCTORNAME are sequences of
 * <br> symbols from {a,...,z,A,...,Z,0,...,9} with a letter at the beginning.
 * 
 * @author Matthias Thimm, Anna Gessler
 */

public class ModalParser extends Parser<ModalBeliefSet> {

	FolParser folparser; //First-order logic parser used for parsing sorts and type declaration 
	
	public ModalParser() {
		folparser = new FolParser();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Parser#parseBeliefBase(java.io.Reader)
	 */
	@Override
	public ModalBeliefSet parseBeliefBase(Reader reader) throws IOException, ParserException {
		ModalBeliefSet beliefSet = new ModalBeliefSet();
		String s = "";
		// For keeping track of the section of the file:
		// 0 means sorts declaration
		// 1 means type declaration, i.e. functor/predicate declaration
		// 2 means formula section
		int section = 0; 
		
		try{
			for(int c = reader.read(); c != -1; c = reader.read()){
				if(c == 10){ // read from the reader and separate formulas by "\n" (ascii code 10)
					s = s.trim();
					if(!s.equals("")){
						if(s.startsWith("type")) section = 1;
						else if(section == 1) section = 2; //A type declaration section has been parsed previously, 
														   //therefore only the formula section remains.
						if(section == 2)
							beliefSet.add((ModalFormula)this.parseFormula(new StringReader(s)));
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
	public Formula parseFormula(Reader reader) throws IOException, ParserException {
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
			//System.out.println(c + "," + s);
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
			else if (s.equals(")")){
				if (!stack.contains("(")) 
					throw new ParserException("Missing opening parentheses.");
				//add contents of parentheses to list
				List<Object> l = new ArrayList<Object>();
				for(Object o = stack.pop(); !((o instanceof String) && ((String)o).equals("(")); o = stack.pop() ) 
					l.add(0, o); 
				// If the preceding token is in {a,...,z,A,...,Z,0,...,9} then treat the 
				// list as a term list.
				// If the token is ] or >, treat the list as a modal formula.
				// Otherwise treat it as a quantification.
				if(stack.size()>0 && stack.lastElement() instanceof String && ((String)stack.lastElement()).matches("[a-z,A-Z,0-9]"))
					stack.push(this.parseTermlist(l));
				else if (stack.size()>0 && stack.lastElement() instanceof String && ((String)stack.lastElement()).matches("]|>")) 
					stack.push(this.parseModalization(l)); 
				else  stack.push(this.parseQuantification(l)); 
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
			//Same for [] and <>
			}else if(s.equals("]")){
				if(stack.lastElement().equals("[")){
					stack.pop();
					stack.push("[]");
				}else stack.push(s);
			}else if(s.equals(">")){
				if(stack.lastElement().equals("<")){
					stack.pop();
					stack.push("<>");
				}else stack.push(s);
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

	private RelationalFormula parseModalization(List<Object> l) throws ParserException {
		if(l.isEmpty()) {
			throw new ParserException("Empty parentheses."); }
		if(! ( l.contains("[]")||l.contains("<>") ) ) 
			return this.parseDisjunction(l); 

		if(!(l.get(1) instanceof RelationalFormula)) {
			throw new ParserException("Unrecognized formula type '" + l.get(1) + "'."); }
		RelationalFormula formula = (RelationalFormula) l.get(1);
		
		if(l.get(0).equals("[]")) {
			return new Necessity(formula); }
		else return new Possibility(formula);	
	}
	
	// TODO Problem: QuantifiedFormulas can only range over FolFormulas
	private RelationalFormula parseQuantification(List<Object> l) {
		if(l.isEmpty())
			throw new ParserException("Empty parentheses.");
		if (l.contains(":")) {
				throw new ParserException("Quantified modal formulas are not currently supported by this parser (TODO)"); }
		return this.parseModalization(l);
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
	 * @throws ParserException
	 */
	@SuppressWarnings("unchecked")
	private RelationalFormula parseAtomic(List<Object> l) throws ParserException{
		if(l.size() == 1){
			Object o = l.get(0);
			if(o instanceof FolFormula) return (FolFormula) o;
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
			if(folparser.getSignature().containsPredicate(s)){
			  // check for zero-arity predicate
			  if(terms == null) 
			    terms = new LinkedList<Term<?>>(); 
				// check correct sorts of terms
				Predicate p = folparser.getSignature().getPredicate(s);
				List<Term<?>> args = new ArrayList<Term<?>>();
				if(p.getArity() != terms.size())
					throw new IllegalArgumentException("Predicate '" + p + "' has arity '" + p.getArity() + "'.");
				for(int i = 0; i < p.getArity(); i++){
					Term<?> t = terms.get(i);
					if(t instanceof Variable){
						if(folparser.getVariables().containsKey(((Variable)t).get())){
							if(!folparser.getVariables().get(((Variable)t).get()).getSort().equals(p.getArgumentTypes().get(i)))
								throw new ParserException("Variable '" + t + "' has wrong sort.");
							args.add(folparser.getVariables().get(((Variable)t).get()));
						}else{
							Variable v = new Variable(((Variable)t).get(),p.getArgumentTypes().get(i));
							args.add(v);
							Map<String, Variable> map = folparser.getVariables();
							map.put(v.get(), v);
							folparser.setVariables(map);
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
		this.folparser.setSignature(signature);
	}
	
}
