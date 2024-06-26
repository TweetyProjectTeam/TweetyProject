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
package org.tweetyproject.arg.saf.reasoner;

import java.util.*;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.*;
import org.tweetyproject.arg.dung.syntax.*;
import org.tweetyproject.arg.saf.syntax.*;
import org.tweetyproject.commons.*;
import org.tweetyproject.logics.pl.syntax.*;


/**
 * This class models an abstract reasoner for structured argumentation frameworks. Given a specific
 * semantics "Sem" for Dung theories, inferences drawn using this reasoner bases on a set "output" of
 * propositions defined by:<br> 
 * Output = { a |(forall i there is an AS in E_i: claim(AS)=A)}<br>
 * where E_1,...,E_n are the extensions of the induced Dung theory wrt. semantics "Sem".
 * 
 * @author Matthias Thimm
 */
public class OutputReasoner implements QualitativeReasoner<StructuredArgumentationFramework,Proposition> {

	/**
	 * The output of this reasoner.
	 */
	private Set<Proposition> output;
	
	/**
	 * The reasoner used for computing the extensions of the induced Dung theory.
	 */
	private AbstractExtensionReasoner reasoner;
	
	/**
	 * Creates a new reasoner
	 * @param reasoner and abstract extension reasoner
	 */
	public OutputReasoner(AbstractExtensionReasoner reasoner) {
		this.reasoner = reasoner;					
	}

	
	/**
	 * Returns the output this reasoner bases upon.
	 * @param saf an SAF
	 * @return the output this reasoner bases upon.
	 */
	public Set<Proposition> getOutput(StructuredArgumentationFramework saf){
		if(this.output == null){
			Collection<Extension<DungTheory>> extensions = this.reasoner.getModels(saf.toDungTheory());			
			this.output = new HashSet<Proposition>();			
			for(Proposition p: saf.getAllPropositions()){
				boolean isOutput = true;
				for(Extension<DungTheory> e: extensions){
					boolean isInExtension = false;
					for(Argument a: e){
						ArgumentStructure arg = (ArgumentStructure) a; 
						if(arg.getClaim().equals(p)){
							isInExtension = true;
							break;
						}
					}
					if(!isInExtension){
						isOutput = false;
						break;
					}
				}
				if(isOutput) this.output.add(p);
			}
		}
		return this.output;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.QualitativeReasoner#query(org.tweetyproject.commons.BeliefBase, org.tweetyproject.commons.Formula)
	 */
	@Override
	public Boolean query(StructuredArgumentationFramework beliefbase, Proposition formula) {
		return this.getOutput(beliefbase).contains(formula);
	}


	@Override
	public boolean isInstalled() {
		return true;
	}
}
