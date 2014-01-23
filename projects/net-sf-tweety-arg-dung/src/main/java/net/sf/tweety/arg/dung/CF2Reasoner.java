package net.sf.tweety.arg.dung;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.BeliefBase;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * This reasoner for Dung theories performs inference on the CF2 extensions.
 * @author Matthias Thimm
 *
 */
public class CF2Reasoner extends AbstractExtensionReasoner {

	/**
	 * Creates a new CF2 reasoner for the given knowledge base.
	 * @param beliefBase a knowledge base.
	 * @param inferenceType The inference type for this reasoner.
	 */
	public CF2Reasoner(BeliefBase beliefBase, int inferenceType){
		super(beliefBase, inferenceType);		
	}
	
	/**
	 * Creates a new CF2 reasoner for the given knowledge base using sceptical inference.
	 * @param beliefBase The knowledge base for this reasoner.
	 */
	public CF2Reasoner(BeliefBase beliefBase){
		super(beliefBase);		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.dung.AbstractExtensionReasoner#computeExtensions()
	 */
	public Set<Extension> computeExtensions(){
		Collection<Collection<Argument>> sccs = ((DungTheory)this.getKnowledgBase()).getStronglyConnectedComponents();
		if(sccs.size() == 1){
			// an extension for a single scc is a conflict-free set with maximal arguments
			ConflictFreeReasoner reasoner = new ConflictFreeReasoner(this.getKnowledgBase(),this.getInferenceType());
			Set<Extension> result = new HashSet<Extension>();
			Set<Extension> possibleExtensions = reasoner.getExtensions();
			boolean valid;
			for(Extension ext: possibleExtensions){
				valid = true;
				for(Extension ext2: possibleExtensions){
					if(ext2.containsAll(ext)){
						valid = false;
						break;
					}					
				}
				if(valid) 
					result.add(ext);
			}
			return result;
		}else{
			//TODO
		}	
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.AbstractExtensionReasoner#getPropositionalCharacterisationBySemantics(java.util.Map, java.util.Map, java.util.Map)
	 */
	@Override
	protected PlBeliefSet getPropositionalCharacterisationBySemantics(Map<Argument, Proposition> in, Map<Argument, Proposition> out, Map<Argument, Proposition> undec) {
		throw new UnsupportedOperationException("Implement me!");
	}
}
