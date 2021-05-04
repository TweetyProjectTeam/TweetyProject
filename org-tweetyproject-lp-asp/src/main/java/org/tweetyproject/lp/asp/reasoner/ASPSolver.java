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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.lp.asp.reasoner;

import java.io.File;
import java.util.Collection;

import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.QualitativeReasoner;
import org.tweetyproject.lp.asp.semantics.AnswerSet;
import org.tweetyproject.lp.asp.syntax.ASPLiteral;
import org.tweetyproject.lp.asp.syntax.ASPRule;
import org.tweetyproject.lp.asp.syntax.Program;

/**
 * This class provides a common interface for asp solvers. Most implementing
 * classes will simply call an external grounder and solver and return the
 * resulting string.
 * 
 * @author Sebastian Homann (Original), Thomas Vengels (Modifications), Anna
 *         Gessler (Modifications)
 */
public abstract class ASPSolver implements QualitativeReasoner<Program,ASPLiteral>, ModelProvider<ASPRule,Program,AnswerSet> {
	
	/**
	 * Stores the (latest) result of the solver as a String.
	 */
	protected String outputData;
	
	public String getOutput() {
		return this.outputData;
	}
	
	/**
	 * The maximum number of models to be generated.
	 * Default: 100
	 */
	protected int maxNumOfModels = 100;
	
	/**
	 * @return the maximum number of models to be generated.
	 */
	public int getMaxNumOfModels() {
		return maxNumOfModels;
	}

	/**
	 * Set the maximum number of models to be generated.
	 * @param maxNumOfModels the maximum number of models to be generated.
	 * 
	 */
	public void setMaxNumOfModels(int maxNumOfModels) {
		this.maxNumOfModels = maxNumOfModels;
	}
	
	/**
	 *  Represents the upper integer bound. 
	 *  Solvers that use this attribute will only accept
	 *  integers in [0,IntegerMaximum] in input programs.
	 *  Likewise, only integers in [0,IntegerMaximum] will be considered
	 *  by when evaluating arithmetic predicates.
	 */
	protected int integerMaximum = 1000;

	/**
	 *  Get the upper integer bound. 
	 *  Solvers that use this attribute will only accept
	 *  integers in [0,IntegerMaximum] in input programs.
	 *  Likewise, only integers in [0,IntegerMaximum] will be considered
	 *  by when evaluating arithmetic predicates.
	 * @return the upper integer bound.
	 */
	public int getIntegerMaximum() {  
		return integerMaximum;
	}

	/**
	 *  Set the upper integer bound. 
	 *  Solvers that use this attribute will only accept
	 *  integers in [0,IntegerMaximum] in input programs.
	 *  Likewise, only integers in [0,IntegerMaximum] will be considered
	 *  by when evaluating arithmetic predicates.
	 * @param integerMaximum the upper integer bound
	 */
	public void setIntegerMaximum(int integerMaximum) {
		this.integerMaximum = integerMaximum;
	}

	@Override
	public abstract Collection<AnswerSet> getModels(Program p);
	
	/**
	 * Returns a characterizing model (answer set) 
	 * of the given belief base using the given 
	 * upper integer limit.
	 * 
	 * @param p a program
	 * @param maxInt the max number of models to be returned
	 * @return AnswerSet
	 */
	public Collection<AnswerSet> getModels(Program p, int maxInt) {
		this.integerMaximum = maxInt;
		return getModels(p);
	}
	
	/**
	 * Returns a characterizing model (answer set) 
	 * of the given belief base using the given 
	 * upper integer limit.
	 * 
	 * @param p a program
	 * @param maxInt the max number of models to be returned
	 * @return AnswerSet
	 */
	public AnswerSet getModel(Program p, int maxInt) {
		this.integerMaximum = maxInt;
		return getModel(p);
	}
	
	/**
	 * Returns a characterizing model (answer set) 
	 * of the given belief base.
	 * 
	 * @param p containing belief base
	 * @return AnswerSet
	 */
	public abstract Collection<AnswerSet> getModels(String p);
	
	/**
	 * Returns a characterizing model (answer set) 
	 * of the given belief base.
	 * 
	 * @param f containing belief base
	 * @return AnswerSet
	 */
	public abstract Collection<AnswerSet> getModels(File f);
	
	@Override
	public abstract Boolean query(Program beliefbase, ASPLiteral formula);

}
