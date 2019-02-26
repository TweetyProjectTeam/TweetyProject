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
package net.sf.tweety.beliefdynamics.mas;

import java.util.*;

import net.sf.tweety.agents.*;
import net.sf.tweety.arg.deductive.categorizer.*;
import net.sf.tweety.arg.deductive.semantics.*;
import net.sf.tweety.graphs.orders.*;
import net.sf.tweety.logics.pl.syntax.*;

/**
 * This class implements a credibility-based categorizer that works like
 * the classical categorizer but dismisses arguments where
 * the least credible agent which uttered a formula in that argument
 * is not as least as credible as the least credible agent which uttered
 * a formula of the parent argument.
 *   
 * @author Matthias Thimm
 */
public class CredibilityCategorizer extends AbstractCredibilityComparer implements Categorizer {
	
	/**
	 * Creates a new credibility categorizer that is guided by the giving information which
	 * agents uttered the formulas and the credibility order. 
	 * @param formulas The information objects that hold the information which agents
	 * 		uttered the formulas.
	 * @param credOrder The credibility order used to guide the categorizing.
	 */
	public CredibilityCategorizer(Collection<InformationObject<PlFormula>> formulas, Order<Agent> credOrder){
		super(formulas,credOrder);		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.deductive.categorizer.Categorizer#categorize(net.sf.tweety.argumentation.deductive.semantics.ArgumentTree)
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
		for(DeductiveArgumentNode child: children){
			if(this.isAtLeastAsPreferredAs(child.getSupport(), node.getSupport()))
				if(this.categorize(argumentTree, node, child) != 0)
					return 0;
		}
		return 1;
	}
}
