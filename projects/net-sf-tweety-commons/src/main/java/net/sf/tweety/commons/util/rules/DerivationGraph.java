package net.sf.tweety.commons.util.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.util.Digraph;
import net.sf.tweety.commons.util.DigraphNode;

public class DerivationGraph<F1 extends Formula, F2 extends Formula, R extends Rule<F1,F2>> extends Digraph<R> {
	
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
			for(R r: open) {
				Collection<F1> params = (Collection<F1>)r.getPremise();
				List<List<DigraphNode<R>>> pre = new ArrayList<>();
				for(int i = 0 ; i < params.size() ; i++) {
					pre.set(i, new ArrayList<>());
					
					// ......
				}
			}
			
			if(finished)
				break;
		}
	}

}
