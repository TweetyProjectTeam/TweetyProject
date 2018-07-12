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
 * class for default negated literals
 * 
 * @author Thomas Vengels
 *
 */
public class NotLiteral implements ELPLiteral {
	ELPLiteral lit;
	
	public NotLiteral(){
		
	}
	
	public NotLiteral(ELPLiteral arg) {
		this.lit = arg;
	}

	@Override
	public ELPLiteral getLiteral() {
		return lit;
	}

	@Override
	public boolean isAtom() {
		return false;
	}

	@Override
	public boolean isDefaultNegated() {
		return true;
	}

	@Override
	public boolean isStrictNegated() {
		return lit.isStrictNegated();
	}

	@Override
	public ELPAtom getAtom() {
		return this.lit.getAtom();
	}
	
	public String toString() {
		return "not "+lit.toString();
	}
	
	@Override
	public boolean isPredicate() {
		return false;
	}
}
