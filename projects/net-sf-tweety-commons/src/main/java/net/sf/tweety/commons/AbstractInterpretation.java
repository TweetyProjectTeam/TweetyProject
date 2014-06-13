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

import java.util.Collection;

/**
 * An abstract interpretation for some logical language.
 * @author Matthias Thimm
 */
public abstract class AbstractInterpretation implements Interpretation {
	
	/**
	 * Checks whether this interpretation satisfies all given formulas.
	 * @param formulas a collection of formulas.
	 * @return "true" if this interpretation satisfies all given formulas. 
	 * @throws IllegalArgumentException if at least one formula does not correspond
	 * 		to the expected language.
	 */
	@Override
	public boolean satisfies(Collection<? extends Formula> formulas) throws IllegalArgumentException{
		for(Formula f: formulas)
			if(!this.satisfies(f))
				return false;
		return true;
	}
}
