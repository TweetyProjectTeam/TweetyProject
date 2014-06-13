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
package net.sf.tweety.logics.fol.lang;

import net.sf.tweety.commons.*;

/**
 * This class models a first-order language without quantifiers and without functions.
 * @author Matthias Thimm
 */
public class FolLanguageNoQuantifiersNoFunctions extends FolLanguageNoQuantifiers{
	/**
	 * Creates a new language on the given signature.
	 * @param signature a signature.
	 */
	public FolLanguageNoQuantifiersNoFunctions(Signature signature){
		super(signature);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.lang.FolLanguageNoQuantifiers#isRepresentable(net.sf.tweety.kr.Formula)
	 */
	@Override
	public boolean isRepresentable(Formula formula){
		if(!super.isRepresentable(formula)) return false;
		return (new FolLanguageNoFunctions(this.getSignature()).isRepresentable(formula));		
	}

}
