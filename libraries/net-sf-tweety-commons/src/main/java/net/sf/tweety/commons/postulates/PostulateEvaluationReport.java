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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.commons.postulates;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.tweety.commons.Formula;

/**
 * Summarises the results of a postulate evaluation.
 * 
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas
 */
public class PostulateEvaluationReport<S extends Formula> {

	/**
	 * The approach that is evaluated
	 */
	private PostulateEvaluatable<S> ev;

	/**
	 * the instances that satisfy the given postulate (and are applicable)
	 */
	private Map<Postulate<S>, Collection<Collection<S>>> positiveInstances;

	/**
	 * the instances that are not applicable for the given postulate (thus also
	 * satisfy the postulate)
	 */
	private Map<Postulate<S>, Collection<Collection<S>>> notApplicableInstances;

	/**
	 * the instances that violate the given postulate
	 */
	private Map<Postulate<S>, Collection<Collection<S>>> negativeInstances;

	/**
	 * Creates a new evaluation report for the given approach and set of postulates
	 * 
	 * @param ev         some approach
	 * @param postulates a set of postulates
	 */
	public PostulateEvaluationReport(PostulateEvaluatable<S> ev, List<Postulate<S>> postulates) {
		this.ev = ev;
		this.positiveInstances = new HashMap<Postulate<S>, Collection<Collection<S>>>();
		this.negativeInstances = new HashMap<Postulate<S>, Collection<Collection<S>>>();
		this.notApplicableInstances = new HashMap<Postulate<S>, Collection<Collection<S>>>();
		for (Postulate<S> p : postulates) {
			this.positiveInstances.put(p, new LinkedList<Collection<S>>());
			this.negativeInstances.put(p, new LinkedList<Collection<S>>());
			this.notApplicableInstances.put(p, new LinkedList<Collection<S>>());
		}
	}

	/**
	 * Adds a positive instance for the given postulate (that is applicable)
	 * 
	 * @param postulate some postulate
	 * @param instance  some instance
	 */
	public void addPositiveInstance(Postulate<S> postulate, Collection<S> instance) {
		this.positiveInstances.get(postulate).add(instance);
	}

	/**
	 * Adds an instance that is not applicable for the given postulate
	 * 
	 * @param postulate some postulate
	 * @param instance  some instance
	 */
	public void addNotApplicableInstance(Postulate<S> postulate, Collection<S> instance) {
		this.notApplicableInstances.get(postulate).add(instance);
	}

	/**
	 * Adds a negative instance for the given postulate
	 * 
	 * @param postulate some postulate
	 * @param instance  some instance
	 */
	public void addNegativeInstance(Postulate<S> postulate, Collection<S> instance) {
		this.negativeInstances.get(postulate).add(instance);
	}

	/**
	 * Returns the negative instances for the given postulate
	 * @param postulate some postulate
	 * @return a collection of negative instances.
	 */
	public Collection<Collection<S>> getNegativeInstances(Postulate<S> postulate){
		if(this.negativeInstances.containsKey(postulate))
			return this.negativeInstances.get(postulate);
		return new HashSet<Collection<S>>();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		int longest = 10;
		for (Postulate<S> p : this.positiveInstances.keySet())
			if (p.getName().length() > longest)
				longest = p.getName().length();
		longest++;

		String result = this.ev.getClass().getSimpleName() + " RESULTS\n----------\n" + String.format("%-" + longest + "s%-13s%-14s%s",
				"Postulate ", "posInstances ", "notApplicable ", "negInstances\n");
		for (Postulate<S> p : this.positiveInstances.keySet()) {
			result += String.format("%-" + longest + "s%-13s%-14s%s", p.getName() + " ",
					this.positiveInstances.get(p).size(), this.notApplicableInstances.get(p).size(),
					this.negativeInstances.get(p).size()) + "\n";
		}

		return result;
	}
}
