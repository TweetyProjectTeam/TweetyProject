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
package org.tweetyproject.arg.aba.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.Formula;

/**
 *
 * An argument derived from an ABA theory.
 *
 * @param <T> is the type of the language that the ABA theory's rules range over
 * @author Nils Geilen
 */
public class Deduction<T extends Formula> extends Argument {

	AbaRule<T> rule;
	Collection<Deduction<T>> subs = new HashSet<>();

	/**
	 * Constructs a new deduction.
	 *
	 * @param name an identifier
	 */
	public Deduction(String name) {
		super(name);
	}

	/**
	 * Constructs a new deduction.
	 *
	 * @param name an identifier
	 * @param rule the toprule
	 */
	public Deduction(String name, AbaRule<T> rule) {
		super(name);
		this.rule = rule;
	}

	/**
	 * Constructs a new deduction.
	 *
	 * @param name an identifier
	 * @param rule the toprule
	 * @param subs a set of subdeductions
	 */
	public Deduction(String name, AbaRule<T> rule, Collection<Deduction<T>> subs) {
		super(name);
		this.rule = rule;
		this.subs.addAll(subs);
	}

	/**
	 * Return all rules appearing in this argument.
	 * @return all rules appearing in this argument.
	 */
	public Collection<AbaRule<T>> getAllRules() {
		Collection<AbaRule<T>> result = new HashSet<>();
		result.add(this.rule);
		for (Deduction<T> sub : this.subs)
			result.addAll(sub.getAllRules());
		return result;
	}

	/**
	 * Return all conclusions appearing in this argument.
	 * @return all conclusions appearing in this argument.
	 */
	public Collection<T> getAllConclusions() {
		Collection<T> conc = new HashSet<>();
		for (AbaRule<T> rule : this.getAllRules())
			conc.add(rule.getConclusion());
		return conc;
	}

	/**
	 * Return the conclusion of this deduction
	 * @return the conclusion of this deduction
	 */
	public T getConclusion() {
		return rule.getConclusion();
	}

	/**
	 * Return the rule
	 * @return the rule
	 */

	public AbaRule<T> getRule() {
		return rule;
	}

	/**Set Rule
	 * @param rule the rule to set
	 */
	public void setRule(AbaRule<T> rule) {
		this.rule = rule;
	}

	/**
	 * Return all assumptions employed by this deduction
	 * @return all assumptions employed by this deduction
	 */
	public Collection<T> getAssumptions() {
		Collection<T> result = new LinkedList<>();
		if (rule.isAssumption())
			result.add(rule.getConclusion());
		else
			for (Deduction<T> sub : subs)
				result.addAll(sub.getAssumptions());
		return result;
	}

	/**
	 * Adds a subdeduction
	 *
	 * @param sub a deduction
	 */
	public void addSubDeduction(Deduction<T> sub) {
		subs.add(sub);
	}

	/**
	 * Return all rules used in this deduction
	 * @return all rules used in this deduction
	 */
	public Collection<AbaRule<T>> getRules() {
		Collection<AbaRule<T>> result = new LinkedList<>();
		if (!rule.isAssumption())
			result.add(rule);
		for (Deduction<T> sub : subs)
			result.addAll(sub.getRules());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.arg.dung.syntax.Argument#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((rule == null) ? 0 : rule.hashCode());
		result = prime * result + ((subs == null) ? 0 : subs.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.arg.dung.syntax.Argument#equals(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Deduction other = (Deduction) obj;
		if (rule == null) {
			if (other.rule != null)
				return false;
		} else if (!rule.equals(other.rule))
			return false;
		if (subs == null) {
			if (other.subs != null)
				return false;
		} else if (!subs.equals(other.subs))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.arg.dung.syntax.Argument#toString()
	 */
	@Override
	public String toString() {
		return "{rule=" + rule + ", subs=" + subs + "}";
	}

}
