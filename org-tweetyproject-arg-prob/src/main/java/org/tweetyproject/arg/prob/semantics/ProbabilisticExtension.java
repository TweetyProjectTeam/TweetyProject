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
package org.tweetyproject.arg.prob.semantics;

import org.tweetyproject.arg.dung.semantics.AbstractArgumentationInterpretation;
import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Labeling;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.math.probability.*;

/**
 * This class implements a probabilistic interpretation for Dung argumentation frameworks, cf. [Thimm:2012].
 * 
 * @author Matthias Thimm
 */
public class ProbabilisticExtension extends ProbabilityFunction<Extension<DungTheory>>{

	/**
	 * Creates a new probabilistic extension for the given theory.
	 */
	public ProbabilisticExtension(){
		super();
	}
	
	/**
	 * Computes the probability of the given argument.
	 * @param a some argument
	 * @return the probability of the argument.
	 */
	public Probability probability(Argument a){
		Probability p = new Probability(0d);
		for(Extension<DungTheory> e: this.keySet())
			if(e.contains(a))
				p = p.add(this.probability(e));
		return p;
	} 
	
	/**
	 * Returns the set of all arguments appearing.
	 * @return the set of all arguments appearing.
	 */
	public Extension<DungTheory> getAllArguments(){
		Extension<DungTheory> e = new Extension<DungTheory>();
		for(Extension<DungTheory> ex: this.keySet())
			e.addAll(ex);
		return e;
	}
	
	/** Returns the upper cut of this probabilistic extension wrt. delta, i.e.
	 * all arguments that have probability &gt;= delta.
	 * @param theory a Dung theory.
	 * @param delta some threshold.
	 * @return the set of arguments that have probability &gt;= delta.
	 */
	public Extension<DungTheory> getUpperCut(DungTheory theory, double delta){
		Extension<DungTheory> e = new Extension<DungTheory>();
		for(Argument arg: theory)
			if(this.probability(arg).doubleValue() >= delta)
				e.add(arg);
		return e;
	}
	
	/** Returns the lower cut of this probabilistic extension wrt. delta, i.e.
	 * all arguments that have probability &lt;= delta.
	 * @param theory a Dung theory.
	 * @param delta some threshold.
	 * @return the set of arguments that have probability &lt;= delta.
	 */
	public Extension<DungTheory> geLowerCut(DungTheory theory, double delta){
		Extension<DungTheory> e = new Extension<DungTheory>();
		for(Argument arg: theory)
			if(this.probability(arg).doubleValue() <= delta)
				e.add(arg);
		return e;
	}
	
	/**
	 * Checks whether the given labeling is congruent to this probabilistic extension,
	 * i.e. whether l(A)=in &lt;=&gt; P(A)=1, l(A)=out &lt;=&gt; P(A)=0, l(A)=undec &lt;=&gt; P(A)=0.5  
	 * @param l some labelling
	 * @return true iff the given labeling is congruent to this probabilistic extension
	 */
	@SuppressWarnings("unlikely-arg-type")
	public boolean isCongruent(Labeling l){
		for(Argument a: l.keySet())
			if(!((l.get(a).equals(ArgumentStatus.IN) && this.get(a).getValue() >= 1 - Probability.PRECISION) ||
					(l.get(a).equals(ArgumentStatus.OUT) && this.get(a).getValue() <= Probability.PRECISION) ||
					(l.get(a).equals(ArgumentStatus.UNDECIDED) && this.get(a).getValue() <= 0.5 + Probability.PRECISION && this.get(a).getValue() >= 0.5 - Probability.PRECISION)))
				return false;
		return true;
	}
	
	/**
	 * Returns the epistemic labeling of this probabilistic extension, i.e.
	 * the labeling with l(A)=in if P(A)&gt;0.5, l(A)=undecided if P(A)=0.5, and 
	 * l(A)=0 if P(A)=0.
	 * @return the epistemic labeling of this probabilistic extension
	 */
	@SuppressWarnings("unlikely-arg-type")
	public Labeling getEpistemicLabeling(){
		Labeling l = new Labeling();
		for(Argument a: this.getAllArguments()){
			if(this.get(a).getValue() > 0.5 + Probability.PRECISION)
				l.put(a, ArgumentStatus.IN);
			else if(this.get(a).getValue() < 0.5 - Probability.PRECISION)
				l.put(a, ArgumentStatus.OUT);
			else l.put(a, ArgumentStatus.UNDECIDED);
		}
		return l;
	}
	
	/**
	 * Returns the characteristic probabilistic extension of the given interpretation,
	 * i.e. the probabilistic extension that assigns probability 0.5
	 * to the given in arguments of the labeling and 0.5 to the union of the in and undecided arguments
	 * 
	 * @param theory some Dung theory
	 * @param i some argumentation interpretation
	 * @return the characteristic probabilistic extension of the given interpretation.
	 */
	public static ProbabilisticExtension getCharacteristicProbabilisticExtension(DungTheory theory, AbstractArgumentationInterpretation i){
		ProbabilisticExtension pe = new ProbabilisticExtension();
		Extension<DungTheory> in = new Extension<DungTheory>(i.getArgumentsOfStatus(ArgumentStatus.IN));
		Extension<DungTheory> undec = new Extension<DungTheory>(i.getArgumentsOfStatus(ArgumentStatus.UNDECIDED));
		if(undec.isEmpty()){
			pe.put(in, new Probability(1d));
			return pe;
		}
		pe.put(in, new Probability(0.5));
		Extension<DungTheory> e = new Extension<DungTheory>(in);
		e.addAll(undec);
		pe.put(e, new Probability(0.5));		
		return pe;
	}
}
