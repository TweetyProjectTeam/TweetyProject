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
import net.sf.tweety.commons.*;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Contradiction;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.Tautology;


/**
 * This reasoner for Dung theories performs inference on the stable extensions.
 * Computes the set of all stable extensions, i.e., all conflict-free sets that attack each other argument.
 * @author Matthias Thimm
 *
 */
public class StableReasoner extends AbstractExtensionReasoner {

	/**
	 * Creates a new stable reasoner.
	 * @param inferenceType The inference type for this reasoner.
	 */
	public StableReasoner(int inferenceType){
		super(inferenceType);		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.AbstractExtensionReasoner#getExtensions(net.sf.tweety.arg.dung.DungTheory)
	 */
	public Set<Extension> getExtensions(DungTheory theory){	
		Extension ext = new Extension();
		for(Formula f: theory)
			ext.add((Argument) f);
		return this.getStableExtensions(theory, ext);
	}
	
	/**
	 * Auxiliary method to compute the set of all stable extensions
	 * @param theory a Dung theory
	 * @param arguments a set of arguments to be refined to yield a stable extension
	 * @return the set of stable extensions that are a subset of <source>arguments</source>
	 */
	private Set<Extension> getStableExtensions(DungTheory theory, Extension ext){
		Set<Extension> completeExtensions = new SccCompleteReasoner(this.getInferenceType()).getExtensions(theory);
		Set<Extension> result = new HashSet<Extension>();
		for(Extension e: completeExtensions)
			if(theory.isAttackingAllOtherArguments(e))
				result.add(e);
		return result;	
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.dung.AbstractExtensionReasoner#getPropositionalCharacterisationBySemantics(java.util.Map, java.util.Map, java.util.Map)
	 */
	@Override
	protected PlBeliefSet getPropositionalCharacterisationBySemantics(DungTheory theory, Map<Argument, Proposition> in, Map<Argument, Proposition> out,Map<Argument, Proposition> undec) {
		PlBeliefSet beliefSet = new PlBeliefSet();
		// an argument is in iff all attackers are out, and
		// no argument is undecided
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
			beliefSet.add((PropositionalFormula)undec.get(a).complement());
			
		}
		return beliefSet;
	}
	
}
