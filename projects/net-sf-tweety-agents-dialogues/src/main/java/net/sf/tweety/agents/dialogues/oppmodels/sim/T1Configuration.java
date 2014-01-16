package net.sf.tweety.agents.dialogues.oppmodels.sim;

/**
 * This class encapsulates configuration options for generating
 * T1 belief states.
 * @author Matthias Thimm
 */
public class T1Configuration extends BeliefStateConfiguration{
	/** this parameter indicates whether the nested model is correct wrt. the other agent. */
	public boolean oppModelCorrect;

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (oppModelCorrect ? 1231 : 1237);
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
		T1Configuration other = (T1Configuration) obj;
		if (oppModelCorrect != other.oppModelCorrect)
			return false;
		return true;
	}
}
