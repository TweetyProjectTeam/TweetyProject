package net.sf.tweety.arg.dung.util;

/**
 * This class lists some parameters
 * for Dung theory generation.
 * @author Matthias Thimm
 */
public class DungTheoryGenerationParameters{
	/** The number of arguments to be created in a theory. */
	public int numberOfArguments = 20;
	/** The attack probability for each two arguments in the theory. */
	public double attackProbability = 0.5;
	/** Whether to avoid self-attacks. */
	public boolean avoidSelfAttacks = true;
	/** Whether the generated theory must have a tree shape. */
	public boolean enforceTreeShape = false;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "<numberOfArguments=" + this.numberOfArguments + "," +
				"attackProbability=" + this.attackProbability + "," +
				"avoidSelfAttacks=" + this.avoidSelfAttacks + "," +
				"enforceTreeShape=" + this.enforceTreeShape +">";
	}
}