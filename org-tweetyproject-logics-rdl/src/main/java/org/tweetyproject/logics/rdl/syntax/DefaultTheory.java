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
package org.tweetyproject.logics.rdl.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.logics.fol.syntax.FolBeliefSet;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 * Models a default theory in Reiter's default logic, see [R. Reiter. A logic for default reasoning. Artificial Intelligence, 13:81–132, 1980].
 * 
 * @author Matthias Thimm, Nils Geilen
 *
 */
public class DefaultTheory implements BeliefBase{

	/** The set of facts (first-order formulas). */
	private FolBeliefSet facts;
	/** The set of default rules */
	private Collection<DefaultRule> defaults;
	
	
	/**
	 * constructs empty default theory
	 */
	public DefaultTheory() {
		super();
		this.facts = new FolBeliefSet();
		this.defaults = new ArrayList<DefaultRule>();
	}

	/**
	 * constructs a default theory from a knowledge base and a set of defaults
	 * @param facts the knowledge base
	 * @param defaults the defaults
	 */
	public DefaultTheory(FolBeliefSet facts, Collection<DefaultRule> defaults) {
		super();
		this.facts = facts;
		this.defaults = new ArrayList<DefaultRule>();
		this.defaults.addAll(defaults);
	}
	
	/**
	 * add facts to knowledge base
	 * @param fact some fol formula
	 */
	void addFact(FolFormula fact){
		facts.add(fact);
	}
	
	/**
	 * removes fact from knowledge base
	 * @param fact some fol formula
	 */
	void removeFact(FolFormula fact){
		facts.remove(fact);
	}
	
	/**
	 * adds default rule 
	 * @param d a default rule
	 */
	void addDefault(DefaultRule d){
		defaults.add(d);
	}
	
	/**
	 * removes default rule
	 * @param d a default rule 
	 */
	void removeDefault(DefaultRule d){
		defaults.remove(d);
	}
	
	/**
	 * @return all the default rules
	 */
	public Collection<DefaultRule> getDefaults() {
		return defaults;
	}
	
	/**
	 * Removes Variables by expanding formulas
	 * @return grounded version of the default theory
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DefaultTheory ground(){
		Set<DefaultRule> ds = new HashSet<>();
		for(DefaultRule d: defaults){
			ds.addAll((Set)(d.allGroundInstances(((FolSignature)getMinimalSignature()).getConstants())));
		}
		return new DefaultTheory(facts, ds);
	}

	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.BeliefBase#getSignature()
	 */
	@Override
	public Signature getMinimalSignature() {
		Signature result = facts.getMinimalSignature();
		for(DefaultRule d: defaults)
			result.addSignature(d.getSignature());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = facts +"\n\n";
		for(DefaultRule d : defaults)
			result += d + "\n";
		return result;
	}
	
	/**
	 * 
	 * @return FoL formulas in default theories
	 */
	public FolBeliefSet getFacts(){
		return facts;
	}
	
	
}
