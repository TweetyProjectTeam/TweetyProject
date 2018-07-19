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
package net.sf.tweety.arg.dung.reasoner;

import java.util.*;

import net.sf.tweety.arg.dung.semantics.*;
import net.sf.tweety.arg.dung.syntax.DungTheory;


/**
 * This reasoner for Dung theories performs inference on the grounded extension.
 * Computes the (unique) grounded extension, i.e., the least fixpoint of the characteristic function faf.
 * 
 * @author Matthias Thimm
 *
 */
public class SimpleGroundedReasoner extends AbstractExtensionReasoner {


	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner#getModels(net.sf.tweety.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Collection<Extension> getModels(DungTheory bbase) {
		Collection<Extension> extensions = new HashSet<Extension>();
		extensions.add(this.getModel(bbase));
		return extensions;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner#getModel(net.sf.tweety.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Extension getModel(DungTheory bbase) {
		Extension ext = new Extension();
		int size;
		do{
			size = ext.size();			
			ext = bbase.faf(ext);			
		}while(size!=ext.size());		
		return ext;
	}	
}
