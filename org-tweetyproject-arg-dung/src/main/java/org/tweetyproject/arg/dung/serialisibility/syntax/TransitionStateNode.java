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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisibility.syntax;

import org.tweetyproject.arg.dung.serialisibility.*;
import org.tweetyproject.graphs.Node;


/**
 * This class represents a node, visualizing in a graph extension of a {@link TransitionState}.
 * 
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class TransitionStateNode implements Node {
	
	private TransitionState state;
	

	/**
	 * @param state TransitionState, which is to be represented by this node.
	 */
	public TransitionStateNode(TransitionState state) {
		super();
		this.state = state;
	}

	/**
	 * @return State represented by this node.
	 */
	public TransitionState getState() {
		return state;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.state.getExtension().toString();
	}

	public String getName() {
		return toString();
	}
	
	

}
