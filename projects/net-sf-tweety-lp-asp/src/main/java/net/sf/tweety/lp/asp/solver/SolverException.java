/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
