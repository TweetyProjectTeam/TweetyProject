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
package org.tweetyproject.logics.dl.reasoner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tweetyproject.commons.QualitativeReasoner;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.logics.dl.syntax.DlBeliefSet;
import org.tweetyproject.logics.dl.syntax.DlSignature;
import org.tweetyproject.logics.dl.syntax.Individual;
import org.tweetyproject.logics.dl.syntax.RoleAssertion;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.dl.semantics.DlInterpretation;
import org.tweetyproject.logics.dl.syntax.AssertionalAxiom;
import org.tweetyproject.logics.dl.syntax.AtomicConcept;
import org.tweetyproject.logics.dl.syntax.AtomicRole;
import org.tweetyproject.logics.dl.syntax.ConceptAssertion;
import org.tweetyproject.logics.dl.syntax.DlAxiom;

/**
 * Naive reasoner for the description logic ALC.
 * 
 * @author Anna Gessler
 *
 */
public class NaiveDlReasoner implements QualitativeReasoner<DlBeliefSet,DlAxiom> {
	@Override
	public Boolean query(DlBeliefSet kb, DlAxiom formula) {
		DlSignature sig = new DlSignature();
		sig.addSignature(kb.getMinimalSignature());
		sig.addSignature(formula.getSignature());	
		
		Set<DlInterpretation> interpretations = getAllInterpretations(sig);
		for(DlInterpretation i: interpretations) {
			if(i.satisfies(kb)) 
				if(!i.satisfies(formula)) 
					return false;	 
		}				
		return true;
	}
	
	/**
	 * Get all interpretations for the given signature.
	 * @param sig a DLSignature
	 * @return set of all interpretations
	 */
	public Set<DlInterpretation> getAllInterpretations(DlSignature sig){
		Set<AssertionalAxiom> atoms = new HashSet<AssertionalAxiom>();
		for(Predicate p: sig.getPredicates()){
			atoms.addAll(this.getAllInstantiations(sig, p, new ArrayList<Individual>()));
		}
	
		Set<Set<AssertionalAxiom>> subsets = new SetTools<AssertionalAxiom>().subsets(atoms);
		Set<DlInterpretation> interpretations = new HashSet<DlInterpretation>();
	
		for(Set<AssertionalAxiom> ats: subsets)
			interpretations.add(new DlInterpretation(ats));		
		return interpretations;
	}
	
	/**
	 * Computes all instantiations of the predicate "p" relative to the signature "sig"
	 * where "arguments" defines the first arguments of the atoms.
	 * @param sig a signature for which the instantiations should be computed.
	 * @param p the predicate of the atoms.
	 * @param args the currently set arguments of the atoms.
	 * @return the complete set of instantiations of "p" relative to "sig" and "arguments".
	 */
	private Set<AssertionalAxiom> getAllInstantiations(DlSignature sig, Predicate p, List<Individual> args) {
		if(p.getArity() == args.size()){
			Set<AssertionalAxiom> atoms = new HashSet<AssertionalAxiom>();
			if (p.getArity() == 1)
				atoms.add(new ConceptAssertion(args.get(0),new AtomicConcept(p)));
			else if (p.getArity() == 2)
				atoms.add(new RoleAssertion(args,new AtomicRole(p)));
			else
				throw new IllegalArgumentException("Predicate arity must be 1 (concept) or 2 (role), but was " + p.getArity());
			return atoms;
		}
	
		Set<AssertionalAxiom> atoms = new HashSet<AssertionalAxiom>();
		for(Individual c: sig.getIndividuals()){
			List<Individual> newArgs = new ArrayList<Individual>(args);
			newArgs.add(c);
			atoms.addAll(this.getAllInstantiations(sig, p, newArgs));
		}		
		return atoms;
	}
	
	/**
	 * the solver is natively installed and is therefore always installed
	 */
	@Override
	public boolean isInstalled() {
		return true;
	}
	

}
