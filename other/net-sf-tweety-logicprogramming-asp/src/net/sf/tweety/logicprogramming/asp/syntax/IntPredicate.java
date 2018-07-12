/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
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
package net.sf.tweety.logicprogramming.asp.syntax;

/**
 * class to model special build-in dlv
 * predicate #int(value), where value
 * is a natural number or a variable
 * 
 * @author Thomas Vengels
 *
 */
class IntPredicate extends ELPAtom {
	public IntPredicate(String value) {
		super("#int",value);
	}
	
	@Override
	public boolean isPredicate() {
		return true;
	}
}
