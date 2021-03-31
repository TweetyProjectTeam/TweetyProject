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
package org.tweetyproject.logics.dl.semantics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.commons.InterpretationSet;
import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.logics.dl.syntax.AssertionalAxiom;
import org.tweetyproject.logics.dl.syntax.AtomicConcept;
import org.tweetyproject.logics.dl.syntax.AtomicRole;
import org.tweetyproject.logics.dl.syntax.BottomConcept;
import org.tweetyproject.logics.dl.syntax.Complement;
import org.tweetyproject.logics.dl.syntax.ComplexConcept;
import org.tweetyproject.logics.dl.syntax.ConceptAssertion;
import org.tweetyproject.logics.dl.syntax.DlAxiom;
import org.tweetyproject.logics.dl.syntax.DlBeliefSet;
import org.tweetyproject.logics.dl.syntax.DlSignature;
import org.tweetyproject.logics.dl.syntax.EquivalenceAxiom;
import org.tweetyproject.logics.dl.syntax.ExistentialRestriction;
import org.tweetyproject.logics.dl.syntax.Individual;
import org.tweetyproject.logics.dl.syntax.Intersection;
import org.tweetyproject.logics.dl.syntax.RoleAssertion;
import org.tweetyproject.logics.dl.syntax.TopConcept;
import org.tweetyproject.logics.dl.syntax.Union;
import org.tweetyproject.logics.dl.syntax.UniversalRestriction;

/**
 * This class models an interpretation for description logics. A DL
 * interpretation consists of a domain (a set of individuals) and a 
 * mapping of each concept name to a subset of the domain
 * and of each role name to a binary relation on the domain 
 * (this mapping is often referred to as the interpretation function in literature).
 *
 * @author Anna Gessler
 */
public class DlInterpretation extends InterpretationSet<AssertionalAxiom, DlBeliefSet, DlAxiom> {
	/**
	 * The domain of this interpretation.
	 */
	private Set<Individual> domain;

	/**
	 * Create a new DL interpretation with the given set of concept
	 * and role assertions that represent the mapping
	 * of concept names and role names to the domain. 
	 * 
	 * @param assertions collection of AssertionalAxiom
	 */
	public DlInterpretation(Collection<AssertionalAxiom> assertions) {
		super(assertions);
		this.domain = this.getDomain();
	}

	@Override
	public boolean satisfies(DlAxiom formula) throws IllegalArgumentException {
		if (formula instanceof ConceptAssertion) {
			ConceptAssertion ca = (ConceptAssertion) formula;
			return getConceptDomain(ca.getConcept()).contains(ca.getIndividual());
		}
		if (formula instanceof RoleAssertion) {
			RoleAssertion ra = (RoleAssertion) formula;
			return getRoleDomain(ra.getRole()).contains(ra.getIndividuals());
		}
		if (formula instanceof EquivalenceAxiom) {
			EquivalenceAxiom a = (EquivalenceAxiom) formula;
			ComplexConcept c1 = a.getFormulas().getFirst();
			ComplexConcept c2 = a.getFormulas().getSecond();
			return isSubsumedBy(c1,c2);
		}
		throw new IllegalArgumentException("DlAxiom " + formula + " is of unknown type.");
	}
	
	@Override
	public boolean satisfies(DlBeliefSet beliefBase) throws IllegalArgumentException {
		for (DlAxiom f : beliefBase)
			if (!this.satisfies(f))
				return false;
		return true;
	}

	
	/**
	 * @return the domain of this interpretation (individuals).
	 */
	private Set<Individual> getDomain() {
		DlSignature sig = new DlSignature();
		for (DlAxiom a : this)
			sig.add(a);
		return sig.getIndividuals();
	}
	
	/**
	 * Checks whether a concept is subsumed by another concept
	 * (c1 =&gt; c2) wrt to this interpretation.
	 * @param c1 a concept
	 * @param c2 a concept
	 * @return "true" if c1 is subsumed by c2, "false" otherwise
	 */
	public boolean isSubsumedBy(ComplexConcept c1, ComplexConcept c2) {
		Set<Individual> d1 = getConceptDomain(c1);
		Set<Individual> d2 = getConceptDomain(c2);
		for (Individual i : d1) {
			if (!d2.contains(i))
				return false;
		}
		return true;
	}

	/**
	 * Returns the subset of the domain that belongs to the given role (i.e. the
	 * extension of the role).
	 * 
	 * @param r role
	 * @return binary subset of domain, i.e. a set of pairs of individuals
	 */
	public Set<Pair<Individual, Individual>> getRoleDomain(AtomicRole r) {
		Set<Pair<Individual, Individual>> pairs = new HashSet<Pair<Individual, Individual>>();
		for (DlAxiom at : this) {
			if (at instanceof RoleAssertion) {
				RoleAssertion ra = (RoleAssertion) at;
				if (r.equals(ra.getRole())) {
					pairs.add(ra.getIndividuals());
				}
			}
		}
		return pairs;
	}

	/**
	 * Returns the subset of the domain that belongs to the given concept (i.e. the
	 * extension of the concept).
	 * 
	 * @param c concept
	 * @return subset of domain, i.e. a set of individuals
	 */
	public Set<Individual> getConceptDomain(ComplexConcept c) {
		if (c instanceof TopConcept) {
			return new HashSet<Individual>(this.domain);
		}
		if (c instanceof BottomConcept) {
			return new HashSet<Individual>();
		}
		if (c instanceof AtomicConcept) {
			Set<Individual> result = new HashSet<Individual>();
			for (AssertionalAxiom at : this) {
				if (at instanceof ConceptAssertion) {
					AtomicConcept atc = (AtomicConcept) ((ConceptAssertion) at).getConcept();
					if (atc.equals(c))
						result.add(((ConceptAssertion) at).getIndividual());
				}
			}
			return result;
		}
		if (c instanceof Complement) {
			Complement cl = (Complement) c;
			Set<Individual> result = new HashSet<Individual>(this.domain);
			result.removeAll(getConceptDomain(cl.getFormula()));
			return result;
		}
		if (c instanceof Union) {
			Set<Individual> result = new HashSet<Individual>();
			Union u = (Union) c;
			for (ComplexConcept cx : u)
				result.addAll(getConceptDomain(cx));
			return result;
		}
		if (c instanceof Intersection) {
			Set<Individual> result = new HashSet<Individual>(this.domain);
			Intersection i = (Intersection) c;
			for (ComplexConcept cx : i)
				result.retainAll(getConceptDomain(cx));
			return result;
		}
		if (c instanceof UniversalRestriction) {
			Set<Individual> result = new HashSet<Individual>();
			UniversalRestriction u = (UniversalRestriction) c;
			Set<Pair<Individual, Individual>> role_domain = getRoleDomain(u.getRole());
			Set<Individual> concept_domain = getConceptDomain(u.getConcept());

			for (Individual i : domain) {
				boolean forall = true;
				for (Individual i2 : domain) {
					if (!concept_domain.contains(i2) || !role_domain.contains(new Pair<Individual, Individual>(i, i2))) {
						forall = false;
						break;
					}
				}
				if (forall)
					result.add(i);
			}
			return result;

		}
		if (c instanceof ExistentialRestriction) {
			Set<Individual> result = new HashSet<Individual>();
			ExistentialRestriction e = (ExistentialRestriction) c;
			Set<Pair<Individual, Individual>> role_domain = getRoleDomain(e.getRole());
			Set<Individual> concept_domain = getConceptDomain(e.getConcept());

			for (Individual i : domain) {
				boolean exists = false;
				for (Individual i2 : domain) {
					if (concept_domain.contains(i2) && role_domain.contains(new Pair<Individual, Individual>(i, i2))) {
						exists = true;
						break;
					}
				}
				if (exists)
					result.add(i);
			}
			return result;
		}
		throw new IllegalArgumentException("Concept " + c + " is of unknown type.");
	}

}
