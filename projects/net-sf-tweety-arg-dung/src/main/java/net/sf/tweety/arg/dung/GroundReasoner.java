/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.dung;

import java.util.*;

import net.sf.tweety.arg.dung.semantics.*;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.*;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;


/**
 * This reasoner for Dung theories performs inference on the grounded extension.
 * Computes the (unique) grounded extension, i.e., the least fixpoint of the characteristic function faf.
 * 
 * @author Matthias Thimm
 *
 */
public class GroundReasoner extends AbstractExtensionReasoner {

	/**
	 * Creates a new ground reasoner for the given knowledge base.
	 * @param beliefBase a knowledge base.
	 * @param inferenceType The inference type for this reasoner.
	 */
	public GroundReasoner(BeliefBase beliefBase, int inferenceType){
		super(beliefBase, inferenceType);		
	}
	
	/**
	 * Creates a new ground reasoner for the given knowledge base using sceptical inference.
	 * @param beliefBase The knowledge base for this reasoner.
	 */
	public GroundReasoner(BeliefBase beliefBase){
		super(beliefBase);		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.dung.AbstractExtensionReasoner#computeExtensions()
	 */
	protected Set<Extension> computeExtensions(){		
		Extension ext = new Extension();
		int size;
		do{
			size = ext.size();			
			ext = ((DungTheory)this.getKnowledgBase()).faf(ext);			
		}while(size!=ext.size());
		Set<Extension> extensions = new HashSet<Extension>();
		extensions.add(ext);
		return extensions;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.dung.AbstractExtensionReasoner#getPropositionalCharacterisationBySemantics(java.util.Map, java.util.Map, java.util.Map)
	 */
	@Override
	protected PlBeliefSet getPropositionalCharacterisationBySemantics(Map<Argument, Proposition> in, Map<Argument, Proposition> out,Map<Argument, Proposition> undec) {
		throw new UnsupportedOperationException("not defined for grounded semantics");
	}
	
}
