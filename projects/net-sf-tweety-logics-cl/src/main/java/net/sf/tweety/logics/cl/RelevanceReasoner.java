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
package net.sf.tweety.logics.cl;

import net.sf.tweety.commons.*;

/**
 * Implements the approach from<br>
 * 
 * James P. Delgrande. Relevance, Conditionals, and Defeasible Reasoning. In preparation.
 * 
 * @author Matthias Thimm
 *
 */
public class RelevanceReasoner extends Reasoner {

	/**
	 * The extension of the knowledgebase. Once this
	 * extension has been computed it is used for
	 * subsequent queries in order to avoid unnecessary
	 * computations.
	 */
	private ClBeliefSet extension;
	
	/**
	 * Creates a new relevance reasoner for the given knowledge base.
	 * @param beliefBase a knowledge base.	
	 */
	public RelevanceReasoner(BeliefBase beliefBase){
		super(beliefBase);		
		if(!(beliefBase instanceof ClBeliefSet))
			throw new IllegalArgumentException("Knowledge base of class ClBeliefSet expected.");
	}		
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Reasoner#query(net.sf.tweety.kr.Formula)
	 */
	@Override
	public Answer query(Formula query) {
		// TODO
		return null;
	}

	/**
	 * Returns the extended knowledge base this reasoner bases on.
	 * @return the extended knowledge base this reasoner bases on.
	 */
	public ClBeliefSet getExtension(){
		if(this.extension == null)
			this.extension = this.computeExtension();
		return this.extension;
	}
	
	/**
	 * Computes the extended knowledge base this reasoner bases on.
	 * @return the extended knowledge base this reasoner bases on.
	 */
	private ClBeliefSet computeExtension(){
		//TODO
		return null;
	}
	
}
