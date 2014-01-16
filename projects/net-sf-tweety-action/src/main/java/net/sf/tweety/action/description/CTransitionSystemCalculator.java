package net.sf.tweety.action.description;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.ParserException;
import net.sf.tweety.action.description.syntax.DynamicLaw;
import net.sf.tweety.action.description.syntax.StaticLaw;
import net.sf.tweety.action.signature.ActionSignature;
import net.sf.tweety.action.signature.FolAction;
import net.sf.tweety.action.signature.FolActionName;
import net.sf.tweety.action.signature.FolFluentName;
import net.sf.tweety.action.transitionsystem.State;
import net.sf.tweety.action.transitionsystem.Transition;
import net.sf.tweety.action.transitionsystem.TransitionSystem;
import net.sf.tweety.lp.asp.solver.AspInterface;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Contradiction;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.Negation;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;
import net.sf.tweety.logics.fol.syntax.Tautology;

/**
 * This class calculates the transition system as it is described by an action
 * description in the action language C using extended logic programming with an
 * approach from: Representing Transition Systems by Logic Programs. by Vladimir
 * Lifschitz and Hudson Turner, LPNMR '99: Proceedings of the 5th International
 * Conference on Logic Programming and Nonmonotonic Reasoning. Pages 92-106,
 * 1999.
 * 
 * @author Sebastian Homann
 * @author Tim Janus (modifictions for asp library)
 */
public class CTransitionSystemCalculator
{
  private AspInterface aspsolver;
  
  /**
   * Creates a new transition system calculator with the given interface to an
   * answer set solver.
   * 
   * @param aspsolver
   */
  public CTransitionSystemCalculator( AspInterface aspsolver )
  {
    this.aspsolver = aspsolver;
  }
  
  /**
   * Calculates a transition system as described by the given action description
   * using all symbols in the given action signature.
   * 
   * @param actionDescription an action description.
   * @param signature an action signature.
   * @return a transition system.
   * @throws IOException
   * @throws IllegalArgumentException is thrown, when the given action
   *           description is not definite, as this method only works for
   *           definite action descriptions.
   */
  public TransitionSystem calculateTransitionSystem(
    CActionDescription actionDescription, ActionSignature signature )
    throws IOException
  {
    if ( !actionDescription.isDefinite() )
      throw new IllegalArgumentException(
        "Cannot calculate transition system of non-definite action description." );
    
    Set< State > states = calculateStates( actionDescription, signature );
    if ( states == null )
      return new TransitionSystem( signature );
    TransitionSystem transitionSystem =
      new TransitionSystem( states, signature );
    
    String rules = getLpT( actionDescription, signature, 1 );
    // TODO: Test the following code
    aspsolver.executeProgram(rules);
    String[] claspResult = (String [])aspsolver.getOutput().toArray();
    /*
    String[] claspResult = aspsolver.calculateAnswerSets( rules );
    */
    if ( claspResult == null )
      return transitionSystem;
    Set< Map< Integer, Set< FOLAtom >>> answerSets =
      parseLpT( claspResult, signature );
    
    for ( Map< Integer, Set< FOLAtom >> answerSet : answerSets ) {
      Set< FOLAtom > sourceStateFluents = new HashSet< FOLAtom >();
      Set< FOLAtom > targetStateFluents = new HashSet< FOLAtom >();
      Set< FOLAtom > actionNames = new HashSet< FOLAtom >();
      for ( FOLAtom a : answerSet.get( new Integer( 0 ) ) ) {
        if ( a.getPredicate() instanceof FolFluentName )
          sourceStateFluents.add( a );
        else if ( a.getPredicate() instanceof FolActionName )
          actionNames.add( a );
      }
      for ( FOLAtom a : answerSet.get( new Integer( 1 ) ) )
        targetStateFluents.add( a );
      
      State sourceState = transitionSystem.getState( sourceStateFluents );
      State targetState = transitionSystem.getState( targetStateFluents );
      FolAction action = new FolAction( actionNames );
      transitionSystem.addTransition( new Transition( sourceState, action,
        targetState ) );
    }
    
    return transitionSystem;
  }
  
  /**
   * calculates the set of all states of the transition system described by an
   * action description.
   * 
   * @param actionDescription an action description.
   * @param signature an action signature.
   * @return the set of all states of the transition system described by an
   *         action description.
   * @throws IOException
   * @throws IllegalArgumentException is thrown, when the given action
   *           description is not definite.
   */
  public Set< State > calculateStates( CActionDescription actionDescription,
    ActionSignature signature )
    throws IOException
  {
    if ( !actionDescription.isDefinite() )
      throw new IllegalArgumentException(
        "Cannot calculate transition system of non-definite action description." );
    Set< State > result = new HashSet< State >();
    String rules = getLpT( actionDescription, signature, 0 );
    
    // TODO: Test the following code
    aspsolver.executeProgram(rules);
    String[] claspResult = (String [])aspsolver.getOutput().toArray();
    /*
    String[] claspResult = aspsolver.calculateAnswerSets( rules );
    */
    if ( claspResult == null )
      return null;
    Set< Map< Integer, Set< FOLAtom >>> states = parseLpT( claspResult, signature );
    for ( Map< Integer, Set< FOLAtom >> state : states ) {
      State s = new State( state.get( new Integer( 0 ) ) );
      result.add( s );
    }
    
    return result;
  }
  
  /**
   * Calculates an extended logic programm lp_T(D) for a given action
   * description D and a parameter T, which corresponds to the length of
   * histories in the transition system described by D. See
   * "Representing Transition Systems by Logic Programs." for more information.
   * 
   * @param d an action description.
   * @param signature an action signature.
   * @param T length of histories of the transition system of D, that correspond
   *          to answer sets of lp_T(D).
   * @return an extended logic program.
   */
  public String getLpT( CActionDescription d, ActionSignature signature, int T )
  {
    if ( T < 0 )
      throw new IllegalArgumentException( "T has to be >= 0." );
    if ( !d.isDefinite() )
      throw new IllegalArgumentException(
        "Cannot calculate transition system of non-definite action description." );
    String result = "";
    Set< FOLAtom > groundedFluentAtoms = signature.getAllGroundedFluentAtoms();
    
    for ( StaticLaw r : d.getStaticLaws() ) {
      for ( int t = 0; t <= T; t++ ) {
        result += this.getLiteralString( r.getHeadFormula(), t, false );
        if ( !( r.getIfFormula() instanceof Tautology ) ) {
          result += " :- ";
          result += getRuleBodyString( r.getIfFormula(), t, true );
        }
        result += ".\n";
      }
    }
    // -B :- not B. B :- not -B. rules for fluentnames with t=0.
    result += getDefaultNegationRules( groundedFluentAtoms, 0 );
    
    if ( T > 0 ) {
      for ( DynamicLaw r : d.getDynamicLaws() ) {
        for ( int t = 0; t < T; t++ ) {
          result += this.getLiteralString( r.getHeadFormula(), t + 1, false );
          result += " :- ";
          if ( !( r.getIfFormula() instanceof Tautology ) ) {
            result += getRuleBodyString( r.getIfFormula(), t + 1, true );
            result += ",";
          }
          result += getRuleBodyString( r.getAfterFormula(), t, false );
          result += ".\n";
        }
      }
      // -B :- not B. B :- not -B. rules for all actionnames.
      Set< FOLAtom > groundedActionNames =
        signature.getAllGroundedActionNameAtoms();
      for ( int t = 0; t < T; t++ ) {
        result += getDefaultNegationRules( groundedActionNames, t );
      }
      // :- not B, not -B. constraints for all fluent names with t>0. removes incomplete answer sets.
      for ( int t = 1; t <= T; t++ ) {
        result += getCompletenessEnforcementRules( groundedFluentAtoms, t );
      }
    }
    
    return result;
  }
  
  /**
   * Returns rules of an extended logic program for the given set of atoms and a
   * parameter t. For each atom a, the result contains the rules
   *   -a(t) :- not  a(t).
   *    a(t) :- not -a(t).
   *
   * @param atoms set of atoms.
   * @param t parameter t.
   * @return rules.
   */
  private String getDefaultNegationRules( Set< FOLAtom > atoms, int t )
  {
    String rules = "";
    for ( FOLAtom a : atoms ) {
      String atomName = getAtomString( a, t );
      rules += "-" + atomName + " :- not " + atomName + ".\n";
      rules += atomName + " :- not -" + atomName + ".\n";
    }
    return rules;
  }
  
  /**
   * Returns rules, that enforce the existence of each atom in the given set in
   * all answer sets of an extended logic program.
   * 
   * @param atoms a set of atoms.
   * @param t parameter to be added to each atom.
   * @return rules of an extended logic program.
   */
  private String getCompletenessEnforcementRules( Set< FOLAtom > atoms, int t )
  {
    String rules = "";
    for ( FOLAtom a : atoms ) {
      String atomName = getAtomString( a, t );
      rules += ":- not " + atomName + ", not -" + atomName + ".\n";
    }
    return rules;
  }
  
  /**
   * Removes illegal characters from atom names, which are not parsable by an
   * lparse compatible answer set solver.
   * 
   * @param s an atom name.
   * @return the same atom name with removed illegal characters.
   */
  private String removeIllegalChars( String s )
  {
    return s.replace( "(", "xxx1xxx" ).replace( ")", "xxx2xxx" ).replace( ",", "xxx3xxx" );
  }
  
  /**
   * Regains all illegal characters from answer sets. This regains the original
   * names for each literal in an answer set.
   * 
   * @param s an atom name in lparse-compatible format.
   * @return the original atom name.
   */
  private String regainIllegalChars( String s )
  {
    return s.replace( "xxx1xxx", "(" ).replace( "xxx2xxx", ")" ).replace( "xxx3xxx", "," );
  }
  
  /**
   * Parses the resulting answer sets of an lp_T(D) program as a set. Each
   * answer set is parsed as a map from integers to sets of atoms, where the
   * integer represents the timestamp for each atom in the corresponding set.
   * 
   * @param lines the output of the answer set solver.
   * @param signature the action signature of the original description.
   * @return a set of maps each mapping timestamps to sets of atoms.
   */
  private Set< Map< Integer, Set< FOLAtom >>> parseLpT( String[] lines,
    ActionSignature signature )
  {
    Set< Map< Integer, Set< FOLAtom >>> result =
      new HashSet< Map< Integer, Set< FOLAtom >>>();
    
    for ( String line : lines ) {
      result.add( parseLpTSingleLine( line, signature ) );
    }
    return result;
  }
  
  /**
   * Utility function parsing a single answer set to a map from timestamp to the
   * set of atoms with that particular timestamp.
   * 
   * @param s a single answer set from the output of a solver.
   * @param signature the action signature of the original action description.
   * @return a map from timestamps to sets of atoms.
   * @throws ParserException
   */
  private Map< Integer, Set< FOLAtom >> parseLpTSingleLine( String s,
    ActionSignature signature )
    throws ParserException
  {
    Map< Integer, Set< FOLAtom >> map = new HashMap< Integer, Set< FOLAtom >>();
    
    String[] tokens = s.split( " " );
    for ( String token : tokens ) {
      token = token.trim();
      int i =
        Integer.parseInt( token.substring( token.indexOf( "(" ) + 1, token
          .indexOf( ")" ) ) );
      if ( map.get( new Integer( i ) ) == null )
        map.put( new Integer( i ), new HashSet< FOLAtom >() );
      if ( !token.startsWith( "-" ) ) {
        FolParser p = new FolParser();
        p.setSignature( signature );
        FOLAtom a = null;
        try {
          a =
            (FOLAtom) p.parseFormula( regainIllegalChars( token.substring( 0,
              token.indexOf( "(" ) ) ) );
        }
        catch ( IOException e ) {
          // Impossible, no io involved
          e.printStackTrace();
        }
        map.get( i ).add( a );
      }
    }
    return map;
  }
  
  /**
   * Calculates the representation of an inner formula of a causal rule
   * according to the translation in
   * "Representing Transition Systems by Logic Programs.". Since the "if"-part
   * of a rule has to be represented by default negation and the "after"-part by
   * a direct translation, this function allows both kinds of translations which
   * may be selected by the boolean parameter "negated".
   * 
   * @param f the inner formula of a causal rule.
   * @param t the timestamp for this translation.
   * @param negated true iff the formula should be represented using default
   *          negation.
   * @return a part of a logic rule representing the given formula.
   */
  private String getRuleBodyString( FolFormula f, int t, boolean negated )
  {
    String result = "";
    if ( f instanceof Conjunction ) {
      Conjunction c = (Conjunction) f;
      for ( RelationalFormula literal : c ) {
        if ( !result.equals( "" ) )
          result += ",";
        result += getLiteralString( (FolFormula) literal, t, negated );
      }
    }
    else {
      result = getLiteralString( f, t, negated );
    }
    return result;
  }
  
  /**
   * Utility function representing a single literal either in a direct manner or
   * using default negation.
   * 
   * @param f the literal to be represented.
   * @param t a timestamp.
   * @param negated true, iff the literal should be represented using default
   *          negation.
   * @return a representation of the given literal.
   */
  private String getLiteralString( FolFormula f, int t, boolean negated )
  {
    String result = negated ? "not " : "";
    if ( f instanceof Negation ) {
      result += negated ? "" : "-";
      result += getAtomString( ( (Negation) f ).getFormula(), t );
    }
    else if ( f instanceof FOLAtom ) {
      result += negated ? "-" : "";
      result += getAtomString( f, t );
    }
    else if ( f instanceof Tautology ) {
      result = "";
    }
    else if ( f instanceof Contradiction ) {
      result = "";
    }
    else {
      throw new IllegalArgumentException( "Not a valid Literal." );
    }
    
    return result;
  }
  
  /**
   * Utility function representing a given atom, either an actionname or a
   * fluentname, to be used in a rule in an extended logic program.
   * 
   * @param f an atom.
   * @param t a timestamp.
   * @return the string representation of the atom.
   */
  private String getAtomString( RelationalFormula f, int t )
  {
    if ( !( f instanceof FOLAtom ) )
      throw new IllegalArgumentException(
        "Cannot calculate transition system. Causal rule is not definite." );
    return removeIllegalChars( f.toString() ) + "(" + Integer.toString( t ) +
      ")";
  }
}
