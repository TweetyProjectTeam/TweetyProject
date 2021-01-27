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
package org.tweetyproject.logics.pcl.syntax;

import java.util.*;

import org.tweetyproject.commons.*;
import org.tweetyproject.logics.pl.syntax.*;


/**
 * This class models a belief set on probabilistic conditional logic, i.e. a set of
 * probabilistic conditionals.
 * 
 * @author Matthias Thimm
 *
 */
public class PclBeliefSet extends BeliefSet<ProbabilisticConditional,PlSignature> {

	/**
	 * Creates a new (empty) conditional belief set.
	 */
	public PclBeliefSet(){
		super();
	}
	
	/**
	 * Creates a new conditional belief set with the given collection of
	 * conditionals.
	 * @param conditionals a collection of conditionals.
	 */
	public PclBeliefSet(Collection<? extends ProbabilisticConditional> conditionals){
		super(conditionals);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.kr.BeliefSet#getSignature()
	 */
	@Override
	public Signature getMinimalSignature() {
		PlSignature sig = new PlSignature();
		for(ProbabilisticConditional c: this)
			sig.addSignature(((PlSignature)c.getSignature()));			
		return sig;
	}

	@Override
	protected PlSignature instantiateSignature() {
		return new PlSignature();
	}	
	
}
