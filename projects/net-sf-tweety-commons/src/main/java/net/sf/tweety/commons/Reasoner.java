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
package net.sf.tweety.commons;

/**
 * A reasoner specifies a specific inference operation for a given language.
 * @author Matthias Thimm
 * @author Anna Gessler
 * 
 * @param S the class of belief bases for this reasoner.
 * @param T the class of formulas for this reasoner
 */
public abstract class Reasoner {
	
	/**
	 * The knowledge base on which reasoning is performed.
	 */
	private BeliefBase beliefBase;
	
	/**
	 * Creates a new reasoner for the given knowledge base.
	 * @param beliefBase a knowledge base.
	 */
	public Reasoner(BeliefBase beliefBase){
		this.beliefBase = beliefBase;
	}
	
	/**
	 * This method determines the answer of the given query
	 * wrt. to the given knowledge base.
	 * @param query a query.
	 * @return the answer to the query.
	 */
	public abstract Answer query(Formula query);
	
	/**
	 * This method determines the answer of the given query
	 * wrt. to a given knowledge base.
	 * @param kb a knowledge base
	 * @param query a query.
	 * @return the answer to the query.
	 */
	public Answer query(BeliefBase kb, Formula query) { 
		this.setKnowledgeBase(kb);
		return this.query(query);
	}
	
	/**
	 * Returns the knowledge base of this reasoner.
	 * @return the knowledge base of this reasoner.
	 */
	public BeliefBase getKnowledgeBase(){
		return this.beliefBase;
	}
	
	/**
	 * Set the knowledge base of this reasoner.
	 * @param beliefBase the knowledge base of this reasoner.
	 */
	public void setKnowledgeBase(BeliefBase beliefBase){
		this.beliefBase = beliefBase;
	}
}
