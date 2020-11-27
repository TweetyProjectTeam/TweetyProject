package net.sf.tweety.math.opt.problem;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.math.term.OptProbElement;

public abstract class GeneralConstraintSatisfactionProblem extends  HashSet<OptProbElement>{
	
	public GeneralConstraintSatisfactionProblem()
	{
		super();
	}
	
	public GeneralConstraintSatisfactionProblem(Collection<? extends OptProbElement> opts)
	{
		super(opts);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
