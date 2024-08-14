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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.commons.analysis;
/**
 * This class represents the results the result of a call to an inconsistency measure
 * with a knowledge base, i.e. the inconsistency value, if found, the computation
 * time and the timeout status.
 *
 * @see org.tweetyproject.logics.commons.analysis.InconsistencyMeasureEvaluator
 *
 * @author Anna Gessler
 */
public class InconsistencyMeasureResult {
	 /**
     * Enum representing the status of an {@code InconsistencyMeasureResult}.
     * <ul>
     * <li>{@code OK}: Indicates that an inconsistency value has been successfully computed.</li>
     * <li>{@code TIMEOUT}: Indicates that the computation timed out before an inconsistency value could be determined.</li>
     * </ul>
     */
    public enum Status {
        OK, TIMEOUT
    }

    /** The computed inconsistency value, if available. */
    private double inconsistencyValue;

    /** The status of the computation, either OK or TIMEOUT. */
    private Status timeoutStatus;

    /** The time elapsed during the computation in milliseconds. */
    private long elapsedTime;

    /**
     * Private constructor to create an {@code InconsistencyMeasureResult}.
     *
     * @param result the inconsistency value, or -1.0 if the result is a timeout.
     * @param status the status of the result, either {@code OK} or {@code TIMEOUT}.
     */
    private InconsistencyMeasureResult(double result, Status status) {
        this.inconsistencyValue = result;
        this.timeoutStatus = status;
        this.elapsedTime = -1;
    }

    /**
     * Creates an {@code InconsistencyMeasureResult} indicating a timeout.
     *
     * @return an {@code InconsistencyMeasureResult} with {@code Status.TIMEOUT}.
     */
    public static InconsistencyMeasureResult timeout() {
        return new InconsistencyMeasureResult(-1.0, Status.TIMEOUT);
    }

    /**
     * Creates an {@code InconsistencyMeasureResult} with the given inconsistency value.
     *
     * @param result the computed inconsistency value.
     * @return an {@code InconsistencyMeasureResult} with {@code Status.OK}.
     */
    public static InconsistencyMeasureResult ok(double result) {
        return new InconsistencyMeasureResult(result, Status.OK);
    }

    /**
     * Returns the status of the inconsistency measure result.
     *
     * @return the status, either {@code OK} or {@code TIMEOUT}.
     */
    public Status getStatus() {
        return timeoutStatus;
    }

    /**
     * Returns the computed inconsistency value.
     *
     * <p>If the status is {@code TIMEOUT}, this value will be -1.0.</p>
     *
     * @return the inconsistency value.
     */
    public double getValue() {
        return inconsistencyValue;
    }

    /**
     * Returns the time elapsed during the computation, in milliseconds.
     *
     * <p>If the status is {@code TIMEOUT}, the elapsed time may not be meaningful.</p>
     *
     * @return the elapsed time in milliseconds.
     */
    public long getElapsedTime() {
        return elapsedTime;
    }

    /**
     * Sets the elapsed time for the computation.
     *
     * <p>This method only has an effect if the status is {@code OK}. If the status
     * is {@code TIMEOUT}, the elapsed time remains unchanged.</p>
     *
     * @param time the elapsed time in milliseconds.
     */
    public void setElapsedTime(long time) {
        if (this.timeoutStatus == Status.OK) {
            this.elapsedTime = time;
        }
    }
}