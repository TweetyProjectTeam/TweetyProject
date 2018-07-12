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
package net.sf.tweety.commons;


/**
 * An abstract answer of a knowledge base to a query. 
 * @author Matthias Thimm
 */
public class Answer {

	/**
	 * The original query for this answer.
	 */
	private Formula query;
	
	/**
	 * The answer of the query if it can be represented
	 * as true or false
	 */
	private boolean answerBoolean;
	
	/**
	 * The answer of the query if it can be represented
	 * as a number
	 */
	private Double answerDouble;
			
	/**
	 * The belief set of the original query. 
	 */
	private BeliefBase beliefBase;
	
	/**
	 * A textual description of the answer to the query.
	 */
	private String text;
	
	/**
	 * Creates an empty answer for the given query.
	 * @param beliefSet some belief set
	 * @param query a query.
	 */
	public Answer(BeliefBase beliefBase, Formula query){
		this.beliefBase = beliefBase;
		this.query = query;
		this.text = new String();
	}
	
	/**
	 * Appends the given text to this answer's text.
	 * @param text a string.
	 */
	public void appendText(String text){
		this.text += text + "\n";
	}
	
	/**
	 * The answer of the query in boolean form.
	 * @param answer a boolean
	 */
	public void setAnswer(boolean answer){
		this.answerBoolean = answer;
	}
	
	/**
	 * The answer of the query in boolean form.
	 * @param answer a boolean
	 */
	public void setAnswer(Double answer){
		this.answerDouble = answer;
	}
	
	/**
	 * Returns the boolean form of this answer.
	 * @return the boolean form of this answer.
	 */
	public boolean getAnswerBoolean(){
		return this.answerBoolean;
	}
	
	/**
	 * Returns the double form of this answer.
	 * @return the double form of this answer.
	 */
	public Double getAnswerDouble(){
		return this.answerDouble;
	}
	
	/**
	 * Returns the textual description of this answer.
	 * @return the textual description of this answer.
	 */
	public String getText(){
		return this.text;
	}
	
	/**
	 * Returns the query this answer relates to.
	 * @return the query this answer relates to.
	 */
	public Formula getQuery(){
		return this.query;
	}
	
	/**
	 * Returns the knowledge base this answer relates to.
	 * @return the knowledge base this answer relates to.
	 */
	public BeliefBase getKnowledgeBase(){
		return this.beliefBase;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return this.text;
	}
	
}
