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
package net.sf.tweety.arg.dung;

import java.util.*;

import net.sf.tweety.arg.dung.semantics.*;
import net.sf.tweety.arg.dung.syntax.*;
import net.sf.tweety.commons.*;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;


/**
 * This reasoner for Dung theories performs inference on the preferred extensions.
 * Computes the set of all preferred extensions, i.e., all maximal admissable sets.
 * @author Matthias Thimm
 *
 */
public class PreferredReasoner extends AbstractExtensionReasoner {

	/**
	 * Creates a new preferred reasoner for the given knowledge base.
	 * @param beliefBase a knowledge base.
	 * @param inferenceType The inference type for this reasoner.
	 */
	public PreferredReasoner(BeliefBase beliefBase, int inferenceType){
		super(beliefBase, inferenceType);		
	}
	
	/**
	 * Creates a new preferred reasoner for the given knowledge base using sceptical inference.
	 * @param beliefBase The knowledge base for this reasoner.
	 */
	public PreferredReasoner(BeliefBase beliefBase){
		super(beliefBase);		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.dung.AbstractExtensionReasoner#computeExtensions()
	 */
	protected Set<Extension> computeExtensions(){		
		return this.getPreferredExtensions(new Extension());		
	}
	
	/**
	 * Auxiliary method to compute the set of all preferred extensions.
	 * @param arguments
	 */
	private Set<Extension> getPreferredExtensions(Extension ext){
		Set<Extension> extensions = new HashSet<Extension>();
		DungTheory dungTheory = (DungTheory) this.getKnowledgBase();
		Extension ext2 = new Extension();
		for(Formula f: dungTheory)
			ext2.add((Argument) f);		
		ext2.removeAll(ext);
		Iterator<Argument> it = ext2.iterator();
		boolean isMaximal = true;
		while(it.hasNext()){
			Argument argument =it.next();
			Extension ext3 = new Extension(ext);
			ext3.add(argument);
			Set<Extension> extensions2 = this.getPreferredExtensions(ext3);
			if(extensions2.size()>0){
				isMaximal = false;
				extensions.addAll(extensions2);
			}
		}
		if(isMaximal && ext.isAdmissable(dungTheory))
				extensions.add(ext);
		return extensions;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.dung.AbstractExtensionReasoner#getPropositionalCharacterisationBySemantics(java.util.Map, java.util.Map, java.util.Map)
	 */
	@Override
	protected PlBeliefSet getPropositionalCharacterisationBySemantics(Map<Argument, Proposition> in, Map<Argument, Proposition> out,Map<Argument, Proposition> undec) {
		throw new UnsupportedOperationException("not defined for preferred semantics");
	}
}
