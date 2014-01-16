package net.sf.tweety.action.query.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import net.sf.tweety.Parser;
import net.sf.tweety.ParserException;
import net.sf.tweety.action.grounding.GroundingRequirement;
import net.sf.tweety.action.grounding.parser.GroundingRequirementsParser;
import net.sf.tweety.action.query.SActionQuerySet;
import net.sf.tweety.action.query.syntax.AlwaysQuery;
import net.sf.tweety.action.query.syntax.HoldsQuery;
import net.sf.tweety.action.query.syntax.NecessarilyQuery;
import net.sf.tweety.action.query.syntax.QueryProposition;
import net.sf.tweety.action.query.syntax.SActionQuery;
import net.sf.tweety.action.signature.ActionSignature;
import net.sf.tweety.action.signature.FolAction;
import net.sf.tweety.action.signature.FolActionName;
import net.sf.tweety.logics.commons.LogicalSymbols;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Contradiction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.Tautology;

/**
 * This class implements a parser for action queries in S. The BNF of such queries is given by: (starting symbol is KB) <br>
 * KB               ::== QUERY1 ("\n" QUERY1)* <br>
 * QUERY1           ::== QUERY ( "requires" REQUIREMENTS )? <br>
 * QUERY            ::== PROPOSITION | QUERY "&&" QUERY | QUERY "||" QUERY | "!" QUERY | "(" QUERY ")" | "+" | "-" <br>
 * PROPOSITION      ::== HOLDSQUERY | ALWAYSQUERY | NECESSARILYQUERY <br>
 * HOLDSQUERY       ::== "holds" "[" STATEFORMULA "]" <br>
 * ALWAYSQUERY      ::== "always" "[" STATEFORMULA "]" <br>
 * NECESSARILYQUERY ::== "necessarily" "[" STATEFORMULA "]" "after" ACTIONS <br>
 * ACTIONS          ::== ACTION ( ";" ACTION )* <br>
 * ACTION           ::== "{" ACTIONNAME ("," ACTIONNAME)* "}" <br>
 * REQUIREMENTS     ::== REQUIREMENT ("," REQUIREMENT)* <br>
 * REQUIREMENT      ::== (VARIABLENAME "<>" VARIABLENAME | VARIABLENAME "<>" CONSTANTNAME)* <br>
 * <br>
 * where STATEFORMULA is an unquantified first-order formula without functors, <br>
 * and VARIABLENAME, CONSTANTNAME are sequences of symbols <br>
 * from {a,...,z,A,...,Z,0,...,9} with a letter at the beginning.
 * 
 * @author Sebastian Homann
 * @author Tim Janus (change constant LogicalSymbols to dynamic)
 */
public class ActionQueryParser
  extends Parser<SActionQuerySet> {
  private ActionSignature signature;
  
  public ActionQueryParser(ActionSignature signature) {
    this.signature = signature;
  }
  
  /*
   * (non-Javadoc)
   * @see net.sf.tweety.Parser#parseBeliefBase(java.io.Reader)
   */
  @Override
  public SActionQuerySet parseBeliefBase( Reader reader ) {
    SActionQuerySet beliefSet = new SActionQuerySet();
    String s = "";
    // read from the reader and separate formulas by "\n"
    try{
      for(int c = reader.read(); c != -1; c = reader.read()){
        if(c == 10){
          if(!s.equals("") && !s.trim().startsWith( "%" )) 
            beliefSet.add(this.parseFormula(new StringReader(s)));
          s = "";
        }else{
          s += (char) c;
        }
      }   
      if(!s.equals("") && !s.trim().startsWith("%")) 
        beliefSet.add(this.parseFormula(new StringReader(s)));
    }catch(Exception e){
      throw new ParserException(e);
    }
    return beliefSet;
  }

  /*
   * (non-Javadoc)
   * @see net.sf.tweety.Parser#parseFormula(java.io.Reader)
   */
  @Override
  public SActionQuery parseFormula( Reader reader )
    throws IOException, ParserException {
    Stack<Object> stack = new Stack<Object>();
    try{
      for(int c = reader.read(); c != -1; c = reader.read())
        this.consumeToken(stack, c);
      return this.parseActionFormula( stack );
    }catch(Exception e){
      throw new ParserException(e);
    }
  }

  /**
   * This method reads one token from the given reader and appropriately
   * constructs a propositional formula from the stream.
   * @param stack a stack used for monitoring the read items.
   * @param c a token from a stream.
   * @throws ParserException in case of parser errors.
   */
  private void consumeToken(Stack<Object> stack, int c) throws ParserException{
    try{
      String s = Character.toString((char) c);
      if(s.equals(" ")){
        // do nothing
      }
      // check if previously a "holds", "always", "necessarily" or "requires" has been read
      else if(stack.size() >= 4 && 
              stack.get(stack.size()-4).equals("h") &&
              stack.get(stack.size()-3).equals("o") &&
              stack.get(stack.size()-2).equals("l") &&
              stack.get(stack.size()-1).equals("d") &&
              s.equals("s"))
        {
            stack.pop();
            stack.pop();
            stack.pop();
            stack.pop();
            stack.push("holds");
        } else if(stack.size() >= 4 && 
              stack.get(stack.size()-4).equals("a") &&
              stack.get(stack.size()-3).equals("f") &&
              stack.get(stack.size()-2).equals("t") &&
              stack.get(stack.size()-1).equals("e") &&
              s.equals("r"))
        {
            stack.pop();
            stack.pop();
            stack.pop();
            stack.pop();
            stack.push("after");
        } else if(stack.size() >= 5 && 
              stack.get(stack.size()-5).equals("a") &&
              stack.get(stack.size()-4).equals("l") &&
              stack.get(stack.size()-3).equals("w") &&
              stack.get(stack.size()-2).equals("a") &&
              stack.get(stack.size()-1).equals("y") &&
              s.equals("s"))
        {
            stack.pop(); stack.pop();
            stack.pop(); stack.pop();
            stack.pop();
            stack.push("always");
        } else if(stack.size() >= 7 && 
               stack.get(stack.size()-7).equals("r") &&
               stack.get(stack.size()-6).equals("e") &&
               stack.get(stack.size()-5).equals("q") &&
               stack.get(stack.size()-4).equals("u") &&
               stack.get(stack.size()-3).equals("i") &&
               stack.get(stack.size()-2).equals("r") &&
               stack.get(stack.size()-1).equals("e") &&
               s.equals("s"))
        {
            stack.pop(); stack.pop();
            stack.pop(); stack.pop();
            stack.pop(); stack.pop();
            stack.pop();
            stack.push("requires");      
        } else if(stack.size() >= 10 && 
          stack.get(stack.size()-10).equals("n") &&
           stack.get(stack.size()-9).equals("e") &&
           stack.get(stack.size()-8).equals("c") &&
           stack.get(stack.size()-7).equals("e") &&
           stack.get(stack.size()-6).equals("s") &&
           stack.get(stack.size()-5).equals("s") &&
           stack.get(stack.size()-4).equals("a") &&
           stack.get(stack.size()-3).equals("r") &&
           stack.get(stack.size()-2).equals("i") &&
           stack.get(stack.size()-1).equals("l") &&
           s.equals("y"))
        {
            stack.pop(); stack.pop();
            stack.pop(); stack.pop();
            stack.pop(); stack.pop();
            stack.pop(); stack.pop();
            stack.pop(); stack.pop();
            stack.push("necessarily");      
      } else if(stack.contains( "[" )) {
        if(s.equals( "[" )) throw new ParserException( "Unexpected '[' parentheses" );
        if(s.equals( "]" )) {
          String stateFormula = "";
          for(Object o = stack.pop(); !((o instanceof String) && ((String)o).equals("[")); o = stack.pop() )
            stateFormula = ((String)o) + stateFormula;
          FolParser p = new FolParser();
          p.setSignature( signature );
          stack.push( p.parseFormula( stateFormula ) );
        } else 
          stack.push( s );
      } else if(s.equals( "]" )) {
        throw new ParserException("Missing opening parenthesis '['.");
      } else if(stack.contains( "{" )) {
        if(s.equals( "}" )) {
          List<Object> l = new ArrayList<Object>();
          for(Object o = stack.pop(); !((o instanceof String) && ((String)o).equals( "{" )); o = stack.pop())
            l.add( 0, o );
          stack.push( this.parseAction(l) );
        } else 
          stack.push( s );
      } else if(s.equals( "}" )) {
        throw new ParserException("Missing opening parenthesis '{'.");
      } else if(s.equals( ";" ) || s.equals( "," )) {
        // do nothing
      } else if(s.equals(")")) {
        if(!stack.contains("("))
          throw new ParserException("Missing opening parentheses.");        
        List<Object> l = new ArrayList<Object>();
        for(Object o = stack.pop(); !((o instanceof String) && ((String)o).equals("(")); o = stack.pop() )
          l.add(0, o);          
        stack.push(this.parseDisjunction(l));
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
   * Parses a list of actionnames or a list of actions.
   * Actions are strings of the form ACTIONNAME ("," ACTIONNAME)*
   * into an action.
   * @param s String
   * @return an Action.
   * @throws ParserException
   * @throws IOException 
   */
  private FolAction parseAction(List<Object> l) throws ParserException, IOException {
    if(l.get(0) instanceof String) {
      //Parse a list of action names into an action
      Set<FOLAtom> actionNames = new HashSet<FOLAtom>();
      String tmp = "";
      for(Object o: l){
        if((o instanceof String) && ((String)o).equals(",") ){
          actionNames.add( parseActionName(tmp) );
          tmp = "";
        }else tmp += (String)o;
      }   
      actionNames.add( parseActionName(tmp) );
      return new FolAction( actionNames );
    } else throw new ParserException("Unexpected token in action string.");
  }

  private FOLAtom parseActionName(String s) throws ParserException, IOException {
    FolParser p = new FolParser();
    p.setSignature( signature );
    FolFormula f = (FolFormula) p.parseFormula( s );
    if(! (f instanceof FOLAtom)) throw new ParserException("Illegal type of action name.");
    FOLAtom a = (FOLAtom) f;
    if(!(a.getPredicate() instanceof FolActionName) ) throw new ParserException("Illegal signature of action name.");
    return a;
  }
  
  /**
   * Parses a simple formula as a list of string tokens or formulas into an action query.
   * This method expects a list of requirements at the end of the list.
   * @param l a list of objects, either String tokens, objects of type PropositionalFormula or a list of requirements
   * @throws ParserException
   */
  private SActionQuery parseActionFormula(Stack<Object> stack ) throws ParserException {
    if(stack.isEmpty())
      throw new ParserException("Empty action query.");
    if(!stack.contains( "requires" ))
      return new SActionQuery( this.parseDisjunction( stack ) );
    
    String requires = "";
    for(Object o = stack.pop(); !((o instanceof String) && ((String)o).equals("requires")); o = stack.pop() )
      requires = ((String)o) + requires;
    
    PropositionalFormula disjunction = parseDisjunction( stack );
    Set<Variable> variables = new HashSet<Variable>();
    for(Proposition p : disjunction.getAtoms()) {
      QueryProposition qp = (QueryProposition) p;
      variables.addAll( qp.getVariables() );
    }
    Set<GroundingRequirement> requirements = new GroundingRequirementsParser().parseRequirements( requires, variables );
    return new SActionQuery(disjunction, requirements);
  }

  /**
   * Parses a simple formula as a list of String tokens or formulas into a propositional formula.
   * This method expects no parentheses in the list and as such treats the formula as a disjunction.
   * @param l a list objects, either String tokens or objects of type PropositionalFormula.
   * @return a propositional formula.
   * @throws ParserException if the list could not be parsed.
   */
  private PropositionalFormula parseDisjunction(List<Object> l) throws ParserException{
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
   * Parses a simple formula as a list of String tokens or formulas into a propositional formula.
   * This method expects no parentheses and no disjunctions in the list and as such treats the formula as a conjunction.
   * @param l a list objects, either String tokens or objects of type PropositionalFormula.
   * @return a propositional formula.
   * @throws ParserException if the list could not be parsed.
   */
  private PropositionalFormula parseConjunction(List<Object> l) throws ParserException{
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
   * Parses a simple formula as a list of String tokens or formulas into a propositional formula.
   * This method expects no parentheses, no disjunctions, and no conjunctions in the list and as such treats the formula as a negation.
   * @param l a list objects, either String tokens or objects of type PropositionalFormula.
   * @return a propositional formula.
   * @throws ParserException if the list could not be parsed.
   */
  private PropositionalFormula parseNegation(List<Object> l) throws ParserException{
    if(l.get(0).equals(LogicalSymbols.CLASSICAL_NEGATION())){
      l.remove(0);
      return new Negation(this.parseAtomic(l));     
    }
    return this.parseAtomic(l);   
  }
  
  /**
   * Parses a simple formula as a list of String tokens or formulas into a propositional formula.
   * This method expects no parentheses, no disjunctions, no conjunctions, and no negation in the list
   * and as such treats the formula as an atomic construct, either a contradiction, a tautology, or a proposition.
   * @param l a list objects, either String tokens or objects of type PropositionalFormula.
   * @return a propositional formula.
   * @throws ParserException
   */
  private PropositionalFormula parseAtomic(List<Object> l) throws ParserException{
    if(l.size() == 1){
      Object o = l.get(0);
      if(o instanceof PropositionalFormula) return (PropositionalFormula) o;
      if(o instanceof String){
        String s = (String) o;
        if(s.equals(LogicalSymbols.CONTRADICTION()))
          return new Contradiction();
        if(s.equals(LogicalSymbols.TAUTOLOGY()))
          return new Tautology();
        throw new ParserException( "Unknown object: '"+s+"'." );
      }
      throw new ParserException("Unknown object " + o);
    }else{
      if(l.get( 0 ).equals( "holds" )) {
        if(l.get( 1 ) instanceof FolFormula)
          return new HoldsQuery( (FolFormula)l.get( 1 ) );
        else throw new ParserException( "Missing inner formula in 'holds' expression." );
      } else if(l.get(0).equals( "always" )) {
        if(l.get( 1 ) instanceof FolFormula)
          return new AlwaysQuery( (FolFormula)l.get( 1 ) );
        else throw new ParserException( "Missing inner formula in 'always' expression." );
      } else if(l.get( 0 ).equals( "necessarily" )) {
        if(!(l.get( 1 ) instanceof FolFormula))
          throw new ParserException("Missing inner formula in 'necessarily' expression.");
        if(l.size() < 4)
          return new NecessarilyQuery( (FolFormula)l.get( 1 ) );
        if(!l.get( 2 ).equals( "after" ))
          throw new ParserException( "Missing 'after' statement in 'necessarily' expression." );
        List<FolAction> actions = new LinkedList< FolAction >();
        Iterator< Object > iter = l.listIterator( 3 );
        while(iter.hasNext()) {
          Object o = iter.next();
          if(!(o instanceof FolAction))
            throw new ParserException( "Uexpected element in 'after' part of the 'necessarily' expression: '"+l+"'.");
          actions.add( (FolAction) o );
        }
        if(actions.isEmpty())
          throw new ParserException( "Empty action list in 'necessarily' expression: '"+l+"'." );
        return new NecessarilyQuery(  (FolFormula)l.get( 1 ), actions );
      }
      throw new ParserException("Could not parse atomic expression: '"+l.toString()+"'.");
    }   
  }

}
