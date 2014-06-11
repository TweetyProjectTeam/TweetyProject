package net.sf.tweety.arg.dung;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.util.MapTools;
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
	
	/**
	 * Computes the extensions for the single af case.
	 * @param theory
	 * @return
	 */
	private Set<Extension> singleAFExtensions(DungTheory theory){
		// an extension for a single scc is a conflict-free set with maximal arguments (minimality check is performed later)
		ConflictFreeReasoner reasoner = new ConflictFreeReasoner(theory,this.getInferenceType());
		return reasoner.getExtensions();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.dung.AbstractExtensionReasoner#computeExtensions()
	 */
	public Set<Extension> computeExtensions(){
		Collection<Collection<Argument>> sccs = ((DungTheory)this.getKnowledgBase()).getStronglyConnectedComponents();
		Set<Extension> extensions = new HashSet<Extension>();
		if(sccs.size() == 1){
			// an extension for a single scc is a conflict-free set with maximal arguments
			extensions = this.singleAFExtensions((DungTheory)this.getKnowledgBase());
		}else{
			DungTheory af = (DungTheory) this.getKnowledgBase();
			// for all strongly connected components, restrict the argumentation framework and get the corresponding extensions
			Map<Collection<Argument>,Set<Extension>> exts = new HashMap<Collection<Argument>,Set<Extension>>();
			for(Collection<Argument> scc: sccs){
				Collection<Argument> t = new HashSet<Argument>(scc);
				t.addAll(this.getOutparents(af, scc));
				DungTheory restTheory = new DungTheory(af.getRestriction(t));//this.getUP(af, scc, af));
				Set<Extension> e = this.singleAFExtensions(restTheory);
				exts.put(restTheory, e);
			}
			// combine all extensions from all sccs that are compatible
			MapTools<Collection<Argument>,Extension> mapTools = new MapTools<Collection<Argument>,Extension>();			
			boolean noext;
			for(Map<Collection<Argument>,Extension> map: mapTools.allMapsSingleSource(exts)){
				// two extensions from two theories are compatible if they agree on their shared arguments
				noext = false;
				for(Collection<Argument> key1: map.keySet()){					
					for(Collection<Argument> key2: map.keySet()){
						if(key1 != key2){
							Collection<Argument> intersection = new HashSet<Argument>(key1);
							intersection.retainAll(key2);
							for(Argument arg: intersection){
								if((map.get(key1).contains(arg) && !map.get(key2).contains(arg)) || (!map.get(key1).contains(arg) && map.get(key2).contains(arg))){
									noext = true;
									break;
								}
							}
						}
						if(noext) break;
					}
					if(noext) break;
				}			
				if(!noext){
					Extension extension = new Extension();
					for(Extension e: map.values())
						extension.addAll(e);
					extensions.add(extension);
				}
			}			
		}	
		// do minimality check
		boolean valid;
		Set<Extension> result = new HashSet<Extension>();
		for(Extension ext: extensions){
			valid = true;
			for(Extension ext2: extensions){
				if(ext != ext2 && ext2.containsAll(ext)){
					valid = false;
					break;
				}					
			}
			if(valid) 
				result.add(ext);
		}
		return result;
	}

	/**
	 * Returns the set { a in A | a nicht in S und a -> S }
	 * @param af some Dung theory
	 * @param s a set of arguments
	 * @return a set of arguments
	 */
	private Collection<Argument> getOutparents(DungTheory af, Collection<Argument> s){
		Collection<Argument> result = new HashSet<Argument>();
		for(Argument a: af)
			if(!s.contains(a)){
				if(af.isAttackedBy(a, new Extension(s)))
					result.add(a);				
			}
		return result;
	}	
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.AbstractExtensionReasoner#getPropositionalCharacterisationBySemantics(java.util.Map, java.util.Map, java.util.Map)
	 */
	@Override
	protected PlBeliefSet getPropositionalCharacterisationBySemantics(Map<Argument, Proposition> in, Map<Argument, Proposition> out, Map<Argument, Proposition> undec) {
		throw new UnsupportedOperationException("Implement me!");
	}
}
