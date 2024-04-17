/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.ldo.syntax;

/**
 * Represents the box modality in the logic of dialectical outcomes (LDO). The box modality (often denoted as "[]" or "box")
 * is used to express necessity within the context of argumentation frameworks in LDO. This class encapsulates an LDO formula
 * and defines the semantic interpretation that the enclosed formula is necessarily true given the dialectical framework.
 *
 * This class provides an implementation for handling the box modality specifically, allowing the representation and manipulation
 * of such modal statements within LDO theories.
 * 
 * @author Matthias Thimm
 */
public class LdoBoxModality extends AbstractLdoModality {

	/**
	 * 
	 * @param innerFormula innerFormula
	 */
	public LdoBoxModality(LdoFormula innerFormula) {
		super(innerFormula);
	}

	@Override
	public LdoFormula clone() {
		return new LdoBoxModality(this.getInnerFormula());
	}

	public String toString(){
		return "[](" + this.getInnerFormula() + ")";
	}
}
