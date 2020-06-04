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
package net.sf.tweety.math.opt;

import java.util.*;

import net.sf.tweety.math.*;
import net.sf.tweety.math.term.*;


/**
 * Classes which extend this class represent algorithms for
 * finding zero points (roots) of functions. 
 * 
 * @author Matthias Thimm
 */
public abstract class RootFinder {

	/**
	 * The precision for finding roots.
	 */
	public static double PRECISION = 0.00001;
	
	/**
	 * The (multi-dimensional) function of this root finder.
	 */
	protected List<Term> functions;

	/**
	 * The starting point of this root finder.
	 */
	protected Map<Variable,Term> startingPoint;
	
	/**
	 * Creates a new root finder for the given function.
	 * @param function a term
	 */
	public RootFinder(){

	}
	/*
	/**
	 * Creates a new root finder for the given starting point and the given function
	 * @param function a function
	 * @param startingPoint the starting point
	 /
	public RootFinder(Term function, Map<Variable,Term> startingPoint){
		this.functions = new LinkedList<Term>();
		this.functions.add(function);
		this.startingPoint = startingPoint;
	}
	*/
	/**
	 * Creates a new root finder for the given starting point and the given
	 * (multi-dimensional) function
	 * @param functions a list of functions
	 * @param startingPoint the starting point
	 */
	public RootFinder(List<Term> functions, Map<Variable,Term> startingPoint){
		this.functions = new LinkedList<Term>();
		this.functions.addAll(functions);
		this.startingPoint = startingPoint;
	}
	
	/**
	 * sets the starting point of this root finder.
	 * @param startingPoint the starting point
	 */
	public void setStartingPoint(Map<Variable,Term> startingPoint){
		this.startingPoint = startingPoint;
	}
	
	/**
	 * Returns the starting point of this finder.
	 * @return the starting point of this finder.
	 */
	public Map<Variable,Term> getStartingPoint(){
		return this.startingPoint;
	}
	
	/**
	 * Returns the function of this root finder.
	 * @return the function of this root finder.
	 */
	public List<Term> getFunctions(){
		return this.functions;
	}
	
	/**
	 * Determines the values for the variables appearing in the function such
	 * the function evaluates to zero.
	 * @return a map from variables to terms such that "function" evaluates to zero.
	 * @throws GeneralMathException if something went wrong.
	 */
	public abstract Map<Variable,Term> randomRoot(List<Term> functions, Map<Variable,Term> startingPoint) throws GeneralMathException;	
	
}
