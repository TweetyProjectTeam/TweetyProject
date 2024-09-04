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
 * T3 belief states. For simplicity we only consider the
 * case that no more than one virtual argument is mapped
 * to a real one.
 * @author Matthias Thimm
 */
public class T3Configuration extends T2Configuration {
	/** The percentage of virtual arguments in the 
	 * view of an agent. */
	public double percentageVirtualArguments = 0.1;
	/** The percentage of attacks a virtual argument
	 * retains from the original attacks. */
	public double percentageVirtualAttacks = 0.9;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(percentageVirtualArguments);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(percentageVirtualAttacks);
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
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		T3Configuration other = (T3Configuration) obj;
		if (Double.doubleToLongBits(percentageVirtualArguments) != Double
				.doubleToLongBits(other.percentageVirtualArguments))
			return false;
		if (Double.doubleToLongBits(percentageVirtualAttacks) != Double
				.doubleToLongBits(other.percentageVirtualAttacks))
			return false;
		return true;
	}	

    /** Default Constructor */
    public T3Configuration(){}
}
