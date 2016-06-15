package net.sf.tweety.commons.util.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.util.Digraph;
import net.sf.tweety.commons.util.DigraphNode;

public class DerivationGraph<F extends Formula, R extends Rule<F,F>> extends Digraph<R> {
	
	public DerivationGraph() {
		
	}
	
	public void allDerivations (Collection<R> rules) {
		Collection<R> open = new ArrayList<>();
		open.addAll(rules);
		for(R r:open) 
			if (r.isFact())
				this.addNode(r);
		open.removeAll(getValues());
		System.out.println(open);
		
		while(true) {
			boolean finished = true;
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
				
				
				
				/*for(int i = 0 ; i < params.size() ; i++) {
					System.out.println(params.get(i));
					List<DigraphNode<R>> l = pre.get(i);
					for(int j = 0 ; j < l.size() ; j++) {
						System.out.println(l.get(j).getValue());
					}
				}
				System.out.println("");*/
			}
			
			if(finished)
				break;
		}
	}

}
