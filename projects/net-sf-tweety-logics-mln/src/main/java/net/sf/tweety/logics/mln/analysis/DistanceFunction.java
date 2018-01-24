/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.mln.analysis;

import java.io.Serializable;
import java.util.List;

/**
 * This interface defines a distance function for two vectors of doubles.
 * 
 * @author Matthias Thimm
 */
public interface DistanceFunction extends Serializable {
	
	/** Measures the distance between the two vectors.
	 * @param l1 some list of doubles.
	 * @param l2 some list of doubles.
	 * @return the distance between the two vectors.
	 */
	public double distance(List<Double> l1, List<Double> l2);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString();
}
