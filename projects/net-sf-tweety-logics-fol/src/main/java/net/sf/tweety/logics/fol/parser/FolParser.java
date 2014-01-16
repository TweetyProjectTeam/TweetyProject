package net.sf.tweety.logics.fol.parser;

import java.io.*;
import java.util.*;

import net.sf.tweety.*;
import net.sf.tweety.logics.commons.LogicalSymbols;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.FunctionalTerm;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.*;
import net.sf.tweety.logics.fol.syntax.*;



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
 * <br>					 "(" FORMULA ")" | FORMULA "&&" FORMULA | FORMULA "||" FORMULA | "!" FORMULA | "+" | "-"
 * <br> ATOM		::== PREDICATENAME ("(" TERM ("," TERM)* ")")?
 * <br> TERM		::== VARIABLENAME | CONSTANTNAME | FUNCTORNAME "(" (TERM ("," TERM)*)?  ")" 
 * <br> 
 * <br> where SORTNAME, PREDICATENAME, CONSTANTNAME, VARIABLENAME, and FUNCTORNAME are sequences of
 * <br> symbols from {a,...,z,A,...,Z,0,...,9} with a letter at the beginning.
 * 
 * @author Matthias Thimm
 */
public class FolParser extends Parser<FolBeliefSet> {

	/**
	 * forall-quantifier used in syntax 
	 */
	public static final String FORALL_QUANTIFIER = "forall";
	
	/**
	 * exists-quantifier used in syntax
	 */
	public static final String EXISTS_QUANTIFIER = "exists";
	
	/**
	 * Keeps track of the signature.
	 */
	private FolSignature signature = new FolSignature();
	
	/**
	 * Keeps track of variables defined.
	 */
	private Map<String,Variable> variables;
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Parser#parseBeliefBase(java.io.Reader)
	 */
	@Override
	public FolBeliefSet parseBeliefBase(Reader reader) throws IOException, ParserException {
		FolBeliefSet beliefSet = new FolBeliefSet();
		String s = "";
		// for keeping track of the section of the file
		// 0 means sorts declaration
		// 1 means functor/predicate declaration
		// 2 means formula section
		int section = 0; 
		// read from the reader and separate formulas by "\n"
		try{
			for(int c = reader.read(); c != -1; c = reader.read()){
				if(c == 10){
					s = s.trim();
					if(!s.equals("")){
						if(s.startsWith("type")) section = 1;
						else if(section == 1) section = 2;
						
						if(section == 2)
							beliefSet.add((FolFormula)this.parseFormula(new StringReader(s)));
						else if(section == 1)
							this.parseTypeDeclaration(s,this.signature);
						else this.parseSortDeclaration(s,this.signature);
					}
					s = "";
				}else{
					s += (char) c;
				}
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
	 */
	public void parseSortDeclaration(String s, FolSignature sig) throws ParserException{
		if(!s.contains("=")) throw new ParserException("Missing '=' in sort declaration '" + s + "'.");
		String sortname = s.substring(0, s.indexOf("=")).trim();
		if(sig.containsSort(sortname)) throw new ParserException("Multiple declarations of sort '" + sortname + "'.");
		Sort theSort = new Sort(sortname);
		sig.add(theSort);
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
			if(c.matches("[a-z,A-Z]([a-z,A-Z,0-9])*"))
				sig.add(new Constant(c, theSort));
			else throw new ParserException("Illegal characters in constant definition '" + c + "'; declartion must conform to [a-z,A-Z]([a-z,A-Z,0-9])*");
		}		
	}
	
	/**
	 * Parses a predicate declaration of the form "type" "(" PREDICATENAME "(" (SORTNAME ("," SORTNAME)*)? ")" ")"
	 * or a functor declaration of the form "type" "(" SORTNAME "=" FUNCTORNAME "(" (SORTNAME ("," SORTNAME)*)? ")" ")"
	 * and modifies the given signature accordingly.
	 * @param s a string
	 * @param sig a signature
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
      sig.add( new Predicate(name) );
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
		if(targetSort == null)
			sig.add(new Predicate(name,theSorts));
		else{
			name = name.substring(name.indexOf("=")+1,name.length());
			sig.add(new Functor(name,theSorts,targetSort));
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Parser#parseFormula(java.io.Reader)
	 */
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
				// check if previously a "forall" or "exists" has been read
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
				// if the preceding token is in {a,...,z,A,...,Z,0,...,9} then treat the 
				// list as a term list, otherwise treat it as a quantification
				if(stack.size()>0 && stack.lastElement() instanceof String && ((String)stack.lastElement()).matches("[a-z,A-Z,0-9]"))
					stack.push(this.parseTermlist(l));
				else stack.push(this.parseQuantification(l));
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
			}else stack.push(s);
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
	private FolFormula parseQuantification(List<Object> l) throws ParserException{
		if(l.isEmpty())
			throw new ParserException("Empty parentheses.");
		if(!(l.contains(":")))
			return this.parseDisjunction(l);	
		if(!l.get(0).equals(EXISTS_QUANTIFIER) && !l.get(0).equals(FORALL_QUANTIFIER))
			throw new ParserException("Unrecognized quantifier '" + l.get(0) + "'.");
		String var = "";
		int idx = 1;
		while(!l.get(idx).equals(":")){
			var += (String) l.get(idx);
			idx++;
		}
		if(!(l.get(idx+1) instanceof FolFormula))
			throw new ParserException("Unrecognized formula type '" + l.get(idx+1) + "'.");
		FolFormula formula = (FolFormula) l.get(idx+1);
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
		if(l.get(0).equals(EXISTS_QUANTIFIER))
			return new ExistsQuantifiedFormula(formula,vars);
		else return new ForallQuantifiedFormula(formula,vars);		
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
			return new Negation(this.parseAtomic(l));			
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
}
