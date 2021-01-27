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
	 * Status of an InconsistencyMeasureResult:
	 * OK means that an inconsistency value has been computed. 
	 * TIMEOUT means that the measure implementation
	 * timed out before a value could be computed.
	 * The timeout is configured in {@link org.tweetyproject.logics.commons.analysis.InconsistencyMeasureEvaluator}.
	 */
	public enum Status {
		OK, TIMEOUT
	}

	private double inconsistencyValue;
	private Status timeoutStatus;
	private long elapsedTime; 

	private InconsistencyMeasureResult(double result, Status status) {
		this.inconsistencyValue = result;
		this.timeoutStatus = status;
		this.elapsedTime = -1;
	}

	public static InconsistencyMeasureResult timeout() {
		return new InconsistencyMeasureResult(-1.0, Status.TIMEOUT);
	}

	public static InconsistencyMeasureResult ok(double result) {
		return new InconsistencyMeasureResult(result, Status.OK);
	}

	public Status getStatus() {
		return timeoutStatus;
	}

	public double getValue() {
		return inconsistencyValue;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long time) {
		if (this.timeoutStatus == Status.OK)
			this.elapsedTime = time;
	}
}