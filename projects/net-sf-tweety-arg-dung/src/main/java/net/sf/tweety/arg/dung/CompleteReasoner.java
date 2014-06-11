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
		return this.getCompleteExtensions(groundedExtension);
	}

	/**
	 * Auxiliary method to compute all complete extensions
	 * @param arguments a set of arguments
	 * @return all complete extensions that are supersets of an argument in <source>arguments</source>
	 */
	private Set<Extension> getCompleteExtensions(Extension ext){
		Set<Extension> extensions = new HashSet<Extension>();
		DungTheory dungTheory = (DungTheory) this.getKnowledgBase();
		if(ext.isConflictFree(dungTheory) && dungTheory.faf(ext).equals(ext)){
			extensions.add(ext);
		}
		Extension ext2 = new Extension();
		for(Formula f: dungTheory)
			ext2.add((Argument) f);
		ext2.removeAll(ext);
		Iterator<Argument> it = ext2.iterator();
		while(it.hasNext()){
			Argument argument = it.next();
			Extension ext3 = new Extension(ext);
			ext3.add(argument);
			extensions.addAll(this.getCompleteExtensions(ext3));
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
