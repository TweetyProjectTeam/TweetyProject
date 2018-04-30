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
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.BeliefBaseReasoner;

/**
 * This reasoner provides is an implementation of the "Iterative Successive Subsitution Algorithm"
 * from [Marco Correia and Jorge Cruz and João Leite. On the Efficient Implementation of Social Abstract Argumentation. ECAI2014]
 * for determining a model of a social abstract argumentation framework according to the simple
 * product semantics.
 *  
 * @author Matthias Thimm
 *
 */
public class IssReasoner implements BeliefBaseReasoner<SocialAbstractArgumentationFramework>{

	/** The semantics used by this reasoner. */
	private SimpleProductSemantics semantics;
	
	/** The tolerance of the ISS algorithm. */
	private double tolerance;
	
	/**
	 * Creates a new reasoner.
	 * @param the simple product semantics used
	 * @param the tolerance of the ISS algorithm. 
	 */
	public IssReasoner(SimpleProductSemantics semantics, double tolerance) {
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
	 * @param saf a social abstract argumentation frameworks
	 * @return the social model computed by the ISS algorithm.
	 */
	public SocialMapping<Double> getSocialModel(SocialAbstractArgumentationFramework saaf){		
		SocialMapping<Double> mapping = new SocialMapping<Double>(this.semantics);
		for(Argument a: saaf)
			mapping.put(a, 0.5);
		SocialMapping<Double> newmapping = mapping;
		do{
			mapping = newmapping;
			newmapping = new SocialMapping<Double>(this.semantics);
			for(Argument a: saaf){
				double val = this.semantics.supp(saaf.getPositive(a), saaf.getNegative(a));
				for(Argument b: saaf.getAttackers(a)){
					if(newmapping.containsKey(b))
						val *= 1-newmapping.get(b);
					else
						val *= 1-mapping.get(b);
				}
				newmapping.put(a, val);
			}
		}while(this.dist(mapping, newmapping,saaf) > this.tolerance);
		return newmapping;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefBaseReasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(SocialAbstractArgumentationFramework saaf, Formula query) {
		Answer answer = new Answer(saaf, query);
		answer.setAnswer(this.getSocialModel(saaf).satisfies(query));
		answer.setAnswer(this.getSocialModel(saaf).get((Argument)query));
		return answer;
	}

}
