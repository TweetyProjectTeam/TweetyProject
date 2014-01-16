package net.sf.tweety.math.opt;

/**
 * This exception is thrown when a problem cannot be solved due to its inconsistency.
 * 
 * @author Matthias Thimm
 */
public class ProblemInconsistentException extends RuntimeException {

	/**
	 * For serialization.
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage(){
		return "The problem is inconsistent.";
	}
}
