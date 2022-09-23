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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.machinelearning.rl.mdp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A fixed policy for MDPs, i.e., a simple map from states to actions.
 * @author Matthias Thimm
 *
 *
 * @param <S> The type of states
 * @param <A> The type of actions
 */
public class FixedPolicy<S extends State, A extends Action> implements Policy<S,A>{
	private Map<S,A> map;
	
	/**
	 * Creates a new fixed policy
	 */
	public FixedPolicy() {
		this.map = new HashMap<>();
	}
	
	@Override
	public A execute(S s) {
		return this.map.get(s);
	}
	
	/**
	 * Checks whether this policy is well-formed, i.e. whether every state in 
	 * the given set of states is mapped to some action.
	 * @param states some set of states
	 * @return "true" iff this policy is well-formed
	 */
	public boolean isWellFormed(Collection<S> states) {
		for(S s: states)
			if(!this.map.containsKey(s))
				return false;
		return true;
	}
	
	/**
	 * Sets the action for the given state.
	 * @param s some state
	 * @param a some action
	 */
	public void set(S s, A a) {
		this.map.put(s, a);
	}
	
	@Override
	public String toString() {
		return this.map.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(map);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FixedPolicy other = (FixedPolicy) obj;
		return Objects.equals(map, other.map);
	}	
}
