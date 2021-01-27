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
 package org.tweetyproject.arg.aspic.reasoner;

import org.tweetyproject.arg.aspic.syntax.AspicArgumentationTheory;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.logics.commons.syntax.interfaces.Invertable;

/**
 * @author Nils Geilen, Matthias Thimm
 *	This class models a reasoner over Aspic formulae
 * 
 * @param <T> the formula type
 */
public class SimpleAspicReasoner<T extends Invertable> extends AbstractAspicReasoner<T>  {

	/**
	 * Creates a new instance
	 * @param aafReasoner Underlying reasoner for AAFs. 
	 */
	public SimpleAspicReasoner(AbstractExtensionReasoner aafReasoner) {
		super(aafReasoner);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.aspic.reasoner.AbstractAspicReasoner#getDungTheory(org.tweetyproject.arg.aspic.syntax.AspicArgumentationTheory, org.tweetyproject.logics.commons.syntax.interfaces.Invertable)
	 */
	@Override
	public DungTheory getDungTheory(AspicArgumentationTheory<T> aat, T query) {		
		return aat.asDungTheory();
	}
	
}
