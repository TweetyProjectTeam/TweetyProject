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
package org.tweetyproject.arg.dung.reasoner;

import java.util.*;

import org.tweetyproject.arg.dung.semantics.*;
import org.tweetyproject.arg.dung.syntax.*;


/**
 * This reasoner for Dung theories performs inference on the complete extensions.
 * Computes the set of all complete extensions, i.e., all admissible sets that contain all their acceptable arguments.
 * @author Matthias Thimm
 *
 */
public class SimpleCompleteReasoner extends AbstractExtensionReasoner {

	@Override
	public Collection<Extension> getModels(DungTheory bbase) {
		Extension groundedExtension = new SimpleGroundedReasoner().getModel(bbase);
		Set<Argument> remaining = new HashSet<Argument>(bbase);
		remaining.removeAll(groundedExtension);
		return this.getCompleteExtensions(bbase,groundedExtension,remaining);	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Extension getModel(DungTheory bbase) {
		// as the grounded extension is also complete, we return that one
		return new SimpleGroundedReasoner().getModel(bbase);
	}
		
	/**
	 * Auxiliary method to compute all complete extensions
	 * @param dungTheory a Dung theory
	 * @param ext some extension
	 * @param remaining arguments that still have to be considered to be part of an extension
	 * @return all complete extensions that are supersets of an argument in <source>arguments</source>
	 */
	private Set<Extension> getCompleteExtensions(DungTheory dungTheory, Extension ext, Collection<Argument> remaining){
		Set<Extension> extensions = new HashSet<Extension>();
		if(ext.isConflictFree(dungTheory)){
			if(dungTheory.faf(ext).equals(ext))
				extensions.add(ext);
			if(!remaining.isEmpty()){
				Argument arg = remaining.iterator().next();
				Collection<Argument> remaining2 = new HashSet<Argument>(remaining);
				remaining2.remove(arg);
				extensions.addAll(this.getCompleteExtensions(dungTheory,ext, remaining2));
				Extension ext2 = new Extension(ext);
				ext2.add(arg);
				extensions.addAll(this.getCompleteExtensions(dungTheory,ext2, remaining2));
			}
		}
		return extensions;		
	}
}
