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
package net.sf.tweety.logicprogramming.asp.syntax;

/**
 * private class to model comparison expressions
 * like "A > B" or $A = B$.
 * 
 * @author Thomas Vengels
 *
 */
public class RelationPredicate extends ELPAtom {
	
	public RelationPredicate(String pred, String ...terms) {
		super(pred, terms);
	}
	
	public String toString() {
		return terms[0] + pred + terms[1];
	}
	
	@Override
	public boolean isPredicate() {
		return true;
	}
}