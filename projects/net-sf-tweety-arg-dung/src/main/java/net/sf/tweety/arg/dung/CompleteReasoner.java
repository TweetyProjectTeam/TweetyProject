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
import net.sf.tweety.logics.pl.syntax.Contradiction;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.Tautology;


/**
 * This reasoner for Dung theories performs inference on the complete extensions.
 * Computes the set of all complete extensions, i.e., all admissible sets that contain all their acceptable arguments.
 * @author Matthias Thimm
 *
 */
public class CompleteReasoner extends AbstractExtensionReasoner {

	/**
	 * Creates a new complete reasoner for the given knowledge base.
	 * @param beliefBase a knowledge base.
	 * @param inferenceType The inference type for this reasoner.
	 */
	public CompleteReasoner(BeliefBase beliefBase, int inferenceType){
		super(beliefBase, inferenceType);		
	}
	
	/**
	 * Creates a new complete reasoner for the given knowledge base using sceptical inference.
	 * @param beliefBase The knowledge base for this reasoner.
	 */
	public CompleteReasoner(BeliefBase beliefBase){
		super(beliefBase);		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.dung.AbstractExtensionReasoner#computeExtensions()
	 */
	public Set<Extension> computeExtensions(){
		Extension groundedExtension = new GroundReasoner(this.getKnowledgBase(),this.getInferenceType()).getExtensions().iterator().next();
		Set<Argument> remaining = new HashSet<Argument>((DungTheory)this.getKnowledgBase());
		remaining.removeAll(groundedExtension);
		return this.getCompleteExtensions(groundedExtension,remaining);
	}

	/**
	 * Auxiliary method to compute all complete extensions
	 * @param arguments a set of arguments
	 * @param remaining arguments that still have to be considered to be part of an extension
	 * @return all complete extensions that are supersets of an argument in <source>arguments</source>
	 */
	private Set<Extension> getCompleteExtensions(Extension ext, Collection<Argument> remaining){
		Set<Extension> extensions = new HashSet<Extension>();
		DungTheory dungTheory = (DungTheory) this.getKnowledgBase();
		if(ext.isConflictFree(dungTheory)){
			if(dungTheory.faf(ext).equals(ext))
				extensions.add(ext);
			if(!remaining.isEmpty()){
				Argument arg = remaining.iterator().next();
				Collection<Argument> remaining2 = new HashSet<Argument>(remaining);
				remaining2.remove(arg);
				extensions.addAll(this.getCompleteExtensions(ext, remaining2));
				Extension ext2 = new Extension(ext);
				ext2.add(arg);
				extensions.addAll(this.getCompleteExtensions(ext2, remaining2));
			}
		}
		return extensions;		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.dung.AbstractExtensionReasoner#getPropositionalCharacterisationBySemantics(java.util.Map, java.util.Map, java.util.Map)
	 */
	@Override
	protected PlBeliefSet getPropositionalCharacterisationBySemantics(Map<Argument, Proposition> in, Map<Argument, Proposition> out,Map<Argument, Proposition> undec) {
		DungTheory theory = (DungTheory) this.getKnowledgBase();
		PlBeliefSet beliefSet = new PlBeliefSet();
		// an argument is in iff all attackers are out
		for(Argument a: theory){
			PropositionalFormula attackersAnd = new Tautology();
			PropositionalFormula attackersOr = new Contradiction();
			PropositionalFormula attackersNotAnd = new Tautology();
			PropositionalFormula attackersNotOr = new Contradiction();
			for(Argument b: theory.getAttackers(a)){
				attackersAnd = attackersAnd.combineWithAnd(out.get(b));
				attackersOr = attackersOr.combineWithOr(in.get(b));
				attackersNotAnd = attackersNotAnd.combineWithAnd(in.get(b).complement());
				attackersNotOr = attackersNotOr.combineWithOr(out.get(b).complement());
			}
			beliefSet.add(((PropositionalFormula)out.get(a).complement()).combineWithOr(attackersOr));
			beliefSet.add(((PropositionalFormula)in.get(a).complement()).combineWithOr(attackersAnd));
			beliefSet.add(((PropositionalFormula)undec.get(a).complement()).combineWithOr(attackersNotAnd));
			beliefSet.add(((PropositionalFormula)undec.get(a).complement()).combineWithOr(attackersNotOr));
		}		
		return beliefSet;
	}
	
}
