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
 *  Copyright 2017 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.simplelogicdeductive;

import java.util.Collection;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.simplelogicdeductive.syntax.SimplePlLogicArgument;
import net.sf.tweety.arg.simplelogicdeductive.syntax.SimplePlRule;
import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.DigraphNode;
import net.sf.tweety.commons.util.rules.DerivationGraph;
import net.sf.tweety.logics.pl.sat.Sat4jSolver;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * 
 * @author Federico Cerutti <federico.cerutti@acm.org>
 * 
 * According to 
 * http://www0.cs.ucl.ac.uk/staff/a.hunter/papers/ac13t.pdf
 * a simple logic knowledge base (propositional version only in this implementation) is 
 * a set of literals---in this implementation rules with empty body---and a set of 
 * simple rules, @see SimplePlRule
 * 
 */
public class SimplePlLogicDeductiveKnowledgebase extends BeliefSet<SimplePlRule> {
	
	public SimplePlLogicDeductiveKnowledgebase(){
		super();
	}

	public SimplePlLogicDeductiveKnowledgebase(Collection<SimplePlRule> _kb){
		super(_kb);
	}
	
	public Signature getSignature() {
		PropositionalSignature signature = new PropositionalSignature();
		for(SimplePlRule f: this)
			signature.addSignature(f.getSignature());
		return signature;
	}
	
	/**
	 * Builds simple logic arguments and attacks among them---simple undercut and 
	 * simple rebuttal---as described in http://www0.cs.ucl.ac.uk/staff/a.hunter/papers/ac13t.pdf
	 * 
	 * @return 	the DungTheory built on the simple logic knowledge base 
	 * 			following http://www0.cs.ucl.ac.uk/staff/a.hunter/papers/ac13t.pdf 
	 */
	public DungTheory getAF(){
		DungTheory af = new DungTheory();
		
		DerivationGraph<PropositionalFormula, SimplePlRule> rule_graph = new DerivationGraph<PropositionalFormula, SimplePlRule>();
		rule_graph.allDerivations(this);
		
		for (DigraphNode<SimplePlRule> node : rule_graph) {
			if (!node.getValue().isFact())
				af.add(new SimplePlLogicArgument(node));
		}
		
		for (Argument arga : af.getNodes()){
			for (Argument argb: af.getNodes()){
				SimplePlLogicArgument larga = (SimplePlLogicArgument)arga;
				SimplePlLogicArgument largb = (SimplePlLogicArgument)argb;
				
				Sat4jSolver solver = new Sat4jSolver();
				solver.getWitness((PropositionalFormula)(larga.getClaim()).combineWithAnd(largb.getClaim()));
				
				if (!solver.isConsistent((PropositionalFormula)(larga.getClaim()).combineWithAnd(largb.getClaim()))){
					af.add(new Attack(arga, argb));
				}
				
				for (SimplePlRule r : largb.getSupport()){
					for (PropositionalFormula p : r.getPremise()){
						if (!solver.isConsistent((PropositionalFormula)(larga.getClaim()).combineWithAnd(p))){
							af.add(new Attack(arga, argb));
						}
					}
				}
					
			}
		}
		
		return af;
		
	}
	
	
}
