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
package net.sf.tweety.logics.rdl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.fol.syntax.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.rdl.syntax.DefaultRule;

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
	 */
	void addFact(FolFormula fact){
		facts.add(fact);
	}
	
	/**
	 * removes fact from knowledge base
	 */
	void removeFact(FolFormula fact){
		facts.remove(fact);
	}
	
	/**
	 * adds default rule 
	 */
	void addDefault(DefaultRule d){
		defaults.add(d);
	}
	
	/**
	 * removes default rule 
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
			ds.addAll((Set)(d.allGroundInstances(((FolSignature)getSignature()).getConstants())));
		}
		return new DefaultTheory(facts, ds);
	}

	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefBase#getSignature()
	 */
	@Override
	public Signature getSignature() {
		Signature result = facts.getSignature();
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
