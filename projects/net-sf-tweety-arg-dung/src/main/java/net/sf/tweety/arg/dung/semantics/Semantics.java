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
package net.sf.tweety.arg.dung.semantics;

/**
 * This interface contains some global constants for identifying
 * certain semantics.
 * 
 * @author Matthias Thimm
 *
 */
public interface Semantics {
	public static final int GROUNDED_SEMANTICS = 0;
	public static final int STABLE_SEMANTICS = 1;
	public static final int PREFERRED_SEMANTICS = 2;
	public static final int COMPLETE_SEMANTICS = 3;
	public static final int ADMISSIBLE_SEMANTICS = 4;
	public static final int CONFLICTFREE_SEMANTICS = 5;
	public static final int SEMISTABLE_SEMANTICS = 6;
	public static final int IDEAL_SEMANTICS = 7;
	public static final int STAGE_SEMANTICS = 8;
	public static final int CF2_SEMANTICS = 9;
	
	public static final int SCEPTICAL_INFERENCE = 4;
	public static final int CREDULOUS_INFERENCE = 5;
}
