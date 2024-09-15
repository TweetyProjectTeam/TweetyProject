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
package org.tweetyproject.action.description.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.action.grounding.GroundingRequirement;
import org.tweetyproject.action.grounding.GroundingTools;
import org.tweetyproject.action.signature.ActionSignature;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.fol.syntax.*;

/**
 * A dynamic law in C has the form "caused F if G after U" where F is a
 * propositional formula over the set of fluent names (called headFormula) G is
 * a propositional formula over the set of fluent names (called ifFormula) U is
 * a propositional formula over the set of fluent names and the set of action
 * names (called afterFormula)
 *
 * @author wutsch
 */
public class DynamicLaw extends CLaw {

	/**
 	* The after formula of this dynamic law.
 	*/
	protected FolFormula afterFormula = new Tautology();

	/**
	 * Constructs a new empty dynamic law.
	 */
	public DynamicLaw() {
		super();
	}

	/**
	 * Creates a new dynamic law of the form: "caused headFormula if ifFormula after
	 * afterFormula"
	 *
	 * @param headFormula  some FOL formula
	 * @param ifFormula    some FOL formula
	 * @param afterFormula some FOL formula
	 */
	public DynamicLaw(FolFormula headFormula, FolFormula ifFormula, FolFormula afterFormula) {
		super(headFormula, ifFormula);
		setAfterFormula(afterFormula);
	}

	/**
	 * Creates a new dynamic law of the form: "caused headFormula if ifFormula after
	 * afterFormula requires requirements"
	 *
	 * @param headFormula  some FOL formula
	 * @param ifFormula    some FOL formula
	 * @param afterFormula some FOL formula
	 * @param requirements a set of requirements
	 */
	public DynamicLaw(FolFormula headFormula, FolFormula ifFormula, FolFormula afterFormula,
			Set<GroundingRequirement> requirements) {
		super(headFormula, ifFormula, requirements);
		setAfterFormula(afterFormula);
	}

	/**
	 * Creates a new dynamic law of the form "caused headFormula after afterFormula"
	 *
	 * @param headFormula  some FOL formula
	 * @param afterFormula some FOL formula
	 */
	public DynamicLaw(FolFormula headFormula, FolFormula afterFormula) {
		super(headFormula);
		setAfterFormula(afterFormula);
	}

	/**
	 * Creates a new dynamic law of the form "caused headFormula after afterFormula"
	 * requires requirements
	 *
	 * @param headFormula  some FOL formula
	 * @param afterFormula some FOL formula
	 * @param requirements a set of requirements
	 */
	public DynamicLaw(FolFormula headFormula, FolFormula afterFormula, Set<GroundingRequirement> requirements) {
		super(headFormula, requirements);
		setAfterFormula(afterFormula);
	}

	/**
	 * Sets the afterFormula of this causal law
	 *
	 * @param afterFormula the new afterFormula of this causal law.
	 */
	private void setAfterFormula(FolFormula afterFormula) {
		if (afterFormula == null) {
			return;
		}

		if (!(new ActionSignature(afterFormula).isRepresentable(afterFormula))) {
			throw new IllegalArgumentException("The formula given has an illegal form");
		}
		this.afterFormula = (FolFormula) afterFormula.collapseAssociativeFormulas();
	}

/**
 * Retrieves the after formula of this causal law.
 *
 * @return the `FolFormula` that constitutes the after formula of this causal law.
 */
public FolFormula getAfterFormula() {
    return afterFormula;
}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.action.desc.c.syntax.CausalRule#isDefinite()
	 */
	@Override
	public boolean isDefinite() {
		if (!headFormula.isLiteral())
			return false;

		if (!isConjunctiveClause(ifFormula))
			return false;

		if (!isConjunctiveClause(afterFormula))
			return false;

		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (ifFormula.equals(headFormula) && ifFormula.equals(afterFormula)) {
			return "inertial " + headFormula.toString();
		} else {
			String r = "caused " + headFormula.toString();
			if (!(ifFormula instanceof Tautology))
				r += " if " + ifFormula.toString();

			return r + " after " + afterFormula.toString();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.action.desc.c.syntax.CausalRule#getSignature()
	 */
	@Override
	public Signature getSignature() {
		ActionSignature sig = new ActionSignature(headFormula);
		sig.add(ifFormula);
		sig.add(afterFormula);
		return sig;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.action.desc.c.syntax.CausalRule#getAtoms()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<FolAtom> getAtoms() {
		Set<FolAtom> result = new HashSet<FolAtom>();
		result.addAll((Collection<? extends FolAtom>) headFormula.getAtoms());
		result.addAll((Collection<? extends FolAtom>) ifFormula.getAtoms());
		result.addAll((Collection<? extends FolAtom>) afterFormula.getAtoms());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.action.desc.c.syntax.CausalRule#toDefinite()
	 */
	@Override
	public Set<CLaw> toDefinite() {
		if (!isValidDefiniteHead(headFormula)) {
			throw new IllegalStateException("Cannot convert causal law with nonliteral head formula to definite form.");
		}
		Set<CLaw> definit = new HashSet<CLaw>();
		Set<RelationalFormula> ifClauses = new HashSet<RelationalFormula>();
		Set<RelationalFormula> afterClauses = new HashSet<RelationalFormula>();

		FolFormula ifDNF = ifFormula.toDnf();
		if (ifDNF instanceof Disjunction) {
			Disjunction conjClause = (Disjunction) ifDNF;
			for (RelationalFormula p : conjClause) {
				ifClauses.add(p);
			}
		} else {
			ifClauses.add(ifDNF);
		}
		FolFormula afterDNF = (FolFormula) afterFormula.toDnf().collapseAssociativeFormulas();
		if (afterDNF instanceof Disjunction) {
			Disjunction conjClause = (Disjunction) afterDNF;
			for (RelationalFormula p : conjClause) {
				afterClauses.add(p);
			}
		} else {
			afterClauses.add(afterDNF);
		}
		for (RelationalFormula ifClause : ifClauses) {
			for (RelationalFormula afterClause : afterClauses) {
				definit.add(new DynamicLaw(headFormula, (FolFormula) ifClause, (FolFormula) afterClause, requirements));
			}
		}

		return definit;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.action.desc.c.syntax.CausalRule#getAllGroundings()
	 */
	@Override
	public Set<CLaw> getAllGrounded() {
		Set<CLaw> result = new HashSet<CLaw>();
		Set<Variable> variables = new HashSet<Variable>();

		for (FolAtom a : getAtoms()) {
			variables.addAll(a.getUnboundVariables());
		}
		Set<Map<Variable, Constant>> substitutions = GroundingTools.getAllSubstitutions(variables);

		for (Map<Variable, Constant> map : substitutions) {
			if (GroundingTools.isValidGroundingApplication(map, requirements))
				result.add(
						new DynamicLaw((FolFormula) headFormula.substitute(map), (FolFormula) ifFormula.substitute(map),
								(FolFormula) afterFormula.substitute(map), requirements));
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.action.description.c.syntax.CRule#getFormulas()
	 */
	@Override
	public Set<FolFormula> getFormulas() {
		Set<FolFormula> result = new HashSet<FolFormula>();
		result.add(headFormula);
		result.add(ifFormula);
		result.add(afterFormula);
		return result;
	}
}
