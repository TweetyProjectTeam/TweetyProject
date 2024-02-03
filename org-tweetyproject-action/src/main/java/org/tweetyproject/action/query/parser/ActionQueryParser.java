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
package org.tweetyproject.action.query.parser;

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

import org.tweetyproject.action.grounding.GroundingRequirement;
import org.tweetyproject.action.grounding.parser.GroundingRequirementsParser;
import org.tweetyproject.action.query.syntax.AlwaysQuery;
import org.tweetyproject.action.query.syntax.HoldsQuery;
import org.tweetyproject.action.query.syntax.NecessarilyQuery;
import org.tweetyproject.action.query.syntax.QueryProposition;
import org.tweetyproject.action.query.syntax.SActionQuery;
import org.tweetyproject.action.query.syntax.SActionQuerySet;
import org.tweetyproject.action.signature.ActionSignature;
import org.tweetyproject.action.signature.FolAction;
import org.tweetyproject.action.signature.FolActionName;
import org.tweetyproject.commons.Parser;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.commons.LogicalSymbols;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.fol.parser.FolParser;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Contradiction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Tautology;

/**
 * This class implements a parser for action queries in S. The BNF of such queries is given by: (starting symbol is KB) <br>
 * KB               ::== QUERY1 ("\n" QUERY1)* <br>
 * QUERY1           ::== QUERY ( "requires" REQUIREMENTS )? <br>
 * QUERY            ::== PROPOSITION | QUERY "&amp;&amp;" QUERY | QUERY "||" QUERY | "!" QUERY | "(" QUERY ")" | "+" | "-" <br>
 * PROPOSITION      ::== HOLDSQUERY | ALWAYSQUERY | NECESSARILYQUERY <br>
 * HOLDSQUERY       ::== "holds" "[" STATEFORMULA "]" <br>
 * ALWAYSQUERY      ::== "always" "[" STATEFORMULA "]" <br>
 * NECESSARILYQUERY ::== "necessarily" "[" STATEFORMULA "]" "after" ACTIONS <br>
 * ACTIONS          ::== ACTION ( ";" ACTION )* <br>
 * ACTION           ::== "{" ACTIONNAME ("," ACTIONNAME)* "}" <br>
 * REQUIREMENTS     ::== REQUIREMENT ("," REQUIREMENT)* <br>
 * REQUIREMENT      ::== (VARIABLENAME "&lt;&gt;" VARIABLENAME | VARIABLENAME "&lt;&gt;" CONSTANTNAME)* <br>
 * <br>
 * where STATEFORMULA is an unquantified first-order formula without functors, <br>
 * and VARIABLENAME, CONSTANTNAME are sequences of symbols <br>
 * from {a,...,z,A,...,Z,0,...,9} with a letter at the beginning.
 * 
 * @author Sebastian Homann
 * @author Tim Janus (change constant LogicalSymbols to dynamic)
 */
public class ActionQueryParser
  extends Parser<SActionQuerySet,SActionQuery> {
  private ActionSignature signature;
  
  /**
   * constructor
 * @param signature 
 */
public ActionQueryParser(ActionSignature signature) {
    this.signature = signature;
  }
  
  /*
   * (non-Javadoc)
   * @see org.tweetyproject.Parser#parseBeliefBase(java.io.Reader)
   */
  @Override
  public SActionQuerySet parseBeliefBase( Reader reader ) {
    SActionQuerySet beliefSet = new SActionQuerySet();
    String s = "";
    // read from the reader and separate formulas by "\n"
    try{
      for(int c = reader.read(); c != -1; c = reader.read()){
        if(c == 10 || c == 13){
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
   * @see org.tweetyproject.Parser#parseFormula(java.io.Reader)
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
   * @param l a list of action names 
   * @return an Action.
   * @throws ParserException if parsing fails
   * @throws IOException if IO fails
   */
  private FolAction parseAction(List<Object> l) throws ParserException, IOException {
    if(l.get(0) instanceof String) {
      //Parse a list of action names into an action
      Set<FolAtom> actionNames = new HashSet<FolAtom>();
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

  private FolAtom parseActionName(String s) throws ParserException, IOException {
    FolParser p = new FolParser();
    p.setSignature( signature );
    FolFormula f = (FolFormula) p.parseFormula( s );
    if(! (f instanceof FolAtom)) throw new ParserException("Illegal type of action name.");
    FolAtom a = (FolAtom) f;
    if(!(a.getPredicate() instanceof FolActionName) ) throw new ParserException("Illegal signature of action name.");
    return a;
  }
  
  /**
   * Parses a simple formula as a list of string tokens or formulas into an action query.
   * This method expects a list of requirements at the end of the list.
   * @param stack a stack of objects, either String tokens, objects of type PropositionalFormula or a list of requirements
   * @return an action query
   * @throws ParserException if parsing fails
   */
  private SActionQuery parseActionFormula(Stack<Object> stack ) throws ParserException {
    if(stack.isEmpty())
      throw new ParserException("Empty action query.");
    if(!stack.contains( "requires" ))
      return new SActionQuery( this.parseDisjunction( stack ) );
    
    String requires = "";
    for(Object o = stack.pop(); !((o instanceof String) && ((String)o).equals("requires")); o = stack.pop() )
      requires = ((String)o) + requires;
    
    PlFormula disjunction = parseDisjunction( stack );
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
  private PlFormula parseDisjunction(List<Object> l) throws ParserException{
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
  private PlFormula parseConjunction(List<Object> l) throws ParserException{
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
  private PlFormula parseNegation(List<Object> l) throws ParserException{
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
   * @throws ParserException if parsing fails
   */
  private PlFormula parseAtomic(List<Object> l) throws ParserException{
    if(l.size() == 1){
      Object o = l.get(0);
      if(o instanceof PlFormula) return (PlFormula) o;
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
