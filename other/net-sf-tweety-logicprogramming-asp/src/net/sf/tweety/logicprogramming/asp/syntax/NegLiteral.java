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
 * class for strict negated literals
 * 
 * @author Thomas Vengels
 *
 */
public class NegLiteral implements ELPLiteral {

	protected ELPAtom atom;
	
	public NegLiteral(ELPAtom a) {
		atom = a;
	}
	
	@Override
	public ELPLiteral getLiteral() {
		return this;
	}

	@Override
	public boolean isAtom() {
		return false;
	}

	@Override
	public boolean isDefaultNegated() {
		return false;
	}

	@Override
	public boolean isStrictNegated() {
		return true;
	}

	@Override
	public ELPAtom getAtom() {
		return this.atom;
	}
	
	public String toString() {
		return "-"+atom.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof NegLiteral))
			return false;

		NegLiteral nl = (NegLiteral) o;
		
		return this.atom.equals( nl.getAtom() );
	}

	@Override
	public boolean isPredicate() {
		return false;
	}

}
