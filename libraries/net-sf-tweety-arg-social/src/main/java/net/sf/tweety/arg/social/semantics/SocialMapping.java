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
package net.sf.tweety.arg.social.semantics;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.social.SocialAbstractArgumentationFramework;
import net.sf.tweety.commons.AbstractInterpretation;
import net.sf.tweety.commons.BeliefBase;

/**
 * Implements a mapping from arguments to social value.
 * 
 * @author Matthias Thimm
 * @param <L> The set used for valuations
 */
public class SocialMapping<L> extends AbstractInterpretation<Argument>{

	/** The semantics used for this mapping */
	private AbstractSocialSemantics<L> semantics;
	
	/** Maps arguments to their values */
	private Map<Argument,L> map;
	
	/**
	 * Creates a new mapping wrt. the given semantics.
	 * @param semantics some semantics
	 */
	public SocialMapping(AbstractSocialSemantics<L> semantics){
		this.semantics = semantics;
		this.map = new HashMap<Argument,L>();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Interpretation#satisfies(net.sf.tweety.commons.Formula)
	 */
	@Override
	public boolean satisfies(Argument formula) throws IllegalArgumentException {
		if(!this.map.containsKey(formula))
			return false;
		return this.semantics.compare(this.map.get(formula), this.semantics.topElement()) == 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Interpretation#satisfies(net.sf.tweety.commons.BeliefBase)
	 */
	@Override
	public boolean satisfies(BeliefBase beliefBase) throws IllegalArgumentException {
		if(!(beliefBase instanceof SocialAbstractArgumentationFramework))
			throw new IllegalArgumentException("Belief base of type 'SocialAbstractArgumentationFramework' expected.");
		SocialAbstractArgumentationFramework saf = (SocialAbstractArgumentationFramework) beliefBase;
		for(Argument arg: saf){
			L arg_value = this.semantics.supp(saf.getPositive(arg), saf.getNegative(arg));
			Collection<L> att_values = new LinkedList<L>();
			for(Argument b: saf.getAttackers(arg))
				att_values.add(this.map.get(b));
			L att_value = this.semantics.neg(this.semantics.or(att_values));
			if(this.semantics.compare(this.semantics.and(arg_value,att_value), this.map.get(arg)) != 0)
				return false;
		}
		return true;
	}

	/**
	 * Returns the social value of the given argument
	 * @param a some argument
	 * @return the social value of the given argument
	 */
	public L get(Argument a){
		return this.map.get(a);
	}
	
	/**
	 * Sets the social value of the given argument
	 * @param a some argument
	 * @param val the value for the argument
	 * @return the social value of the given argument
	 */
	public L put(Argument a, L val){
		return this.map.put(a,val);
	}
	
	/**
	 * Returns "true" iff the given argument has a value.
	 * @param a some argument
	 * @return "true" iff the given argument has a value.
	 */
	public boolean containsKey(Argument a){
		return this.map.containsKey(a);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.map.toString();
	}

}
