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

import java.util.List;

/**
 * This class implement the p-norm distance function where distances are normalized
 * corresponding to their distance to 0.5.
 * @author Matthias Thimm
 */
public class ProbabilisticPNormDistanceFunction implements DistanceFunction {
	
	private static final long serialVersionUID = 3665499615843076323L;

	/** The parameter for the p-norm.*/
	private int p;
	
	/** The parameter of the normalization. */
	private int c;
	
	/** Creates a new p-norm distance function where distances are normalized.
	 * @param p the parameter for the p-norm.
	 * @param c the parameter of the normalization.
	 */
	public ProbabilisticPNormDistanceFunction(int p, int c){
		if(c < 3 || c % 2 != 1)
			throw new IllegalArgumentException("c has to be greater 2 and odd.");
		this.p = p;
		this.c = c;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.markovlogic.analysis.DistanceFunction#distance(java.util.List, java.util.List)
	 */
	@Override
	public double distance(List<Double> l1, List<Double> l2) {
		if(l1.size() != l2.size())
			throw new IllegalArgumentException("Lengths of lists must match.");
		Double sum = new Double(0);
		for(int i = 0; i< l1.size(); i++)
			sum += Math.pow(Math.abs(Math.pow(0.5-l1.get(i),this.c)-Math.pow(0.5-l2.get(i),this.c)),this.p);
		return Math.pow(sum, new Double(1)/p);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.p+"-pnorm";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + c;
		result = prime * result + p;
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
		ProbabilisticPNormDistanceFunction other = (ProbabilisticPNormDistanceFunction) obj;
		if (c != other.c)
			return false;
		if (p != other.p)
			return false;
		return true;
	}
}
