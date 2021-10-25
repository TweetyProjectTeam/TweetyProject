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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;

import java.util.*;

import org.tweetyproject.arg.dung.semantics.*;
import org.tweetyproject.arg.dung.syntax.DungTheory;


/**
 * This reasoner for Dung theories performs inference on the grounded extension.
 * Computes the (unique) grounded extension, i.e., the least fixpoint of the characteristic function faf.
 * 
 * @author Sebastian Franke
 *
 */
public class SimpleGroundedReasoner extends AbstractExtensionReasoner {


	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModels(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
		Collection<Extension<DungTheory>> extensions = new HashSet<Extension<DungTheory>>();
		extensions.add(this.getModel(bbase));
		return extensions;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Extension<DungTheory> getModel(DungTheory bbase) {
		Extension<DungTheory> ext = new Extension<DungTheory>();
		int size;
		do{
			size = ext.size();			
			ext = ((DungTheory)bbase).faf(ext);			
		}while(size!=ext.size());		
		return ext;
	}


}
