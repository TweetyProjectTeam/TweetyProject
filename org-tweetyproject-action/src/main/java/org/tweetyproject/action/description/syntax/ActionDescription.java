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

import org.tweetyproject.action.signature.ActionSignature;
import org.tweetyproject.commons.BeliefSet;

/**
 * This class represents an action description as a set of causal laws.
 * 
 * @author Sebastian Homann
 * @param <T> Type of causal law to be kept in this action description.
 */
public abstract class ActionDescription<T extends CausalLaw> extends BeliefSet<T, ActionSignature> {

	/**
	 * Creates a new empty action description.
	 */
	public ActionDescription() {
		super();
	}

	/**
	 * Creates a new action description containing all elements in the collection
	 * given.
	 * 
	 * @param c a collection of causal laws.
	 */
	public ActionDescription(Collection<? extends T> c) {
		super(c);
	}

	@Override
	protected ActionSignature instantiateSignature() {
		return new ActionSignature();
	}

}
