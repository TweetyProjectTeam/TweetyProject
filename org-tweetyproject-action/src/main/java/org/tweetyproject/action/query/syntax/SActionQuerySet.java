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
package org.tweetyproject.action.query.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.action.grounding.GroundingRequirement;
import org.tweetyproject.action.signature.ActionSignature;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.logics.pl.syntax.Conjunction;

/**
 * This class models a set of action queries in the language "S" which is based on
 * the query language "P" discussed in the paper:
 * [Gelfond, Michael and Lifschitz, Vladimir: Action Languages.
 * ETAI: Electronic Transactions on AI, 1998.]
 *
 * @author Sebastian Homann
 */
public class SActionQuerySet extends ActionQuerySet<SActionQuery> {

	/**
	 * Creates a new empty action query set for the query language S.
	 */
	public SActionQuerySet() {
	}

	/**
	 * Creates a new belief set with the given collection of queries.
	 *
	 * @param c a collection of formulae.
	 */
	public SActionQuerySet(Collection<SActionQuery> c) {
		super(c);
	}

	/**
	 * Creates a new SActionQuerySet consisting of all possible groundings
	 * @return a new SActionQuerySet consisting of all possible groundings of the
	 *         contained queries.
	 */
	public SActionQuerySet ground() {
		Set<SActionQuery> queries = new HashSet<SActionQuery>();
		for (SActionQuery query : this) {
			queries.addAll(query.getAllGrounded());
		}
		return new SActionQuerySet(queries);
	}

	/**
	 * Returns a conjunction of all contained queries. The resulting action query is
	 * satisfied iff all queries contained in this query set are satisfied.
	 *
	 * @return a conjunction of all contained queries.
	 */
	public SActionQuery getConjunctionOfAllQueries() {
		Conjunction result = new Conjunction();
		Set<GroundingRequirement> requirements = new HashSet<GroundingRequirement>();
		for (SActionQuery q : this) {
			result.add(q.getFormula());
			requirements.addAll(q.getGroundingRequirements());
		}
		return new SActionQuery(result, requirements);
	}

	@Override
	public Signature getMinimalSignature() {
		ActionSignature sig = new ActionSignature();
		for (SActionQuery q : this)
			sig.add(q.getActionSignature());
		return sig;
	}
}
