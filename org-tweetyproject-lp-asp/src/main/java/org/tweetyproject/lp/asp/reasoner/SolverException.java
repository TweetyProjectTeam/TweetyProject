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
package org.tweetyproject.lp.asp.reasoner;

/**
 * This class models a generic exception for ASP (Answer Set Programming) solvers.
 *
 * <p>
 * The `SolverException` is thrown when an error occurs in the context of an ASP solver.
 * It contains both a textual message and a corresponding error code to indicate the
 * specific type of error.
 * </p>
 *
 * <p><b>Authors:</b> Thomas Vengels, Tim Janus</p>
 */
public class SolverException extends Exception {

    private static final long serialVersionUID = 1L;

    /** Error code indicating a general error. */
    public static final int SE_ERROR = 1;

    /** Error code indicating an I/O failure. */
    public static final int SE_IO_FAILED = 2;

    /** Error code indicating that the binary could not be found. */
    public static final int SE_NO_BINARY = 3;

    /** Error code indicating a syntax error. */
    public static final int SE_SYNTAX_ERROR = 4;

    /** Error code indicating that the input file could not be opened. */
    public static final int SE_CANNOT_OPEN_INPUT = 5;

    /** Error code indicating that the solver could not be found. */
    public static final int SE_CANNOT_FIND_SOLVER = 6;

    /** Error code indicating insufficient permissions to execute the solver. */
    public static final int SE_PERMISSIONS = 7;

    /** The textual message associated with the exception. */
    public final String solverErrorText;

    /** The error code associated with the exception. */
    public final int solverErrorCode;

    /**
     * Creates a new `SolverException` with the specified message and error code.
     *
     * @param text the text message describing the exception.
     * @param exceptionCode the error code indicating the type of error.
     */
    public SolverException(String text, int exceptionCode) {
        super(text);
        this.solverErrorText = text;
        this.solverErrorCode = exceptionCode;
    }

    /**
     * Returns a string representation of the exception, which is the textual
     * message provided when the exception was created.
     *
     * @return the textual message associated with the exception.
     */
    @Override
    public String toString() {
        return solverErrorText;
    }
}
