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
 * This class implements a simple categorizer that does
 * an bottom-up marking of the tree; leafs are marked
 * undefeated and an inner node is marked defeated
 * if at least one child is marked undefeated and
 * defeated otherwise.<br>
 * <br>
 * The method "categorize" returns "1" if the root node is
 * undefeated and "0" if it is defeated.
 * 
 * @author Matthias Thimm
 */
public class ClassicalCategorizer implements Categorizer {

	/* (non-Javadoc)
	 * @see org.tweetyproject.argumentation.deductive.categorizer.Categorizer#categorize(org.tweetyproject.argumentation.deductive.semantics.ArgumentTree)
	 */
	@Override
	public double categorize(ArgumentTree argumentTree) {
		return this.categorize(argumentTree, null, argumentTree.getRoot());
	}

	/**
	 * Performs the bottom-up search.
	 * @param argumentTree some argument tree.
	 * @param parent the parent of the current node.
	 * @param node the current node.
	 * @return "1" if node is undefeated, "0" otherwise.
	 */
	private double categorize(ArgumentTree argumentTree, DeductiveArgumentNode parent, DeductiveArgumentNode node){
		Collection<DeductiveArgumentNode> children = argumentTree.getNeighbors(node);
		if(parent != null) children.remove(parent);
		if(children.isEmpty())
			return 1;
		for(DeductiveArgumentNode child: children)
			if(this.categorize(argumentTree, node, child) != 0)
				return 0;
		return 1;
	}

}
