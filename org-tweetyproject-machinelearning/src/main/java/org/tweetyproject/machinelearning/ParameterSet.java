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
package org.tweetyproject.machinelearning;

import java.util.ArrayList;

/**
 * A set of parameters that can be given to a trainer to 
 * specifiy the training task.
 * @author Matthias Thimm
 */
public class ParameterSet extends ArrayList<TrainingParameter>{

	/** For serialization. */
	private static final long serialVersionUID = 1198936758760287517L;

	/**
	 * Checks whether this set contains a parameter with the same
	 * name as the given parameter.
	 * @param param some parameter
	 * @return Checks whether this set contains a parameter with the same
	 * name as the given parameter.
	 */
	public boolean containsParameter(TrainingParameter param){
		for(TrainingParameter p: this)
			if(p.getName().equals(param.getName()))
				return true;
		return false;
	}
	
	/**
	 * Returns the parameter of this set with the same name as the given
	 * parameter (or throws an IllegalArgumentException)
	 * @param param some parameter
	 * @return Returns the parameter of this set with the same name as the given
	 * parameter (or throws an IllegalArgumentException)
	 */
	public TrainingParameter getParameter(TrainingParameter param){
		for(TrainingParameter p: this)
			if(p.getName().equals(param.getName()))
				return p;
		throw new IllegalArgumentException("Parameter not contained in this set");
	}

	
}
