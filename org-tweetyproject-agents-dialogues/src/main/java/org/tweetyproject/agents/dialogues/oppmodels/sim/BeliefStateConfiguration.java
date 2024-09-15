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
package org.tweetyproject.agents.dialogues.oppmodels.sim;

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

    /** Default Constructor */
    public BeliefStateConfiguration(){}
}
