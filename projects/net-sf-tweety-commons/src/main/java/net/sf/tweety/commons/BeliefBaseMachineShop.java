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
 * Classes implementing this interface are capable of restoring
 * consistency of inconsistent belief bases.
 * 
 * @author Matthias Thimm
 */
public interface BeliefBaseMachineShop {

	/**
	 * Repairs the given belief base, i.e. restores consistency.
	 * @param beliefBase a possibly inconsistent belief base.
	 * @return a consistent belief base that is as close as possible
	 * 	to the given belief base. NOTE: if the given belief base is
	 *  consistent this method is expected to return it unmodified.
	 */
	public BeliefBase repair(BeliefBase beliefBase);
}
