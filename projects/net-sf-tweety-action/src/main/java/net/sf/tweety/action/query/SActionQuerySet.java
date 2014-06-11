package net.sf.tweety.action.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.action.ActionQuerySet;
import net.sf.tweety.action.grounding.GroundingRequirement;
import net.sf.tweety.action.query.syntax.SActionQuery;
import net.sf.tweety.action.signature.ActionSignature;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.pl.syntax.Conjunction;

/**
 * This class models a set of action queries in the language S which is based on
 * the query language "P" discussed in the paper: Action Languages. by Michael
 * Gelfond and Vladimir Lifschitz, ETAI: Electronic Transactions on AI, 1998.
 * 
 * @author Sebastian Homann
 */
public class SActionQuerySet
  extends ActionQuerySet< SActionQuery >
{
  
  /**
   * Creates a new empty action query set for the query language S.
   */
  public SActionQuerySet()
  {
  }
  
  /**
   * Creates a new belief set with the given collection of queries.
   * 
   * @param c a collection of formulae.
   */
  public SActionQuerySet( Collection< SActionQuery > c )
  {
    super( c );
  }
  
  /*
   * (non-Javadoc)
   * @see net.sf.tweety.BeliefSet#getSignature()
   */
  @Override
  public Signature getSignature()
  {
    ActionSignature sig = new ActionSignature();
    for ( SActionQuery q : this )
      sig.add( q.getActionSignature() );
    return sig;
  }
  
  /**
   * Returns a new SActionQuerySet consisting of all possible groundings of the
   * contained queries.
   * 
   * @return a new SActionQuerySet consisting of all possible groundings of the
   *         contained queries.
   */
  public SActionQuerySet ground()
  {
    Set< SActionQuery > queries = new HashSet< SActionQuery >();
    for ( SActionQuery query : this ) {
      queries.addAll( query.getAllGrounded() );
    }
    return new SActionQuerySet( queries );
  }
  
  /**
   * Returns a conjunction of all contained queries. The resulting action query
   * is satisfied iff all queries contained in this query set are satisfied.
   * 
   * @return a conjunction of all contained queries.
   */
  public SActionQuery getConjunctionOfAllQueries()
  {
    Conjunction result = new Conjunction();
    Set< GroundingRequirement > requirements =
      new HashSet< GroundingRequirement >();
    for ( SActionQuery q : this ) {
      result.add( q.getFormula() );
      requirements.addAll( q.getGroundingRequirements() );
    }
    return new SActionQuery( result, requirements );
  }
}
