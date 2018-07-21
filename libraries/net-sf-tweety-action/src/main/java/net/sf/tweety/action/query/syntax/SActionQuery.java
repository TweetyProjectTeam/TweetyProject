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
package net.sf.tweety.action.query.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.action.grounding.GroundingRequirement;
import net.sf.tweety.action.grounding.GroundingTools;
import net.sf.tweety.action.signature.ActionSignature;
import net.sf.tweety.action.signature.FolAction;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * This class represents a single action query in the action query language S,
 * which is based on the query language "P" discussed in the paper: Action
 * Languages. by Michael Gelfond and Vladimir Lifschitz, ETAI: Electronic
 * Transactions on AI, 1998. 
 * 
 * An action query is represented by a propositionalformula over propositions 
 * of one of the following kinds: HoldsQuery, AlwaysQuery, NecessarilyQuery.
 * 
 * @author Sebastian Homann
 */
public class SActionQuery
  implements ActionQuery
{
  
  protected PropositionalFormula formula;
  protected Set< GroundingRequirement > requirements =
    new HashSet< GroundingRequirement >();
  
  /**
   * Creates a new action query with the given propositional formula and no
   * grounding requirements.
   * 
   * @param formula
   */
  public SActionQuery( PropositionalFormula formula )
  {
    if ( formula == null )
      throw new NullPointerException();
    for ( Proposition p : formula.getAtoms() ) {
      if ( !( p instanceof QueryProposition ) )
        throw new IllegalArgumentException(
          "Invalid proposition in action query: has to be of type QueryProposition." );
    }
    this.formula = formula;
  }
  
  /**
   * Creates a new action query with the given propositional formula 
   * and grounding requirements.
   * @param formula
   * @param requirements
   */
  public SActionQuery( PropositionalFormula formula,
    Set< GroundingRequirement > requirements )
  {
    this( formula );
    this.requirements.addAll( requirements );
  }
  
  /*
   * (non-Javadoc)
   * @see net.sf.tweety.Formula#getSignature()
   */
  @Override
  public Signature getSignature()
  {
    return formula.getSignature();
  }
  
  /**
   * Returns the formula represented by this action query.
   * @return the formula represented by this action query.
   */
  public PropositionalFormula getFormula()
  {
    return formula;
  }
  
  /**
   * Returns the action signature of this action query.
   * @return the action signature of this action query.
   */
  public ActionSignature getActionSignature()
  {
    ActionSignature s = new ActionSignature();
    for ( Proposition p : formula.getAtoms() ) {
      s.add( ( (QueryProposition) p ).getActionSignature() );
    }
    return s;
  }
  
  /**
   * Returns all inner formulas that are contained in query propositions
   * in this action query.
   * @return all inner formulas of this action query.
   */
  public Set< FolFormula > getInnerFormulas()
  {
    Set< FolFormula > result = new HashSet< FolFormula >();
    for ( Proposition p : formula.getAtoms() ) {
      result.add( ( (QueryProposition) p ).getInnerFormula() );
    }
    return result;
  }
  
  /**
   * Returns all actions, which occur in action sequences in necessarily
   * queries in this action query.
   * @return all actions, which occur in action sequences in necessarily
   * queries in this action query.
   */
  public Set< FolAction > getInnerActions()
  {
    Set< FolAction > result = new HashSet< FolAction >();
    for ( Proposition p : formula.getAtoms() ) {
      result.addAll( ( (QueryProposition) p ).getInnerActions() );
    }
    return result;
  }
  
  /**
   * Returns all inner atoms, which occur in state formulas and actions in 
   * this action query.
   * @return all inner atoms, which occur in state formulas and actions in 
   * this action query.
   */
  @SuppressWarnings("unchecked")
public Set< FOLAtom > getInnerAtoms()
  {
    Set< FOLAtom > result = new HashSet< FOLAtom >();
    for ( Proposition p : formula.getAtoms() ) {
      result.addAll( (Collection<? extends FOLAtom>) ( (QueryProposition) p ).getInnerFormula().getAtoms() );
      for ( FolAction action : ( (QueryProposition) p ).getInnerActions() )
        result.addAll( action.getAtoms() );
    }
    return result;
  }
  
  /**
   * Returns all inner variables, which occur in state formulas and actions
   * in this action query. 
   * @return all inner variables, which occur in state formulas and actions
   * in this action query. 
   */
  public Set< Variable > getInnerVariables()
  {
    Set< Variable > variables = new HashSet< Variable >();
    for ( FOLAtom a : getInnerAtoms() )
      variables.addAll( a.getUnboundVariables() );
    return variables;
  }
  
  /**
   * Returns all grounding requirements, that have to be met, when this
   * action query is grounded.
   * @return a set of grounding requirements.
   */
  public Set< GroundingRequirement > getGroundingRequirements()
  {
    return new HashSet< GroundingRequirement >( requirements );
  }
  
  /**
   * Returns the set of all grounded instances of this causal rule.
   * 
   * @return the set of all grounded instances of this causal rule.
   */
  public Set< SActionQuery > getAllGrounded()
  {
    Set< SActionQuery > result = new HashSet< SActionQuery >();
    Set< Variable > variables = new HashSet< Variable >();
    
    for ( FOLAtom a : getInnerAtoms() )
      variables.addAll( a.getUnboundVariables() );
    
    Set< Map< Variable, Constant >> substitutions =
      GroundingTools.getAllSubstitutions( variables );
    for ( Map< Variable, Constant > map : substitutions ) {
      if ( GroundingTools.isValidGroundingApplication( map, requirements ) )
        result.add( substituteInnerFormulas( map ) );
    }
    return result;
  }
  
  /**
   * Returns a new action query in which all variables are mapped to constants
   * with regard to the given map.
   * @param map a map from variables to constants.
   * @return a new action query in which all variables are mapped to constants
   * with regard to the given map.
   */
  protected SActionQuery substituteInnerFormulas( Map< Variable, Constant > map )
  {
    return new SActionQuery( substitutePropositions( map, formula ),
      requirements );
  }
  
  /**
   * Utility function that walks through all parts of a propositional formula
   * with query propositions substituting all variables with constants according
   * to the given map.
   * @param map a map from variables to constants.
   * @param formula an action query.
   * @return a new propositional formula in which all variables have been 
   * substituted by constants according to the given map. 
   */
  private static PropositionalFormula substitutePropositions(
    Map< Variable, Constant > map, PropositionalFormula formula )
  {
    if ( formula instanceof Conjunction ) {
      Conjunction newMe = new Conjunction();
      for ( PropositionalFormula f : (Conjunction) formula )
        newMe.add( substitutePropositions( map, f ) );
      return newMe;
    }
    if ( formula instanceof Disjunction ) {
      Disjunction newMe = new Disjunction();
      for ( PropositionalFormula f : (Disjunction) formula )
        newMe.add( substitutePropositions( map, f ) );
      return newMe;
    }
    if ( formula instanceof Negation )
      return new Negation( substitutePropositions( map, ( (Negation) formula )
        .getFormula() ) );
    
    if ( formula instanceof QueryProposition )
      return ( (QueryProposition) formula ).substitute( map );
    
    return formula;
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    String result = formula.toString();
    //    if(!requirements.isEmpty())
    //      result += requirements.toString();
    return result;
  }
}
