package net.sf.tweety.action.query;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.action.ActionQuery;
import net.sf.tweety.action.ActionQuerySatisfactionTester;
import net.sf.tweety.action.query.syntax.AlwaysQuery;
import net.sf.tweety.action.query.syntax.HoldsQuery;
import net.sf.tweety.action.query.syntax.NecessarilyQuery;
import net.sf.tweety.action.query.syntax.QueryProposition;
import net.sf.tweety.action.query.syntax.SActionQuery;
import net.sf.tweety.action.signature.FolAction;
import net.sf.tweety.action.transitionsystem.State;
import net.sf.tweety.action.transitionsystem.Transition;
import net.sf.tweety.action.transitionsystem.TransitionSystem;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.lp.asp.solver.AspInterface;
import net.sf.tweety.logics.fol.syntax.AssociativeFOLFormula;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Contradiction;
import net.sf.tweety.logics.fol.syntax.Disjunction;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.Negation;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * This class provides methods capable of checking if a given transition system
 * satisfies a set of action queries in the action query language s. This is
 * acomplished by a translation of action queries to normal logic programms
 * presented in [1]. 
 * [1] Bachelor thesis. Action und Change: Update von Aktionsbeschreibungen 
 *     by Sebastian Homann. TU Dortmund, 2010.
 * 
 * @author Sebastian Homann
 */
public class SActionQuerySatisfactionTester
  implements ActionQuerySatisfactionTester
{
  //private AspInterface aspsolver;
  
  /**
   * Creates a new instance of this satisfaction tester using the given answer
   * set solver.
   * 
   * @param aspsolver
   */
  public SActionQuerySatisfactionTester( AspInterface aspsolver )
  {
   // this.aspsolver = aspsolver;
  }
  
  /*
   * (non-Javadoc)
   * @see
   * net.sf.tweety.action.ActionQuerySatisfactionTester#isSatisfied(net.sf.tweety
   * .action.description.transitionsystem.TransitionSystem,
   * net.sf.tweety.BeliefBase)
   */
  public boolean isSatisfied( TransitionSystem transitionSystem,  BeliefBase actionQueries ) {
	 //TODO IMPLEMENT ME
	  if ( transitionSystem == null )
      return false;        
    String program = "";
    program += getTransitionSystemRules( transitionSystem );
    SActionQuerySet qset = (SActionQuerySet) actionQueries;
    program += getRules( qset );
    program += getConstraints( qset );
    
    // TODO: Add satisfiable term to new asp lib version:
    boolean result = false;
    /*
    try {
      result = aspsolver.calculateSatisfiable( program );
    }
    catch ( IOException e ) {
      e.printStackTrace();
    }
    catch ( AspException e ) {
      e.printStackTrace();
    }
    */
    System.out.println(program);
    return result;     
  }
  
  /*
   * (non-Javadoc)
   * @see
   * net.sf.tweety.action.ActionQuerySatisfactionTester#isSatisfied(net.sf.tweety
   * .action.description.transitionsystem.TransitionSystem, java.util.Set)
   */
  public boolean isSatisfied( TransitionSystem transitionSystem,
    Set< ActionQuery > actionQueries )
  {
    SActionQuerySet qset = new SActionQuerySet();
    for ( ActionQuery q : actionQueries ) {
      if ( !( q instanceof SActionQuery ) )
        return false;
      qset.add( (SActionQuery) q );
    }
    return isSatisfied( transitionSystem, qset );
  }
  
  /**
   * Returns the program C_q which contains a constraint for each query in
   * question. Each query is identified by an atom in the resulting program.
   * 
   * @param queries a set of queries for which C_q should be constructed.
   */
  private String getConstraints( Collection< ? extends SActionQuery > queries )
  {
    String result = "";
    for ( SActionQuery q : queries ) {
      result += ":- not ";
      result += removeIllegalCharacters( q.toString() );
      result += "(S), state(S).\n";
    }
    return result;
  }
  
  /**
   * Returns the basic translation of action query laws to rules in the logic
   * program.
   * 
   * @param queries the set of queries that should be translated.
   * @return a string containing a logic program representation of the given
   *         action queries.
   */
  private String getRules( Collection< SActionQuery > queries )
  {
    Set< FolFormula > stateParts = new HashSet< FolFormula >();
    Set< PropositionalFormula > queryParts =
      new HashSet< PropositionalFormula >();
    Set< QueryProposition > queryPropositions =
      new HashSet< QueryProposition >();
    for ( SActionQuery q : queries ) {
      queryPropositions.addAll( getQueryPropositions( q ) );
      queryParts.addAll( getQueryParts( q.getFormula() ) );
    }
    for ( QueryProposition queryProposition : queryPropositions ) {
      stateParts.addAll( getStateParts( queryProposition.getInnerFormula() ) );
    }
    String result = "";
    for ( FolFormula f : stateParts ) {
      result += getStatePartRules( f );
    }
    for ( PropositionalFormula p : queryParts ) {
      result += getQueryPartRules( p );
    }
    for ( QueryProposition q : queryPropositions ) {
      result += getQueryPropositionPartRules( q );
    }
    
    return result;
  }
  
  /**
   * This function translates a propositional formula into a logic program
   * according to certain rules. See [1] for a detailed description of this
   * translation.
   * 
   * @param statePart a propositional formula.
   * @return a string containing rules of a normal logic program that represent
   *         the given formula.
   */
  private String getStatePartRules( FolFormula statePart )
  {
    String result = "";
    if ( statePart instanceof Negation ) {
      Negation neg = (Negation) statePart;
      result += removeIllegalCharacters( neg.toString() );
      result += "(S) :- not ";
      result += removeIllegalCharacters( neg.getFormula().toString() );
      result += "(S), state(S).\n";
    }
    else if ( statePart instanceof Conjunction ) {
      Conjunction conj = (Conjunction) statePart;
      result += removeIllegalCharacters( conj.toString() );
      result += "(S) :- ";
      for ( RelationalFormula f : conj ) {
        result += removeIllegalCharacters( f.toString() );
        result += "(S), ";
      }
      result += "state(S).\n";
    }
    else if ( statePart instanceof Disjunction ) {
      Disjunction disj = (Disjunction) statePart;
      for ( RelationalFormula f : disj ) {
        result += removeIllegalCharacters( disj.toString() );
        result += "(S) :- ";
        result += removeIllegalCharacters( f.toString() );
        result += "(S), state(S).\n";
      }
    }
    return result;
  }
  
  /**
   * Calculates the translation of an action query to rules of a normal logic
   * program according to the translation sheme presented in [1].
   * 
   * @param queryPart
   * @return a String containing normal logic rules that represent the given
   *         action query.
   */
  private String getQueryPartRules( PropositionalFormula queryPart )
  {
    String result = "";
    if ( queryPart instanceof net.sf.tweety.logics.pl.syntax.Negation ) {
      net.sf.tweety.logics.pl.syntax.Negation neg =
        (net.sf.tweety.logics.pl.syntax.Negation) queryPart;
      result += removeIllegalCharacters( neg.toString() );
      result += "(S) :- not ";
      result += removeIllegalCharacters( neg.getFormula().toString() );
      result += "(S), state(S).\n";
    }
    else if ( queryPart instanceof net.sf.tweety.logics.pl.syntax.Conjunction ) {
      net.sf.tweety.logics.pl.syntax.Conjunction conj =
        (net.sf.tweety.logics.pl.syntax.Conjunction) queryPart;
      result += removeIllegalCharacters( conj.toString() );
      result += "(S) :- ";
      for ( PropositionalFormula f : conj ) {
        result += removeIllegalCharacters( f.toString() );
        result += "(S), ";
      }
      result += "state(S).\n";
    }
    else if ( queryPart instanceof net.sf.tweety.logics.pl.syntax.Disjunction ) {
      net.sf.tweety.logics.pl.syntax.Disjunction disj =
        (net.sf.tweety.logics.pl.syntax.Disjunction) queryPart;
      for ( PropositionalFormula p : disj ) {
        result += removeIllegalCharacters( disj.toString() );
        result += "(S) :- ";
        result += removeIllegalCharacters( p.toString() );
        result += "(S), state(S).\n";
      }
    }
    return result;
  }
  
  /**
   * Calculatesthe translation of a query proposition (holds, always,
   * necessarily) to rules of a normal logic program.
   * 
   * @param queryProposition
   * @return the rules of a normal logic program representing the given query
   *         proposition.
   */
  private String getQueryPropositionPartRules( QueryProposition queryProposition )
  {
    String result = "";
    if ( queryProposition instanceof HoldsQuery ) {
      HoldsQuery holdsQuery = (HoldsQuery) queryProposition;
      result += removeIllegalCharacters( holdsQuery.toString() );
      result += "(S) :- ";
      result +=
        removeIllegalCharacters( holdsQuery.getInnerFormula().toString() );
      result += "(S), state(S).\n";
    }
    else if ( queryProposition instanceof AlwaysQuery ) {
      AlwaysQuery alwaysQuery = (AlwaysQuery) queryProposition;
      result += removeIllegalCharacters( alwaysQuery.toString() );
      result += "(S) :- not ";
      result +=
        removeIllegalCharacters( alwaysQuery.getInnerFormula().toString() );
      result += "_notalways, state(S).\n";
      result +=
        removeIllegalCharacters( alwaysQuery.getInnerFormula().toString() );
      result += "_notalways :- not ";
      result +=
        removeIllegalCharacters( alwaysQuery.getInnerFormula().toString() );
      result += "(S), state(S).\n";
    }
    else if ( queryProposition instanceof NecessarilyQuery ) {
      NecessarilyQuery necessarilyQuery = (NecessarilyQuery) queryProposition;
      result += removeIllegalCharacters( necessarilyQuery.toString() );
      result += "(S) :- not ";
      result += removeIllegalCharacters( necessarilyQuery.toString() );
      result += "_neg(S), state(S).\n";
      result += removeIllegalCharacters( necessarilyQuery.toString() );
      result += "_neg(S) :- t(S,";
      result +=
        removeIllegalCharacters( necessarilyQuery.getActions().get( 0 )
          .toString() );
      result += ",S2), state(S), state(S2)";
      if ( necessarilyQuery.getActions().size() > 1 ) {
        result += ", not ";
        result +=
          removeIllegalCharacters( getNecessarilyQueryMinusFirstAction(
            necessarilyQuery ).toString() );
        result += "(S2)";
      }
      else if ( !( necessarilyQuery.getInnerFormula() instanceof Contradiction ) ) {
        result += ", not ";
        result +=
          removeIllegalCharacters( necessarilyQuery.getInnerFormula()
            .toString() );
        result += "(S2)";
      }
      result += ".\n";
    }
    
    return result;
  }
  
  /**
   * Calculates the set of all possible parts of the propositional formula
   * given.
   * 
   * @param formula a propositional formula in the form of a FolFormula which is
   *          used for easy grounding.
   * @return The set of all parts of the given formula.
   */
  private Set< FolFormula > getStateParts( FolFormula formula )
  {
    Set< FolFormula > result = new HashSet< FolFormula >();
    result.add( formula );
    if ( formula instanceof AssociativeFOLFormula ) {
      for ( RelationalFormula rel : (AssociativeFOLFormula) formula ) {
        result.addAll( getStateParts( (FolFormula) rel ) );
      }
    }
    else if ( formula instanceof Negation ) {
      Negation neg = (Negation) formula;
      FolFormula f = (FolFormula) neg.getFormula();
      result.addAll( getStateParts( f ) );
    }
    return result;
  }
  
  /**
   * Calculates the set of all subformulas of an action query down to
   * propositions (holds, always, necessarily)
   * 
   * @param formula an action query in the form of a propositional formula
   * @return the set of all subformulas of formula.
   */
  private Set< PropositionalFormula > getQueryParts(
    PropositionalFormula formula )
  {
    Set< PropositionalFormula > result = new HashSet< PropositionalFormula >();
    result.add( formula );
    if ( formula instanceof net.sf.tweety.logics.pl.syntax.AssociativePropositionalFormula ) {
      for ( PropositionalFormula f : (net.sf.tweety.logics.pl.syntax.AssociativePropositionalFormula) formula ) {
        result.addAll( getQueryParts( f ) );
      }
    }
    else if ( formula instanceof net.sf.tweety.logics.pl.syntax.Negation ) {
      net.sf.tweety.logics.pl.syntax.Negation neg =
        (net.sf.tweety.logics.pl.syntax.Negation) formula;
      result.addAll( getQueryParts( neg.getFormula() ) );
    }
    return result;
  }
  
  /**
   * Calculates the set of all query propositions which appear in the given
   * query. A query proposition may either be a holds, always or a necessarily
   * query.
   * 
   * @param query an action query.
   * @return the set of all query propositions in query.
   */
  private Set< QueryProposition > getQueryPropositions( SActionQuery query )
  {
    Set< QueryProposition > result = new HashSet< QueryProposition >();
    for ( Proposition p : query.getFormula().getAtoms() ) {
      QueryProposition qprop = (QueryProposition) p;
      result.add( qprop );
      if ( qprop instanceof NecessarilyQuery ) {
        NecessarilyQuery nec = (NecessarilyQuery) qprop;
        while ( nec.getActions().size() > 1 ) {
          nec = getNecessarilyQueryMinusFirstAction( nec );
          result.add( nec );
        }
      }
    }
    return result;
  }
  
  /**
   * Calculates a normal logic program which consists only of facts describing
   * the transition system given.
   * 
   * @param transitionSystem a transition system.
   * @return a string containing the rules of a logic program that represents
   *         the transition system.
   */
  private String getTransitionSystemRules( TransitionSystem transitionSystem )
  {
    String statefacts = "";
    String fluentfacts = "";
    String transitionfacts = "";
    int statecounter = 0;
    Map< State, String > statemap = new HashMap< State, String >();
    for ( State s : transitionSystem.getStates() ) {
      String statename = "s" + Integer.toString( statecounter );
      statemap.put( s, statename );
      statefacts += "state(" + statename + ").\n";
      for ( FOLAtom a : s.getPositiveFluents() ) {
        fluentfacts += removeIllegalCharacters( a.toString() );
        fluentfacts += "(" + statename + ").\n";
      }
      fluentfacts += "\n";
      statecounter++;
    }
    for ( Transition t : transitionSystem.getTransitions() ) {
      transitionfacts +=
        "t(" + statemap.get( t.getFrom() ) + "," +
          removeIllegalCharacters( t.getAction().toString() ) + "," +
          statemap.get( t.getTo() ) + ").\n";
    }
    
    return statefacts + "\n" + fluentfacts + "\n" + transitionfacts;
  }
  
  /**
   * For a given necessarily query of the form 
   *   necessarily F after A_0 ; A_1 ; ... ; A_n 
   * this function returns a new query of the form 
   *   necessarily F after A_1 ; ... ; A_n .
   * 
   * @param q a necessarily query.
   * @return a new necessarily query wich has the same action sequence as q
   *         minus the first action.
   */
  private NecessarilyQuery getNecessarilyQueryMinusFirstAction(
    NecessarilyQuery q )
  {
    List< FolAction > actionList = q.getActions();
    if ( actionList.size() < 2 )
      return q;
    actionList.remove( 0 );
    return new NecessarilyQuery( q.getInnerFormula(), actionList );
  }
  
  /**
   * For an easy mapping of formulas to atoms in a logic program, this function
   * changes all symbols, which may occur in a string representation of an
   * action query to unique valid characters in the input language of lparse.
   * 
   * @param s a string, possibly containing invalid characters in the input
   *          language of lparse-compatible asp-solvers.
   * @return a new string, containing only valid characters.
   */
  private String removeIllegalCharacters( String s )
  {
    return s.replace( "(", "xxx1xxx" ).replace( ")", "xxx2xxx" ).replace( ",", "xxx3xxx" )
      .replace( "!", "xxx4xxx" ).replace( "&&", "xxx5xxx" ).replace( "||", "xxx6xxx" )
      .replace( "[", "xxx7xxx" ).replace( "]", "xxx8xxx" ).replace( "{", "xxx9xxx" )
      .replace( "}", "xxx10xxx" ).replace( " ", "xxx11xxx" ).replace( "+", "xxx12xxx" )
      .replace( "-", "xxx13xxx" ).replace( ";", "xxx14xxx" );
  }
  
  /**
   * This function exists mainly for debug reasons to regain a human readable
   * version of the atoms in a logic program or in a resulting stable model.
   * 
   * @param s
   */
  public String regainIllegalCharacters( String s )
  {
    return s.replace( "xxx1xxx", "(" ).replace( "xxx2xxx", ")" ).replace( "xxx3xxx", "," )
      .replace( "xxx4xxx", "!" ).replace( "xxx5xxx", "&&" ).replace( "xxx6xxx", "||" )
      .replace( "xxx7xxx", "[" ).replace( "xxx8xxx", "]" ).replace( "xxx9xxx", "{" )
      .replace( "xxx10xxx", "}" ).replace( "xxx11xxx", " " ).replace( "xxx12xxx", "+" )
      .replace( "xxx13xxx", "-" ).replace( "xxx14xxx", ";" );
  }
}
