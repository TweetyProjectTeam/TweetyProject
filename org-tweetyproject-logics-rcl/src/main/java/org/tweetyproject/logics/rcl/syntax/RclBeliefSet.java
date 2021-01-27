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
package org.tweetyproject.logics.rcl.syntax;

import java.util.*;

import org.tweetyproject.commons.*;
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.fol.syntax.*;

/**
 * This class models a belief set on relational conditional logic, i.e. a set of relational conditionals.
 * 
 * @author Matthias Thimm
 *
 */
public class RclBeliefSet extends BeliefSet<RelationalConditional,FolSignature> {
	
	/**
	 * Creates a new (empty) conditional belief set.
	 */
	public RclBeliefSet(){
		super();
	}
	
	/**
	 * Creates a new relational conditional belief set with the given collection of
	 * relational conditionals.
	 * @param conditionals a collection of relational conditionals.
	 */
	public RclBeliefSet(Collection<? extends RelationalConditional> conditionals){
		super(conditionals);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.kr.BeliefBase#getSignature()
	 */
	@Override
	public Signature getMinimalSignature(){
		FolSignature sig = new FolSignature();
		for(Formula f: this){
			RelationalConditional c = (RelationalConditional) f;
			sig.addAll(c.getTerms(Constant.class));
			sig.addAll(c.getFunctors());
			sig.addAll(c.getPredicates());			
		}
		return sig;
	}

	@Override
	protected FolSignature instantiateSignature() {
		return new FolSignature();
	}

}
