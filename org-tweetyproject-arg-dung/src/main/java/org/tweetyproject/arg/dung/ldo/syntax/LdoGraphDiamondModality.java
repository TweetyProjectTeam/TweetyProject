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

import java.util.Set;


/**
 * *description missing*
 * @author Matthias Thimm
 *
 */
public class LdoGraphDiamondModality extends AbstractGraphLdoModality{
	
	
	/**
	 * 
	 * @param innerFormula inner Formula
	 * @param lowerReferenceArguments lower Reference Arguments
	 * @param upperReferenceArguments upper Reference Arguments
	 */
	public LdoGraphDiamondModality(LdoFormula innerFormula,	Set<LdoArgument> lowerReferenceArguments, Set<LdoArgument> upperReferenceArguments) {
		super(innerFormula, lowerReferenceArguments,upperReferenceArguments);
	}

	@Override
	public LdoFormula clone() {
		return new LdoGraphBoxModality(this.getInnerFormula(),this.getLowerReferenceArguments(),this.getUpperReferenceArguments());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "<" + this.getLowerReferenceArguments() + "," + this.getUpperReferenceArguments() + ">(" + this.getInnerFormula() + ")";
	}
}
