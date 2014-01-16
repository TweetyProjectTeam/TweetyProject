package net.sf.tweety.agents.dialogues.oppmodels.sim;

/**
 * This class encapsulates configuration options for generating
 * belief states.
 * @author Matthias Thimm
 */
public abstract class BeliefStateConfiguration {
	/** The maximal depth of the recursive model. */
	public int maxRecursionDepth;
	/** The probability that an argument appearing in depth n does not appear
	 * in depth n+1. */
	public double probRecursionDecay;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + maxRecursionDepth;
		long temp;
		temp = Double.doubleToLongBits(probRecursionDecay);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BeliefStateConfiguration other = (BeliefStateConfiguration) obj;
		if (maxRecursionDepth != other.maxRecursionDepth)
			return false;
		if (Double.doubleToLongBits(probRecursionDecay) != Double
				.doubleToLongBits(other.probRecursionDecay))
			return false;
		return true;
	}
}
