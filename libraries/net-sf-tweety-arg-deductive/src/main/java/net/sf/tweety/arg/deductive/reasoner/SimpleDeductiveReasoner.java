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
package net.sf.tweety.arg.deductive.reasoner;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.arg.deductive.accumulator.Accumulator;
import net.sf.tweety.arg.deductive.categorizer.Categorizer;
import net.sf.tweety.arg.deductive.semantics.ArgumentTree;
import net.sf.tweety.arg.deductive.semantics.DeductiveArgument;
import net.sf.tweety.arg.deductive.semantics.DeductiveArgumentNode;
import net.sf.tweety.arg.deductive.syntax.DeductiveKnowledgeBase;
import net.sf.tweety.graphs.DirectedEdge;
import net.sf.tweety.graphs.Edge;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * This class implements a brute force approach to deductive argumentation.
 * 
 * It performs deductive argumentation on a set of propositional formulas. 
 * 
 * @author Matthias Thimm
 */
public class SimpleDeductiveReasoner extends AbstractDeductiveArgumentationReasoner {

	/** Creates a new reasoner for the given belief base,
	 * categorizer, and accumulator.
	 * @param categorizer some categorizer.
	 * @param accumulator some accumulator.
	 */
	public SimpleDeductiveReasoner(Categorizer categorizer, Accumulator accumulator) {
		super(categorizer, accumulator);		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.deductive.AbstractDeductiveArgumentationReasoner#getArgumentTree(net.sf.tweety.arg.deductive.DeductiveKnowledgeBase, net.sf.tweety.arg.deductive.semantics.DeductiveArgument)
	 */
	@Override
	protected ArgumentTree getArgumentTree(DeductiveKnowledgeBase kb, DeductiveArgument arg) {
		return this.getArgumentTree(kb, new DeductiveArgumentNode(arg), new HashSet<PlFormula>());
	}
	
	/**
	 * Computes the argument tree of the given argument.
	 * 
	 * @param kb the knowledge base
	 * @param argNode some argument node.
	 * @param support the union of the supports of all previously encountered arguments
	 * @return the argument tree for the argument
	 */
	private ArgumentTree getArgumentTree(DeductiveKnowledgeBase kb, DeductiveArgumentNode argNode, Set<PlFormula> support) {
		support.addAll(argNode.getSupport());	 
		// 1.) collect all possible undercuts 
		PlFormula claim = new Negation(new Conjunction(argNode.getSupport()));
		Set<DeductiveArgument> possibleUndercuts = kb.getDeductiveArguments(claim);
		// 2.) remove all undercuts that violate non-inclusion in previous supports
		Set<DeductiveArgument> undercuts = new HashSet<DeductiveArgument>();
		for(DeductiveArgument undercut: possibleUndercuts){
			Set<PlFormula> sup = new HashSet<PlFormula>(undercut.getSupport());
			sup.removeAll(support);
			if(!sup.isEmpty())
				undercuts.add(undercut);
		}
		// 3.) create argument tree recursively
		ArgumentTree argTree = new ArgumentTree(argNode);
		argTree.add(argNode);
		for(DeductiveArgument undercut: undercuts){
			DeductiveArgumentNode undercutNode = new DeductiveArgumentNode(undercut);
			ArgumentTree subTree = this.getArgumentTree(kb, undercutNode, new HashSet<PlFormula>(support));
			for(DeductiveArgumentNode node: subTree)
				argTree.add(node);
			for(Edge<DeductiveArgumentNode> edge: subTree.getEdges())
				argTree.add(edge);
			argTree.add(new DirectedEdge<DeductiveArgumentNode>(undercutNode, argNode));
		}
		return argTree;
	}
}
