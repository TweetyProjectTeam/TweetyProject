package net.sf.tweety.agents.dialogues.oppmodels.sim;

/**
 * This class encapsulates configuration options for generating
 * T2 belief states.
 * @author Matthias Thimm
 */
public class T2Configuration extends BeliefStateConfiguration{
	/** The maximal number of sub-models in the probability distribution
	 * of a model */
	public int maxRecursionWidth;

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + maxRecursionWidth;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		T2Configuration other = (T2Configuration) obj;
		if (maxRecursionWidth != other.maxRecursionWidth)
			return false;
		return true;
	}
}
