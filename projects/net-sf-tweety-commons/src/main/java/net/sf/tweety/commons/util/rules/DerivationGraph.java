/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
 package net.sf.tweety.commons.util.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.util.Digraph;
import net.sf.tweety.commons.util.DigraphNode;

/**
 * 
 * @author Nils Geilen
 * expands a set of formulae to a graph where nodes are formulae and edges show, which forumla can be composed of which formulae
 * @param <F> a formula type
 * @param <R> a rule type F^n -> F
 */


public class DerivationGraph<F extends Formula, R extends Rule<F,F>> extends Digraph<R> {
	
	public DerivationGraph() {
		super(false);
	}
	
	/**
	 * expands the rule set 
	 * @param rules a collection of rules
	 */
	public void allDerivations (Collection<R> rules) {
		Collection<R> open = new ArrayList<>();
		open.addAll(rules);
		for(R r:open) 
			if (r.isFact())
				this.addNode(r);
		open.removeAll(getValues());
		
		
		while(true) {
			int noe = this.numberOfEdges();
			rule_loop: for(R r: open) {
				List<F> params = new ArrayList<>();
				params.addAll(r.getPremise());
				List<List<DigraphNode<R>>> pre = new ArrayList<>();
				for(int i = 0 ; i < params.size() ; i++) {
					pre.add(new ArrayList<>());
					F param = params.get(i);
					for(DigraphNode<R> n:this)
						if(n.getValue().getConclusion().equals(param))
							pre.get(i).add(n);
				}
				
				for(List<DigraphNode<R>> l:pre) {
					if(l.isEmpty())
						continue rule_loop;
				}
				
				
				Digraph<DigraphNode<R>> param_tree = new Digraph<>(false);
				param_tree.addNode(null);
				for(List<DigraphNode<R>> l:pre) {
					Collection<DigraphNode<DigraphNode<R>>> leafs = param_tree.getLeafs();
					for(DigraphNode<DigraphNode<R>> leaf:leafs)
						for(DigraphNode<R> n:l){
							leaf.addEdge(param_tree.addNode(n));
						}
				}
				
				
				for(DigraphNode<DigraphNode<R>> leaf: param_tree.getLeafs()) {
					
					List<DigraphNode<R>> path = new ArrayList<>();
					do {
						path.add(leaf.getValue());
						leaf = leaf.getParent();
					} while (! leaf.isRoot());
					
					if(containsNode(r, path))
						continue;
					
					DigraphNode<R> new_node = this.addNode(r);
					for(DigraphNode<R> node: path)
						node.addEdge(new_node);
					
				}
				
			}
			
			//printTrees(System.out);
			
			//System.out.println(size());
			//System.out.println(numberOfEdges());
			if(noe == this.numberOfEdges())
				break;
		}
	}
	
	private boolean containsNode(R value, List<DigraphNode<R>> parents) {
		next_node : for(DigraphNode<R> node:this) {
			if(node.getValue().equals(value)) {
				for(DigraphNode<R> n:parents) {
					if(!node.getParents().contains(n))
						continue next_node;
				}
				return true;
			}
		}
		return false;
	}

}
