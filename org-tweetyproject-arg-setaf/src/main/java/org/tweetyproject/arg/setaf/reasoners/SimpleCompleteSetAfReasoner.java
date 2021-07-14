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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.setaf.reasoners;

import java.util.*;

import org.tweetyproject.arg.setaf.syntax.*;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.*;


/**
 * This reasoner for SetAf theories performs inference on the complete extensions.
 * Computes the set of all complete extensions, i.e., all admissible sets that contain all their acceptable arguments.
 * @author Matthias Thimm, Sebastian Franke
 *
 */
public class SimpleCompleteSetAfReasoner extends AbstractExtensionSetAfReasoner {

	@Override
	public Collection<Extension> getModels(ArgumentationFramework bbase) {
		Extension groundedExtension = new SimpleGroundedSetAfReasoner().getModel(bbase);
		Set<Argument> remaining = new HashSet<Argument>((SetAf)bbase);
		remaining.removeAll(groundedExtension);
		return this.getCompleteExtensions((SetAf)bbase,groundedExtension,remaining);	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.setaf.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.setaf.syntax.DungTheory)
	 */
	@Override
	public Extension getModel(ArgumentationFramework bbase) {
		// as the grounded extension is also complete, we return that one
		return new SimpleGroundedSetAfReasoner().getModel(bbase);
	}
		
	/**
	 * Auxiliary method to compute all complete extensions
	 * @param setafTheory a SetAf theory
	 * @param ext some extension
	 * @param remaining arguments that still have to be considered to be part of an extension
	 * @return all complete extensions that are supersets of an argument in <source>arguments</source>
	 */
	private Set<Extension> getCompleteExtensions(SetAf dungTheory, Extension ext, Collection<Argument> remaining){
		Set<Extension> extensions = new HashSet<Extension>();
		if(dungTheory.isConflictFree(ext)){
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
