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
package org.tweetyproject.arg.deductive.categorizer;

import java.util.Collection;

import org.tweetyproject.arg.deductive.semantics.ArgumentTree;
import org.tweetyproject.arg.deductive.semantics.DeductiveArgumentNode;

/**
 * This class implements the h-categorizer from<br>
 * <br>
 * Philippe Besnard and Anthony Hunter. A logic-based theory of deductive arguments.
 * In Artificial Intelligence, 128(1-2):203-235, 2001.
 *
 * @author Matthias Thimm
 */
public class HCategorizer implements Categorizer{
	/**
	 * Defautl Constructor
	 */
	public HCategorizer(){
		//default
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.argumentation.deductive.categorizer.Categorizer#categorize(org.tweetyproject.argumentation.deductive.semantics.ArgumentTree)
	 */
	@Override
	public double categorize(ArgumentTree argumentTree) {
		return this.categorize(argumentTree,null,argumentTree.getRoot());
	}

	/**
	 * Categorizes the node in the given tree.
	 * @param argumentTree some argument tree.
	 * @param parent the parent of the current node.
	 * @param node some node.
	 * @return the categorization of the node.
	 */
	private double categorize(ArgumentTree argumentTree, DeductiveArgumentNode parent, DeductiveArgumentNode node){
		Collection<DeductiveArgumentNode> children = argumentTree.getNeighbors(node);
		if(parent != null) children.remove(parent);
		if(children.isEmpty())
			return 1;
		double sumChildren = 1;
		for(DeductiveArgumentNode child: children)
			sumChildren += this.categorize(argumentTree, node, child);
		return 1d/sumChildren;
	}

}
