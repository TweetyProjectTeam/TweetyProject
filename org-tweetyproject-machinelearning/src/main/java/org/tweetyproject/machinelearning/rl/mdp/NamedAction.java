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

import java.util.Objects;

/**
 * An action in a Markov Decision Process that is solely characterized
 * through its name.
 * 
 * @author Matthias Thimm
 *
 */
public class NamedAction implements Action{
	/** The name of this action. */
	private String name;
	
	/**
	 * Creates a new NamedAction.
	 * @param name some name
	 */
	public NamedAction(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the name of this action
	 * @return the name of this action
	 */
	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.name;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NamedAction other = (NamedAction) obj;
		return Objects.equals(name, other.name);
	}
}
