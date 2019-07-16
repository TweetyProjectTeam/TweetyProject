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
package net.sf.tweety.arg.deductive.semantics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import net.sf.tweety.arg.deductive.syntax.DeductiveKnowledgeBase;
import net.sf.tweety.graphs.*;
import net.sf.tweety.logics.pl.sat.PlMusEnumerator;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * Instances of this class are compilations in the sense of
 * Definition 8 in<br/>
 * <br/>
 * Philippe Besnard and Anthony Hunter. Knowledgebase Compilation for Efficient Logical Argumentation.
 * In Proceedings of the 10th International Conference on Knowledge Representation (KR'06), pages
 * 123-133, AAAI Press, 2006.<br/>
 * <br/>
 * A compilation of a knowledge base is a graph where the nodes
 * are the minimal inconsistent subsets of the knowledge base
 * and the edges connect sets that have a non-empty intersection.
 * 
 * @author Matthias Thimm
 *
 */
public class Compilation extends DefaultGraph<CompilationNode>{

	/** Creates the compilation of the given knowledge base.
	 * @param kb some deductive knowledge base.
	 */
	public Compilation(DeductiveKnowledgeBase kb){		
		Collection<Collection<PlFormula>> minInconSets = PlMusEnumerator.getDefaultEnumerator().minimalInconsistentSubsets(kb);
		for(Collection<PlFormula> set: minInconSets)
			this.add(new CompilationNode(set));
		Stack<CompilationNode> stackNodes = new Stack<CompilationNode>();
		stackNodes.addAll(this.getNodes());
		while(!stackNodes.isEmpty()){
			CompilationNode nodeA = stackNodes.pop();
			if(stackNodes.isEmpty())
				break;
			for(CompilationNode nodeB: stackNodes){
				PlBeliefSet temp = new PlBeliefSet(nodeA);
				temp.retainAll(nodeB);
				if(!temp.isEmpty())
					this.add(new UndirectedEdge<CompilationNode>(nodeA,nodeB));
			}
		}
	}
	
	/**
	 * Returns the argument tree for the given argument.
	 * @param arg some deductive argument.
	 * @return the argument tree for the given argument.
	 */
	public ArgumentTree getArgumentTree(DeductiveArgument arg){
		DeductiveArgumentNode argNode = new DeductiveArgumentNode(arg);
		ArgumentTree argTree = new ArgumentTree(argNode);
		argTree.add(argNode);
		Set<CompilationNode> firstLevelNodes = this.firstLevel(arg);
		for(CompilationNode node: firstLevelNodes){
			Set<PlFormula> support = new HashSet<PlFormula>(node);
			support.removeAll(arg.getSupport());
			DeductiveArgument undercut = new DeductiveArgument(support,new Negation(new Conjunction(arg.getSupport())));
			DeductiveArgumentNode undercutNode = new DeductiveArgumentNode(undercut); 
			argTree.add(undercutNode);
			argTree.add(new DirectedEdge<DeductiveArgumentNode>(undercutNode,argNode));
			Set<CompilationNode> remainingNodes = new HashSet<CompilationNode>(this.getNodes());
			remainingNodes.remove(node);
			this.subcuts(undercutNode, remainingNodes, node, new HashSet<PlFormula>(arg.getSupport()), argTree);
		}
		return argTree;
	}
	
	/**
	 * This method returns the compilation nodes that can be used
	 * to construct undercuts to the given argument.
	 * @param arg some argument.
	 * @return a set of compilation nodes.
	 */
	private Set<CompilationNode> firstLevel(DeductiveArgument arg){
		Stack<CompilationNode> candidates = new Stack<CompilationNode>();
		for(CompilationNode node: this){
			Set<PlFormula> set = new HashSet<PlFormula>(node);
			set.retainAll(arg.getSupport());
			if(!set.isEmpty())
				candidates.add(node);
		}
		Set<CompilationNode> result = new HashSet<CompilationNode>();
		while(!candidates.isEmpty()){
			CompilationNode node = candidates.pop();
			boolean addToResult = true;
			for(CompilationNode node2: candidates){
				Set<PlFormula> set1 = new HashSet<PlFormula>(node);
				Set<PlFormula> set2 = new HashSet<PlFormula>(node2);
				set1.removeAll(arg.getSupport());
				set2.removeAll(arg.getSupport());
				if(set2.containsAll(set1)){
					addToResult = false;
					break;
				}
			}
			if(addToResult)
				for(CompilationNode node2: result){
					Set<PlFormula> set1 = new HashSet<PlFormula>(node);
					Set<PlFormula> set2 = new HashSet<PlFormula>(node2);
					set1.removeAll(arg.getSupport());
					set2.removeAll(arg.getSupport());
					if(set2.containsAll(set1)){
						addToResult = false;
						break;
					}
				}
			if(addToResult)
				result.add(node);
		}
		return result;
	}
	
	/**
	 * This method recursively builds up the argument tree from
	 * the given argument.
	 * @param argNode an argument.
	 * @param remainingNodes the non-visited nodes in the compilation.
	 * @param current the current node.
	 * @param currentSupport the union of the supports of the current path.
	 * @param argTree the argument tree.
	 */
	private void subcuts(DeductiveArgumentNode argNode, Set<CompilationNode> remainingNodes, CompilationNode current, Set<PlFormula> currentSupport, ArgumentTree argTree){
		for(CompilationNode node: remainingNodes){
			UndirectedEdge<CompilationNode> edge = new UndirectedEdge<CompilationNode>(current,node);
			if(this.contains(edge)){
				if(!currentSupport.containsAll(node)){
					Set<PlFormula> set = new HashSet<PlFormula>(argNode.getSupport());
					set.retainAll(node);
					if(!set.isEmpty()){
						boolean properUndercut = true;
						for(Edge<CompilationNode> edge2: this.getEdges()){
							if(!edge2.equals(edge) && (edge2.getNodeA() == current || edge2.getNodeB() == current)){
								Set<PlFormula> set1 = new HashSet<PlFormula>(node);
								Set<PlFormula> set2;
								if(edge2.getNodeA() == current)
									set2 = new  HashSet<PlFormula>(edge2.getNodeB());
								else set2 = new  HashSet<PlFormula>(edge2.getNodeA());
								set1.retainAll(argNode.getSupport());
								set2.retainAll(argNode.getSupport());
								if(set1.containsAll(set2)){
									properUndercut = false;
									break;
								}
							}
						}
						if(properUndercut){
							Set<PlFormula> support = new HashSet<PlFormula>(node);
							support.removeAll(argNode.getSupport());
							DeductiveArgument undercut = new DeductiveArgument(support,new Negation(new Conjunction(argNode.getSupport())));
							DeductiveArgumentNode undercutNode = new DeductiveArgumentNode(undercut); 
							argTree.add(undercutNode);
							argTree.add(new DirectedEdge<DeductiveArgumentNode>(undercutNode,argNode));
							Set<CompilationNode> newRemainingNodes = new HashSet<CompilationNode>(remainingNodes);
							newRemainingNodes.remove(node);
							Set<PlFormula> newSupport = new HashSet<PlFormula>(support);
							newSupport.addAll(undercut.getSupport());
							this.subcuts(undercutNode, newRemainingNodes, node, newSupport, argTree);
						}
					}
				}
			}
		}
	}
}
