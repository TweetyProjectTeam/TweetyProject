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
package org.tweetyproject.lp.asp.analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.commons.util.DefaultSubsetIterator;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.commons.util.SubsetIterator;
import org.tweetyproject.logics.commons.analysis.InconsistencyMeasure;
import org.tweetyproject.logics.fol.semantics.HerbrandBase;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.lp.asp.reasoner.ASPSolver;
import org.tweetyproject.lp.asp.semantics.AnswerSet;
import org.tweetyproject.lp.asp.syntax.ASPAtom;
import org.tweetyproject.lp.asp.syntax.ASPLiteral;
import org.tweetyproject.lp.asp.syntax.StrictNegation;
import org.tweetyproject.lp.asp.syntax.Program;

/**
 * This class implements the inconsistency measure $I_sd$ from
 * [Ulbricht, Thimm, Brewka. Measuring Inconsistency in Answer Set Programs. JELIA 2016]<br>
 * The implememtation is a straightforward brute-force search approach.
 * 
 * @author Matthias Thimm
 *
 */
public class SdInconsistencyMeasure implements InconsistencyMeasure<Program>{

	/** The ASP solver used for determining inconsistency */
	private ASPSolver solver;
		
	/**
	 * Creates a new inconsistency measure based on the given
	 * solver. 
	 * @param solver some ASP solver
	 */
	public SdInconsistencyMeasure(ASPSolver solver){
		this.solver = solver;
	}
	
	/**
	 * Checks whether the given set of literals is consistent, i.e.
	 * does not contain two complementary literals.
	 * @param c some set
	 * @return "true" if the set does not contain two complementary literals
	 */
	private boolean isConsistent(Collection<ASPLiteral> c){
		for(ASPLiteral l: c){
			if(l instanceof StrictNegation)
				if(c.contains(((StrictNegation)l).getAtom()))
					return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.InconsistencyMeasure#inconsistencyMeasure(org.tweetyproject.commons.BeliefBase)
	 */
	@Override
	public Double inconsistencyMeasure(Program beliefBase) {
		if(!beliefBase.isGround())
			throw new RuntimeException("Measure only defined for ground programs.");
		try {
			if(solver.getModel(beliefBase).size() > 0)
				return 0d;
			Set<ASPLiteral> allLiterals = new HashSet<ASPLiteral>();
			FolSignature sig = beliefBase.getMinimalSignature();
			for(FolAtom a: new HerbrandBase(sig).getAtoms()){
				allLiterals.add(new ASPAtom(a));
				allLiterals.add(new StrictNegation(new ASPAtom(a)));
			}	
			Double result = Double.POSITIVE_INFINITY;
			SubsetIterator<ASPLiteral> lit_it = new DefaultSubsetIterator<>(allLiterals);
			while(lit_it.hasNext()){
				Set<ASPLiteral> m = lit_it.next();
				//skip inconsistent m
				if(!this.isConsistent(m)) continue;
				Program p = beliefBase.getReduct(m);
				Collection<AnswerSet> asl = this.solver.getModels(p);
				if(asl.size() == 0) continue;
				int val = (new SetTools<ASPLiteral>()).symmetricDifference(m, asl.iterator().next()).size();
				if(val < result)
					result = Double.valueOf(val);				
			}			
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
