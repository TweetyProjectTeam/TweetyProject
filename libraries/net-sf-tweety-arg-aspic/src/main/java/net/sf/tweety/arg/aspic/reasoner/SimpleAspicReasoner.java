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
 package net.sf.tweety.arg.aspic.reasoner;

import net.sf.tweety.arg.aspic.syntax.AspicArgumentationTheory;
import net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

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
	 * @see net.sf.tweety.arg.aspic.reasoner.AbstractAspicReasoner#getDungTheory(net.sf.tweety.arg.aspic.syntax.AspicArgumentationTheory, net.sf.tweety.logics.commons.syntax.interfaces.Invertable)
	 */
	@Override
	public DungTheory getDungTheory(AspicArgumentationTheory<T> aat, T query) {		
		return aat.asDungTheory();
	}
	
}
