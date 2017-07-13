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
package net.sf.tweety.arg.prob.lotteries;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.ldo.syntax.LdoFormula;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.math.probability.Probability;

/**
 * This class implements an argumentation lottery based on the logic of dialectical outcomes,
 * i.e. a lottery on an exhaustive and disjoint set of formulas of LDO.
 * 
 * @author Matthias Thimm
 */
public class LdoArgumentationLottery {
	
	/** Maps LdoFormulas to probabilities */
	private Map<LdoFormula,Probability> prob;
	
	/** The semantics used for this lottery*/
	private Semantics semantics;
	
	/** The AAF used. */
	private DungTheory aaf;
	
	/**
	 * Creates a new lottery for the given set of formulas using the given
	 * probability function and semantics.
	 * @param formulas some set of formulas
	 * @param p some probability function 
	 * @param semantics some semantics
	 */
	public LdoArgumentationLottery(Collection<LdoFormula> formulas, SubgraphProbabilityFunction p, Semantics semantics){
		// TODO: check whether formulas are exhaustive and disjoint
		prob = new HashMap<LdoFormula,Probability>();
		for(LdoFormula f: formulas)
			this.prob.put(f, p.getAcceptanceProbability(f, semantics));
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
	public Collection<LdoFormula> getPossibleOutcomes(){
		return this.prob.keySet();
	}
	
	/**
	 * Returns the probability of the given outcome.
	 * @param d some division
	 * @return the probability of the given division.
	 */
	public Probability get(LdoFormula f){
		if(this.prob.containsKey(f))
			return this.prob.get(f);
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
		for(LdoFormula f: this.prob.keySet())
			if(first){
				s += this.prob.get(f).toString() + "," + f.toString();
				first = false;
			}else{
				s += " ; " + this.prob.get(f).toString() + "," + f.toString();
			}
				
		s += " ]";
		return s;
	}
}
