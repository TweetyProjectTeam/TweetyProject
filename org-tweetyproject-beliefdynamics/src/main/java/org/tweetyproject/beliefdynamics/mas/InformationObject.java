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
package org.tweetyproject.beliefdynamics.mas;

import org.tweetyproject.agents.*;
import org.tweetyproject.commons.*;

/**
 * This class represents a formula annotated with the source of the formula.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of the inner formula.
 */
public class InformationObject<T extends Formula> implements Formula{
	
	/**
	 * The source of this information object.
	 */
	private Agent source;
	
	/**
	 * The formula of this information object
	 */
	private T formula;
	
	/**
	 * Creates a new information object for the given formula
	 * and the given source.
	 * @param formula some formula.
	 * @param source some agent.
	 */
	public InformationObject(T formula, Agent source){
		this.formula = formula;
		this.source = source;
	}
	
	/**
	 * Returns the source of this information object.
	 * @return the source of this information object.
	 */
	public Agent getSource(){
		return this.source;
	}
	
	/**
	 * Returns the formula of this information object.
	 * @return the formula of this information object.
	 */
	public T getFormula(){
		return this.formula;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.Formula#getSignature()
	 */
	@Override
	public Signature getSignature() {
		return this.formula.getSignature();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return this.source + ":" + this.formula;
	}
}
