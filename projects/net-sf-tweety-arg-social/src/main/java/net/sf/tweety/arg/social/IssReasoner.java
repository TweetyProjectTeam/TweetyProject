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
package net.sf.tweety.arg.social;

import java.util.Collection;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.social.semantics.SimpleProductSemantics;
import net.sf.tweety.arg.social.semantics.SocialMapping;
import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Reasoner;

/**
 * This reasoner provides is an implementation of the "Iterative Successive Subsitution Algorithm"
 * from [Marco Correia and Jorge Cruz and João Leite. On the Efficient Implementation of Social Abstract Argumentation. ECAI2014]
 * for determining a model of a social abstract argumentation framework according to the simple
 * product semantics.
 *  
 * @author Matthias Thimm
 *
 */
public class IssReasoner extends Reasoner{

	/** The semantics used by this reasoner. */
	private SimpleProductSemantics semantics;
	
	/** The mapping to be computed by this reasoner. */
	private SocialMapping<Double> mapping = null;
	
	/** The tolerance of the ISS algorithm. */
	private double tolerance;
	
	/**
	 * Creates a new reasoner for the given social abstract argumentation framework
	 * @param beliefBase a social abstract argumentation framework
	 * @param the simple product semantics used
	 * @param the tolerance of the ISS algorithm. 
	 */
	public IssReasoner(BeliefBase beliefBase, SimpleProductSemantics semantics, double tolerance) {
		super(beliefBase);
		if(!(beliefBase instanceof SocialAbstractArgumentationFramework))
			throw new IllegalArgumentException("Belief base of type 'SocialAbstractArgumentationFramework' expected.");
		this.semantics = semantics;
		this.tolerance = tolerance;
	}

	/**
	 * Returns the maximum-norm distance between the two social
	 * mappings
	 * @param sm1 some social mapping
	 * @param sm2 some social mapping
	 * @param args some arguments
	 * @return the maximum-norm distance between the two social
	 * mappings
	 */
	private double dist(SocialMapping<Double> sm1, SocialMapping<Double> sm2, Collection<Argument> args){
		double dist = 0;
		for(Argument a: args){
			if(Math.abs(sm1.get(a)-sm2.get(a)) > dist)
				dist = Math.abs(sm1.get(a)-sm2.get(a));
		}
		return dist;
	}
	
	/**
	 * Returns the social model computed by the ISS algorithm.
	 * @return the social model computed by the ISS algorithm.
	 */
	public SocialMapping<Double> getSocialModel(){
		if(this.mapping == null){
			SocialAbstractArgumentationFramework saf = (SocialAbstractArgumentationFramework) this.getKnowledgeBase();
			this.mapping = new SocialMapping<Double>(this.semantics);
			for(Argument a: saf)
				this.mapping.put(a, 0.5);
			SocialMapping<Double> newmapping = this.mapping;
			do{
				this.mapping = newmapping;
				newmapping = new SocialMapping<Double>(this.semantics);
				for(Argument a: saf){
					double val = this.semantics.supp(saf.getPositive(a), saf.getNegative(a));
					for(Argument b: saf.getAttackers(a)){
						if(newmapping.containsKey(b))
							val *= 1-newmapping.get(b);
						else
							val *= 1-this.mapping.get(b);
					}
					newmapping.put(a, val);
				}
			}while(this.dist(this.mapping, newmapping,saf) > this.tolerance);
		}
		return this.mapping;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Reasoner#query(net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(Formula query) {
		Answer answer = new Answer(this.getKnowledgeBase(), query);
		answer.setAnswer(this.getSocialModel().satisfies(query));
		answer.setAnswer(this.getSocialModel().get((Argument)query));
		return answer;
	}

}
