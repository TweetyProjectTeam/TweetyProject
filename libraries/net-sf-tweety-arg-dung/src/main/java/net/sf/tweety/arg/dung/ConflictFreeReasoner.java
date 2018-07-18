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
package net.sf.tweety.arg.dung;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.util.SetTools;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * This reasoner for Dung theories performs inference on the conflict-free extensions.
 * @author Matthias Thimm
 *
 */
public class ConflictFreeReasoner extends AbstractExtensionReasoner {

	/**
	 * Creates a new conflict-free reasoner for the given knowledge base.
	 * @param beliefBase a knowledge base.
	 * @param inferenceType The inference type for this reasoner.
	 */
	public ConflictFreeReasoner(int inferenceType){
		super(inferenceType);		
	}
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.AbstractExtensionReasoner#getExtensions(net.sf.tweety.arg.dung.DungTheory)
	 */
	public Set<Extension> getExtensions(DungTheory theory){
		Set<Extension> extensions = new HashSet<Extension>();
		// Check all subsets
		for(Set<Argument> ext: new SetTools<Argument>().subsets(theory))
			if(new Extension(ext).isConflictFree(theory))
				extensions.add(new Extension(ext));
		return extensions;
	}


	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.AbstractExtensionReasoner#getPropositionalCharacterisationBySemantics(net.sf.tweety.arg.dung.DungTheory, java.util.Map, java.util.Map, java.util.Map)
	 */
	@Override
	protected PlBeliefSet getPropositionalCharacterisationBySemantics(DungTheory theory, Map<Argument, Proposition> in, Map<Argument, Proposition> out, Map<Argument, Proposition> undec) {
		throw new UnsupportedOperationException("Implement me!");
	}
}
