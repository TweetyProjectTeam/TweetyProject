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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.petri.syntax;

import org.tweetyproject.graphs.Node;

/**
 * An abstract class for the two types of nodes in a Petri net, places and transitions
 * @author Benedikt Knopp
 */
public abstract class PetriNetNode implements Node {

	/**
	 * a unique identifier
	 */
	String id;
	/**
	 * a pretty name
	 */
	String name;

	/**
	 * Create a new instance
	 * @param id a unique identifier
	 */
	public PetriNetNode(String id) {
		this.id = id;
	}

	/**
	 * Create a new instance
	 * @param id a unique identifier
	 * @param name a pretty name
	 */
	public PetriNetNode(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Teturn the id
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Setter id
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Return the name
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter name
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 *
	 * Return true iff this node is final
	 * @return true iff this node is final
	 */
	protected abstract boolean isFinal();

}
