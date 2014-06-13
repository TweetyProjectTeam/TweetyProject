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
package net.sf.tweety.commons;

/**
 * A signatures lists the atomic language structures for some language.
 * @author Matthias Thimm
 */
public abstract class Signature {
	
	/**
	 * Checks whether this signature is a sub-signature of the
	 * given signature, i.e. whether each logical expression expressible
	 * with this signature is also expressible with the given signature.
	 * @param other a signature.
	 * @return "true" iff this signature is a subsignature of the given one.
	 */
	public abstract boolean isSubSignature(Signature other);
	
	/**
	 * Checks whether this signature has common elements with the
	 * given signature, i.e. whether there are logical expressions expressible
	 * with this signature that are also expressible with the given signature.
	 * @param other a signature.
	 * @return "true" iff this signature is overlapping with the given one.
	 */
	public abstract boolean isOverlappingSignature(Signature other);
	
	/** 
	 * Adds the elements of the given signature to this signature.
	 * @param other a signature.
	 */
	public abstract void addSignature(Signature other);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public abstract int hashCode();
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public abstract boolean equals(Object obj);
}
