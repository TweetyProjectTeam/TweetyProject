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

import java.util.*;

import net.sf.tweety.arg.dung.semantics.*;
import net.sf.tweety.arg.dung.syntax.*;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;


/**
 * This reasoner for Dung theories performs inference on the complete extensions.
 * Computes the set of all complete extensions, i.e., all admissible sets that contain all their acceptable arguments.
 * @author Matthias Thimm
 *
 */
public class CompleteReasoner extends AbstractExtensionReasoner {

	/** Whether to use the standard SAT solver instead of the brute force approach. */
	private boolean useSatSolver = true;
	
	/**
	 * Creates a new complete reasoner.
	 * @param inferenceType The inference type for this reasoner.
	 */
	public CompleteReasoner(int inferenceType){
		super(inferenceType);		
	}
	
	/**
	 * Creates a new complete reasoner for the given knowledge base using sceptical inference.
	 * @param inferenceType The inference type for this reasoner.
	 * @param useSatSolver whether to use the standard SAT solver instead of the brute force approach.
	 */
	public CompleteReasoner(int inferenceType, boolean useSatSolver){
		super(inferenceType);
		this.useSatSolver = useSatSolver;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.AbstractExtensionReasoner#getExtensions(net.sf.tweety.arg.dung.DungTheory)
	 */
	public Set<Extension> getExtensions(DungTheory aaf){
		if(this.useSatSolver)
			return this.computeExtensionsBySatSolving(aaf);
		Extension groundedExtension = new GroundReasoner(this.getInferenceType()).getExtensions(aaf).iterator().next();
		Set<Argument> remaining = new HashSet<Argument>(aaf);
		remaining.removeAll(groundedExtension);
		return this.getCompleteExtensions(aaf,groundedExtension,remaining);
	}

	/**
	 * Computes the extensions by reducing the problem to SAT solving
	 * @param DungTheory an AAF
	 * @return the extensions of the given Dung theory.
	 */
	protected Set<Extension> computeExtensionsBySatSolving(DungTheory aaf){
		SatSolver solver = SatSolver.getDefaultSolver();
		PlBeliefSet prop = this.getPropositionalCharacterisation(aaf);
		// get some labeling from the solver, then add the negation of this to the program and repeat
		// to obtain all labelings
		Set<Extension> result = new HashSet<Extension>();
		Extension ext;
		do{
			PossibleWorld w = (PossibleWorld) solver.getWitness(prop);
			if(w == null)
				break;
			ext = new Extension();
			for(Proposition p: w){
				if(p.getName().startsWith("in_"))
					ext.add(new Argument(p.getName().substring(3)));				
			}
			result.add(ext);
			// add the newly found extension in negative form to prop
			// so the next witness cannot be the same
			Collection<PropositionalFormula> f = new HashSet<PropositionalFormula>();
			for(Proposition p: w)
				f.add(p);
			prop.add(new Negation(new Conjunction(f)));
		}while(true);
		return result;
	}
	
	/**
	 * Auxiliary method to compute all complete extensions
	 * @param arguments a set of arguments
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
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.dung.AbstractExtensionReasoner#getPropositionalCharacterisationBySemantics(java.util.Map, java.util.Map, java.util.Map)
	 */
	@Override
	protected PlBeliefSet getPropositionalCharacterisationBySemantics(DungTheory theory,Map<Argument, Proposition> in, Map<Argument, Proposition> out,Map<Argument, Proposition> undec) {
		PlBeliefSet beliefSet = new PlBeliefSet();
		// an argument is in iff all attackers are out
		for(Argument a: theory){
			if(theory.getAttackers(a).isEmpty()){
				beliefSet.add(((PropositionalFormula)in.get(a)));
			}else{
				Collection<PropositionalFormula> attackersAnd = new HashSet<PropositionalFormula>();//new Tautology();
				Collection<PropositionalFormula> attackersOr = new HashSet<PropositionalFormula>();//new Contradiction();
				Collection<PropositionalFormula> attackersNotAnd = new HashSet<PropositionalFormula>();//new Tautology();
				Collection<PropositionalFormula> attackersNotOr = new HashSet<PropositionalFormula>();//new Contradiction();
				for(Argument b: theory.getAttackers(a)){
					attackersAnd.add(out.get(b));
					attackersOr.add(in.get(b));
					attackersNotAnd.add((PropositionalFormula)in.get(b).complement());
					attackersNotOr.add((PropositionalFormula)out.get(b).complement());
				}
				beliefSet.add(((PropositionalFormula)out.get(a).complement()).combineWithOr(new Disjunction(attackersOr)));
				beliefSet.add(((PropositionalFormula)in.get(a).complement()).combineWithOr(new Conjunction(attackersAnd)));
				beliefSet.add(((PropositionalFormula)undec.get(a).complement()).combineWithOr(new Conjunction(attackersNotAnd)));
				beliefSet.add(((PropositionalFormula)undec.get(a).complement()).combineWithOr(new Disjunction(attackersNotOr)));
			}
		}		
		return beliefSet;
	}
	
}
