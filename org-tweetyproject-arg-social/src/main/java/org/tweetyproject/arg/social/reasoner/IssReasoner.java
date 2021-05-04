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
package org.tweetyproject.arg.social.reasoner;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.social.semantics.SimpleProductSemantics;
import org.tweetyproject.arg.social.semantics.SocialMapping;
import org.tweetyproject.arg.social.syntax.SocialAbstractArgumentationFramework;
import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.QuantitativeReasoner;

/**
 * This reasoner provides is an implementation of the "Iterative Successive Substitution Algorithm"
 * from [Marco Correia and Jorge Cruz and João Leite. On the Efficient Implementation of Social Abstract Argumentation. ECAI2014]
 * for determining a model of a social abstract argumentation framework according to the simple
 * product semantics.
 *  
 * @author Matthias Thimm
 *
 */
public class IssReasoner implements QuantitativeReasoner<SocialAbstractArgumentationFramework,Argument>, ModelProvider<Argument,SocialAbstractArgumentationFramework,SocialMapping<Double>> {

	/** The semantics used by this reasoner. */
	private SimpleProductSemantics semantics;
	
	/** The tolerance of the ISS algorithm. */
	private double tolerance;
	
	/**
	 * Creates a new reasoner.
	 * @param semantics the simple product semantics used
	 * @param tolerance the tolerance of the ISS algorithm. 
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
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.Reasoner#query(org.tweetyproject.commons.BeliefBase, org.tweetyproject.commons.Formula)
	 */
	@Override
	public Double query(SocialAbstractArgumentationFramework beliefbase, Argument formula) {		
		return this.getModel(beliefbase).get(formula);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.ModelProvider#getModels(org.tweetyproject.commons.BeliefBase)
	 */
	@Override
	public Collection<SocialMapping<Double>> getModels(SocialAbstractArgumentationFramework bbase) {
		Collection<SocialMapping<Double>> models = new HashSet<SocialMapping<Double>>();
		models.add(this.getModel(bbase));
		return models;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.ModelProvider#getModel(org.tweetyproject.commons.BeliefBase)
	 */
	@Override
	public SocialMapping<Double> getModel(SocialAbstractArgumentationFramework bbase) {
		SocialMapping<Double> mapping = new SocialMapping<Double>(this.semantics);
		for(Argument a: bbase)
			mapping.put(a, 0.5);
		SocialMapping<Double> newmapping = mapping;
		do{
			mapping = newmapping;
			newmapping = new SocialMapping<Double>(this.semantics);
			for(Argument a: bbase){
				double val = this.semantics.supp(bbase.getPositive(a), bbase.getNegative(a));
				for(Argument b: bbase.getAttackers(a)){
					if(newmapping.containsKey(b))
						val *= 1-newmapping.get(b);
					else
						val *= 1-mapping.get(b);
				}
				newmapping.put(a, val);
			}
		}while(this.dist(mapping, newmapping,bbase) > this.tolerance);
		return newmapping;
	}
}
