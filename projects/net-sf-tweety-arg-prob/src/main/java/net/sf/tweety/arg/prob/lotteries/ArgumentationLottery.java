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
package net.sf.tweety.arg.prob.lotteries;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.divisions.Division;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.math.probability.Probability;

/**
 * This class implements an argumentation lottery, i.e. a lottery on an exhaustive and
 * disjoint set of divisions.
 * 
 * @author Matthias Thimm
 */
public class ArgumentationLottery {

	/** Maps divisions to probabilities */
	private Map<Division,Probability> prob;
	
	/** The semantics used for this lottery*/
	private Semantics semantics;
	
	/** The AAF used. */
	private DungTheory aaf;
	
	/**
	 * Creates a new lottery for the given set of divisions using the given
	 * probability function and semantics.
	 * @param divisions some set of divisions
	 * @param p some probability function 
	 * @param semantics some semantics
	 */
	public ArgumentationLottery(Collection<Division> divisions, SubgraphProbabilityFunction p, Semantics semantics){
		// check whether divisions is exhaustive and disjoint (disabled for now)
		//if(!Division.isDisjoint(divisions, p.getTheory(), semantics) || !Division.isExhaustive(divisions, p.getTheory(), semantics))
		//	throw new IllegalArgumentException("Given set of divisions is not exhaustive and disjoint.");
		prob = new HashMap<Division,Probability>();		
		for(Division d: divisions)
			this.prob.put(d, p.getAcceptanceProbability(d, semantics));		 
		
		this.semantics = semantics;
		this.aaf = p.getTheory();
	}
	
	/**
	 * Returns the used semantics.
	 * @return the used semantics.
	 */
	public Semantics getSemantics(){
		return this.semantics;
	}
	
	/**
	 * Returns the set of possible outcomes.
	 * @return the set of possible outcomes.
	 */
	public Collection<Division> getPossibleOutcomes(){
		return this.prob.keySet();
	}
	
	/**
	 * Returns the probability of the given outcome.
	 * @param d some division
	 * @return the probability of the given division.
	 */
	public Probability get(Division d){
		if(this.prob.containsKey(d))
			return this.prob.get(d);
		return new Probability(0d);
	}
	
	/**
	 * Returns the used argumentation theory.
	 * @return the used argumentation theory.
	 */
	public DungTheory getTheory(){
		return this.aaf;
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String s = "[ ";
		boolean first = true;
		for(Division d: this.prob.keySet())
			if(first){
				s += this.prob.get(d).toString() + "," + d.toString();
				first = false;
			}else{
				s += " ; " + this.prob.get(d).toString() + "," + d.toString();
			}
				
		s += " ]";
		return s;
	}
}
