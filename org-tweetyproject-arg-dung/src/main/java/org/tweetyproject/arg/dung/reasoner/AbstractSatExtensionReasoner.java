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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * Uses a provided SAT solver to solve reasoning problems in AAFs.
 * @author Matthias Thimm
 */
public abstract class AbstractSatExtensionReasoner extends AbstractExtensionReasoner{
	
	/**
	 * A SAT solver 
	 */
	protected SatSolver solver;
	
	/**
	 * Instantiates a new reasoner that uses the given SAT solver
	 * @param solver some AT solver
	 */
	public AbstractSatExtensionReasoner(SatSolver solver) {
		this.solver = solver;
	}
		
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModels(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Collection<Extension> getModels(DungTheory bbase) {		
		PlBeliefSet prop = this.getPropositionalCharacterisation(bbase);
		// get some labeling from the solver, then add the negation of this to the program and repeat
		// to obtain all labelings
		Set<Extension> result = new HashSet<Extension>();
		Extension ext;
		do{
			PossibleWorld w = (PossibleWorld) this.solver.getWitness(prop);
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
			Collection<PlFormula> f = new HashSet<PlFormula>();
			for(Proposition p: w)
				f.add(p);
			prop.add(new Negation(new Conjunction(f)));
		}while(true);
		return result;
	}
	
	/**
	 * Creates a propositional representation of the set of labelings of the given
	 * Dung theory that are consistent with the given semantics. This means that
	 * for every argument A in the theory three propositions are created: in_A, out_A,
	 * undec_A. For every attack A-&gt;B the formula "in_A =&gt; out_B" is added to the belief set.
	 * Depending on the actual semantics further propositional formulas are added. For example,
	 * for any admissable semantics and unattacked argument A, the constraint "\top=&gt;in_A" is added;
	 * another constraint added for admissable semantics is, given any argument A and attackers B1...BN,
	 * add the constraint in_A =&gt; out_B1 ^ ... ^ out_BN.
	 * @param aaf a Dung Thery    
	 * @return a propositional belief set.
	 */
	public PlBeliefSet getPropositionalCharacterisation(DungTheory aaf){
		Map<Argument,Proposition> in = new HashMap<Argument,Proposition>();
		Map<Argument,Proposition> out = new HashMap<Argument,Proposition>();
		Map<Argument,Proposition> undec = new HashMap<Argument,Proposition>();
		PlBeliefSet beliefSet = new PlBeliefSet();
		for(Argument a: aaf){
			in.put(a, new Proposition("in_" + a.getName()));
			out.put(a, new Proposition("out_" + a.getName()));
			undec.put(a, new Proposition("undec_" + a.getName()));
			// for every argument only one of in/out/undec can be true
			beliefSet.add(in.get(a).combineWithOr(out.get(a).combineWithOr(undec.get(a))));
			beliefSet.add((PlFormula)in.get(a).combineWithAnd(out.get(a)).complement());
			beliefSet.add((PlFormula)in.get(a).combineWithAnd(undec.get(a)).complement());
			beliefSet.add((PlFormula)out.get(a).combineWithAnd(undec.get(a)).complement());
		}
		beliefSet.addAll(this.getPropositionalCharacterisationBySemantics(aaf,in,out,undec));
		return beliefSet;
	}
	
	/**
	 * Returns the semantic-specific propositional characterization of the underlying Dung
	 * theory, see <code>getPropositionalCharacterisation</code>. 
	 * @param aaf the Dung theory
	 * @param in propositional variables of in arguments.
	 * @param out propositional variables of out arguments.
	 * @param undec propositional variables of undec arguments.
	 * @return the semantic-specific propositional characterization of the underlying Dung
	 * theory, see <code>getPropositionalCharacterisation</code>.
	 */
	protected abstract PlBeliefSet getPropositionalCharacterisationBySemantics(DungTheory aaf, Map<Argument,Proposition> in, Map<Argument,Proposition> out, Map<Argument,Proposition> undec);

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Extension getModel(DungTheory bbase) {
		// returns the first found model
		PlBeliefSet prop = this.getPropositionalCharacterisation(bbase);
		PossibleWorld w = (PossibleWorld) this.solver.getWitness(prop);
		if(w == null)
			return null;
		Extension ext = new Extension();
		for(Proposition p: w){
			if(p.getName().startsWith("in_"))
				ext.add(new Argument(p.getName().substring(3)));				
		}
		return ext;
	}
}
