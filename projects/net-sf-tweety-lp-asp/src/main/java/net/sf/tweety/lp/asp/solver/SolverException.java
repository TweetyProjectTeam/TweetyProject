package net.sf.tweety.lp.asp.solver;

/**
 * 
 * @author Thomas Vengels
 * @author Tim Janus
 */
public class SolverException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static int	SE_ERROR = 1;
	public static int	SE_IO_FAILED = 2;
	public static int	SE_NO_BINARY = 3;
	public static int	SE_SYNTAX_ERROR = 4;
	public static int	SE_CANNOT_OPEN_INPUT = 5;
	public static int 	SE_CANNOT_FIND_SOLVER = 6;
	public static int 	SE_PERMISSIONS = 7;

	public SolverException(String text, int exceptionCode) {		
		super(text);		
		solverErrorText = text;
		solverErrorCode = exceptionCode;
	}
	
	public final String solverErrorText;
	public final int solverErrorCode;
	
	@Override
	public String toString() {
		return solverErrorText;
	}
}
