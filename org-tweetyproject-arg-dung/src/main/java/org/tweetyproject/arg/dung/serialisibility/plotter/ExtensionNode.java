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
package org.tweetyproject.arg.dung.serialisibility.plotter;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.Node;


/**
 * This class represents a node, representing a extension of an argumentation framework.
 * 
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class ExtensionNode implements Node {
	
	private Extension<DungTheory> extension;

	/**
	 * @param extension Collection of extensions, which are to be represented by this node.
	 */
	public ExtensionNode(Extension<DungTheory> extension) {
		super();
		this.extension = extension;
	}

	/**
	 * @return extension, which is to be represented by this node.
	 */
	public Extension<DungTheory> getExtension() {
		return extension;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.extension.toString();
	}
	
	

}
