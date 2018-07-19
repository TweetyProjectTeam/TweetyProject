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
package net.sf.tweety.arg.dung.ldo.semantics;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.ldo.syntax.AbstractGraphLdoModality;
import net.sf.tweety.arg.dung.ldo.syntax.AbstractLdoModality;
import net.sf.tweety.arg.dung.ldo.syntax.LdoArgument;
import net.sf.tweety.arg.dung.ldo.syntax.LdoBoxModality;
import net.sf.tweety.arg.dung.ldo.syntax.LdoConjunction;
import net.sf.tweety.arg.dung.ldo.syntax.LdoDiamondModality;
import net.sf.tweety.arg.dung.ldo.syntax.LdoDisjunction;
import net.sf.tweety.arg.dung.ldo.syntax.LdoFormula;
import net.sf.tweety.arg.dung.ldo.syntax.LdoGraphBoxModality;
import net.sf.tweety.arg.dung.ldo.syntax.LdoGraphDiamondModality;
import net.sf.tweety.arg.dung.ldo.syntax.LdoNegation;
import net.sf.tweety.arg.dung.ldo.syntax.LdoRelation;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.commons.AbstractInterpretation;
import net.sf.tweety.graphs.Graph;

/**
 * This class models an LDO interpretation, i.e., a pair
 * of an argumentation framework and (possibly) an extension.
 * 
 * @author Matthias Thimm
 *
 */
public class LdoInterpretation extends AbstractInterpretation<DungTheory,LdoFormula> {

	/** The abstract argumentation framework */
	private DungTheory theory;
	/** the extension */
	private Extension ext = null;
	/** The used semantics */
	private Semantics sem;
	
	/**
	 * Creates a new interpretation
	 * @param theory an abstract argumentation framework 
	 * @param ext an extension (possibly null)
	 * @param semantics the used semantics (see net.sf.tweety.arg.dung.semantics.Semantics)
	 */
	public LdoInterpretation(DungTheory theory, Extension ext, Semantics semantics){
		this.theory = theory;
		this.ext = ext;
		this.sem = semantics;
	}
	
	/**
	 * Creates a new interpretation
	 * @param theory an abstract argumentation framework 
	 * @param semantics the used semantics (see net.sf.tweety.arg.dung.semantics.Semantics)
	 */
	public LdoInterpretation(DungTheory theory, Semantics semantics){
		this(theory, null, semantics);
	}
	
	@Override
	public boolean satisfies(LdoFormula formula) throws IllegalArgumentException {
		if(this.ext == null){
			AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(this.sem);
			for(Extension e: reasoner.getModels(this.theory)){
				LdoInterpretation i = new LdoInterpretation(this.theory, e, this.sem);
				if(!i.satisfies(formula))
					return false;
			}
			return true;
		}
		if(formula instanceof LdoArgument)
			return this.ext.contains(((LdoArgument)formula).getArgument());
		if(formula instanceof LdoNegation)
			return !this.satisfies(((LdoNegation)formula).getFormula());
		if(formula instanceof LdoConjunction){
			LdoConjunction c = (LdoConjunction) formula;
			for(LdoFormula f : c)
				if(!this.satisfies(f))
					return false;
			return true;
		}
		if(formula instanceof LdoDisjunction){
			LdoDisjunction d = (LdoDisjunction) formula;
			for(LdoFormula f: d)
				if(this.satisfies(f))
					return true;
			return false;
		}
		if(formula instanceof AbstractLdoModality){
			LdoFormula innerFormula = ((AbstractLdoModality)formula).getInnerFormula();
			if(formula instanceof AbstractGraphLdoModality){
				Set<LdoArgument> refLower = ((AbstractGraphLdoModality)formula).getLowerReferenceArguments();
				Set<LdoArgument> refUpper = ((AbstractGraphLdoModality)formula).getUpperReferenceArguments();
				Set<Argument> refArgsLower = new HashSet<Argument>();
				Set<Argument> refArgsUpper = new HashSet<Argument>();
				for(LdoArgument a: refLower)
					refArgsLower.add(a.getArgument());
				for(LdoArgument a: refUpper)
					refArgsUpper.add(a.getArgument());
				if(formula instanceof LdoGraphBoxModality){
					for(Graph<Argument> t: theory.getSubgraphs()){
						DungTheory th = new DungTheory(t);
						if(th.containsAll(refArgsLower) && refArgsUpper.containsAll(th)){
							LdoInterpretation i = new LdoInterpretation(th, this.ext, this.sem);
							if(!i.satisfies(innerFormula))
								return false;
						}
					}
					return true;
				}
				if(formula instanceof LdoGraphDiamondModality){
					for(Graph<Argument> t: theory.getSubgraphs()){
						DungTheory th = new DungTheory(t);
						if(th.containsAll(refArgsLower) && refArgsUpper.containsAll(th)){
							LdoInterpretation i = new LdoInterpretation(th, this.ext, this.sem);
							if(i.satisfies(innerFormula))
								return true;
						}
					}
					return false;
				}
				throw new IllegalArgumentException("Ldo formula " + formula + " is of unknown type.");
			}
			AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(this.sem);
			if(formula instanceof LdoBoxModality){
				for(Extension e: reasoner.getModels(this.theory)){
					LdoInterpretation i = new LdoInterpretation(this.theory, e, this.sem);
					if(!i.satisfies(innerFormula))
						return false;					
				}
				return true;
			}
			if(formula instanceof LdoDiamondModality){
				for(Extension e: reasoner.getModels(this.theory)){
					LdoInterpretation i = new LdoInterpretation(this.theory, e, this.sem);
					if(i.satisfies(innerFormula))
						return true;					
				}
				return false;
			}
			throw new IllegalArgumentException("Ldo formula " + formula + " is of unknown type.");
		}
		if(formula instanceof LdoRelation){
			LdoFormula left = ((LdoRelation)formula).getLeft();
			LdoFormula right = ((LdoRelation)formula).getRight();
			for(Graph<Argument> t: theory.getSubgraphs()){
				// check for completeness
				boolean complete = true;
				for(Attack a: theory.getAttacks()){
					if(t.contains(a.getAttacked()) && t.contains(a.getAttacker())){
						if(!t.areAdjacent(a.getAttacker(), a.getAttacked())){
							complete = false;
							break;
						}
					}
				}
				if(complete){				
					DungTheory th = new DungTheory(t);
					LdoInterpretation i = new LdoInterpretation(th,this.ext,this.sem);
					if(i.satisfies(left) && !i.satisfies(right))
						return false;
				}
			}
			return true;
		}
		throw new IllegalArgumentException("Ldo formula " + formula + " is of unknown type.");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Interpretation#satisfies(net.sf.tweety.commons.BeliefBase)
	 */
	@Override
	public boolean satisfies(DungTheory beliefBase)	throws IllegalArgumentException {
		throw new UnsupportedOperationException("Not supported.");
	}

}
