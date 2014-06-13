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
package net.sf.tweety.machinelearning;

/**
 * A category for multi-class classification using a double as identifier.
 * 
 * @author Matthias Thimm
 */
public class DoubleCategory implements Category {
	
	/** The value of the category. */
	private double cat;

	/**
	 * Creates a new category with the given value.
	 * @param cat some boolean
	 */
	public DoubleCategory(double cat){
		this.cat = cat;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(cat);
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
		DoubleCategory other = (DoubleCategory) obj;
		if (Double.doubleToLongBits(cat) != Double.doubleToLongBits(other.cat))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.machinelearning.Category#asDouble()
	 */
	@Override
	public double asDouble() {
		return this.cat;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return new Double(this.cat).toString();
	}
}
