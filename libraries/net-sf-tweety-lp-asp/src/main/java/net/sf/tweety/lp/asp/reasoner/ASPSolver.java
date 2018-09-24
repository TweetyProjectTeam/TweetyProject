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
package net.sf.tweety.lp.asp.reasoner;

import net.sf.tweety.commons.ModelProvider;
import net.sf.tweety.commons.QualitativeReasoner;
import net.sf.tweety.lp.asp.semantics.AnswerSet;
import net.sf.tweety.lp.asp.semantics.AnswerSetList;
import net.sf.tweety.lp.asp.syntax.ASPLiteral;
import net.sf.tweety.lp.asp.syntax.ASPRule;
import net.sf.tweety.lp.asp.syntax.Program;

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
	 */
	protected int maxNumOfModels = 100;
	
	/**
	 * Get the maximum number of models to be generated.
	 * @return maximum number
	 */
	public int getMaxNumOfModels() {
		return maxNumOfModels;
	}

	/**
	 * Set the maximum number of models to be generated.
	 * @return maximum number
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
	 */
	public void setIntegerMaximum(int integerMaximum) {
		this.integerMaximum = integerMaximum;
	}

	@Override
	public abstract AnswerSetList getModels(Program bbase);
	
	/**
	 * Returns a characterizing model (answer set) 
	 * of the given belief base using the given 
	 * upper integer limit.
	 * 
	 * @param p
	 * @param maxInt
	 * @return AnswerSet
	 */
	public AnswerSetList getModels(Program p, int maxInt) {
		this.integerMaximum = maxInt;
		return getModels(p);
	}
	
	/**
	 * Returns a characterizing model (answer set) 
	 * of the given belief base using the given 
	 * upper integer limit.
	 * 
	 * @param p
	 * @param maxInt
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
	 * @param p
	 * @param maxInt
	 * @return AnswerSet
	 */
	public abstract AnswerSetList getModels(String p);

}
