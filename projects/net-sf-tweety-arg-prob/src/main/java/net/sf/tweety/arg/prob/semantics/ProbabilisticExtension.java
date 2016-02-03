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
package net.sf.tweety.arg.prob.semantics;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.semantics.AbstractArgumentationInterpretation;
import net.sf.tweety.arg.dung.semantics.ArgumentStatus;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.Labeling;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.math.probability.*;

/**
 * This class implements a probabilistic interpretation for Dung argumentation frameworks, cf. [Thimm:2012].
 * 
 * @author Matthias Thimm
 */
public class ProbabilisticExtension extends ProbabilityFunction<Extension>{

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
		for(Extension e: this.keySet())
			if(e.contains(a))
				p = p.add(this.probability(e));
		return p;
	} 
	
	/**
	 * Returns the set of all arguments appearing.
	 * @return the set of all arguments appearing.
	 */
	public Extension getAllArguments(){
		Extension e = new Extension();
		for(Extension ex: this.keySet())
			e.addAll(ex);
		return e;
	}
	
	/** Returns the upper cut of this probabilistic extension wrt. delta, i.e.
	 * all arguments that have probability >= delta.
	 * @param theory a Dung theory.
	 * @param delta some threshold.
	 * @return the set of arguments that have probability >= delta.
	 */
	public Extension getUpperCut(DungTheory theory, double delta){
		Extension e = new Extension();
		for(Argument arg: theory)
			if(this.probability(arg).doubleValue() >= delta)
				e.add(arg);
		return e;
	}
	
	/** Returns the lower cut of this probabilistic extension wrt. delta, i.e.
	 * all arguments that have probability <= delta.
	 * @param theory a Dung theory.
	 * @param delta some threshold.
	 * @return the set of arguments that have probability <= delta.
	 */
	public Extension geLowerCut(DungTheory theory, double delta){
		Extension e = new Extension();
		for(Argument arg: theory)
			if(this.probability(arg).doubleValue() <= delta)
				e.add(arg);
		return e;
	}
	
	/**
	 * Checks whether the given labeling is congruent to this probabilistic extension,
	 * i.e. whether l(A)=in <=> P(A)=1, l(A)=out <=> P(A)=0, l(A)=undec <=> P(A)=0.5  
	 * @param l
	 */
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
	 * the labeling with l(A)=in if P(A)>0.5, l(A)=undecided if P(A)=0.5, and 
	 * l(A)=0 if P(A)=0.
	 * @return the epistemic labeling of this probabilistic extension
	 */
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
		Extension in = new Extension(i.getArgumentsOfStatus(ArgumentStatus.IN));
		Extension undec = new Extension(i.getArgumentsOfStatus(ArgumentStatus.UNDECIDED));
		if(undec.isEmpty()){
			pe.put(in, new Probability(1d));
			return pe;
		}
		pe.put(in, new Probability(0.5));
		Extension e = new Extension(in);
		e.addAll(undec);
		pe.put(e, new Probability(0.5));		
		return pe;
	}
}
