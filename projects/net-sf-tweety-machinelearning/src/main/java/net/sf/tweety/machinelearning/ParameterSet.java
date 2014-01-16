package net.sf.tweety.machinelearning;

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
