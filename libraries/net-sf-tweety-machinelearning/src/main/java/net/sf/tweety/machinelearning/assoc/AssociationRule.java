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
package net.sf.tweety.machinelearning.assoc;

import java.util.Collection;
import java.util.HashSet;

/**
 * An association rule in item set mining.
 * @author Matthias Thimm
 *
 * @param <T> The type of items.
 */
public class AssociationRule<T> {

	/** The premise of the rule. */
	private Collection<T> premise;
	/** The conclusion of the rule. */
	private Collection<T> conclusion;
	
	/**
	 * Creates a new empty association rule.
	 */
	public AssociationRule(){
		this(new HashSet<T>(),new HashSet<T>());
	}
	
	/**
	 * Creates a new association rule with the given premise
	 * and conclusion
	 * @param premise some set of objects for the premise.
	 * @param conclusion some set of objects for the conclusion.
	 */
	public AssociationRule(Collection<T> premise, Collection<T> conclusion){
		this.premise = premise;
		this.conclusion = conclusion;
	}
	
	/**
	 * Adds the given object to the premise.
	 * @param t some object
	 * @return "true" iff this has been changed.
	 */
	public boolean addToPremise(T t){
		return this.premise.add(t);
	}
	
	/**
	 * Adds the given object to the conclusion.
	 * @param t some object
	 * @return "true" iff this has been changed.
	 */
	public boolean addToConclusion(T t){
		return this.conclusion.add(t);
	}

	/**
	 * Returns the premise of this rule.
	 * @return the premise of this rule.
	 */
	public Collection<T> getPremise(){
		return this.premise;
	}
	
	/**
	 * Returns the conclusion of this rule.
	 * @return the conclusion of this rule.
	 */
	public Collection<T> getConclusion(){
		return this.conclusion;
	}
	
	/**
	 * Returns the support of the given item wrt. the given database,
	 * i.e., the number of transactions of the database, which contain the
	 * given item, divided by the number of all transactions.
	 * @param item some object
	 * @param database a database (set of sets of objects)
	 * @return the support of the item.
	 * @param <S> the type of objects
	 */
	public static <S extends Object> double support(S item, Collection<Collection<S>> database){
		double cnt = 0;
		for(Collection<S> t: database)
			if(t.contains(item))
				cnt++;
		return cnt/database.size();
	}
	
	/**
	 * Returns the support of the given itemset wrt. the given database,
	 * i.e., the number of transactions of the database, which contain the
	 * given itemset, divided by the number of all transactions.
	 * @param itemset some set of objects
	 * @param database a database (set of sets of objects)
	 * @return the support of the itemset.
	 * @param <S> the type of objects
	 */
	public static <S extends Object> double support(Collection<S> itemset, Collection<Collection<S>> database){
		double cnt = 0;
		for(Collection<S> t: database)
			if(t.containsAll(itemset))
				cnt++;
		return cnt/database.size();
	}
	
	/**
	 * Returns the support of this rule wrt. the
	 * given database (a set of transactions), i.e.
	 * the support (frequency of occurrences) of the 
	 * union of premise and conclusion in the database.
	 * @param database some set of transactions.
	 * @return the support of this rule.
	 */
	public double support(Collection<Collection<T>> database){
		Collection<T> all = new HashSet<T>(this.premise);
		all.addAll(conclusion);
		return AssociationRule.support(all, database);		
	}
	
	/**
	 * Returns the confidence of this rule wrt. the
	 * given database (a set of transactions). More precisely,
	 * the support of this rule divided by the support of the
	 * premise alone.
	 * @param database some set of transactions.
	 * @return the confidence of the rule
	 */
	public double confidence(Collection<Collection<T>> database){
		return this.support(database)/AssociationRule.support(this.premise, database);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.premise + "->" + this.conclusion;
	}
}
