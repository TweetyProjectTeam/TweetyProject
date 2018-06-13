/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.lp.asp.analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.util.DefaultSubsetIterator;
import net.sf.tweety.commons.util.SetTools;
import net.sf.tweety.commons.util.SubsetIterator;
import net.sf.tweety.logics.commons.analysis.InconsistencyMeasure;
import net.sf.tweety.logics.fol.semantics.HerbrandBase;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.lp.asp.semantics.AnswerSetList;
import net.sf.tweety.lp.asp.solver.Solver;
import net.sf.tweety.lp.asp.solver.SolverException;
import net.sf.tweety.lp.asp.syntax.DLPAtom;
import net.sf.tweety.lp.asp.syntax.DLPLiteral;
import net.sf.tweety.lp.asp.syntax.DLPNeg;
import net.sf.tweety.lp.asp.syntax.Program;

/**
 * This class implements the inconsistency measure $I_sd$ from
 * [Ulbricht, Thimm, Brewka. Measuring Inconsistency in Answer Set Programs. JELIA 2016]<br/>
 * The implememtation is a straightforward brute-force search approach.
 * 
 * @author Matthias Thimm
 *
 */
public class SdInconsistencyMeasure implements InconsistencyMeasure<Program>{

	/** The ASP solver used for determining inconsistency */
	private Solver solver;
		
	/**
	 * Creates a new inconsistency measure based on the given
	 * solver. 
	 * @param solver some ASP solver
	 */
	public SdInconsistencyMeasure(Solver solver){
		this.solver = solver;
	}
	
	/**
	 * Checks whether the given set of literals is consistent, i.e.
	 * does not contain two complementary literals.
	 * @param c some set
	 * @return "true" if the set does not contain two complementary literals
	 */
	private boolean isConsistent(Collection<DLPLiteral> c){
		for(DLPLiteral l: c){
			if(l instanceof DLPNeg)
				if(c.contains(((DLPNeg)l).getAtom()))
					return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.InconsistencyMeasure#inconsistencyMeasure(net.sf.tweety.commons.BeliefBase)
	 */
	@Override
	public Double inconsistencyMeasure(Program beliefBase) {
		if(!beliefBase.isGround())
			throw new RuntimeException("Measure only defined for ground programs.");
		try {
			if(solver.computeModels(beliefBase, 1).size() > 0)
				return 0d;
			Set<DLPLiteral> allLiterals = new HashSet<DLPLiteral>();
			FolSignature sig = beliefBase.getSignature();
			for(FOLAtom a: new HerbrandBase(sig).getAtoms()){
				allLiterals.add(new DLPAtom(a));
				allLiterals.add(new DLPNeg(new DLPAtom(a)));
			}	
			Double result = Double.POSITIVE_INFINITY;
			SubsetIterator<DLPLiteral> lit_it = new DefaultSubsetIterator<>(allLiterals);
			while(lit_it.hasNext()){
				Set<DLPLiteral> m = lit_it.next();
				//skip inconsistent m
				if(!this.isConsistent(m)) continue;
				Program p = beliefBase.reduct(m);
				AnswerSetList asl = this.solver.computeModels(p, 1);
				if(asl.size() == 0) continue;
				int val = (new SetTools<DLPLiteral>()).symmetricDifference(m, asl.get(0)).size();
				if(val < result)
					result = new Double(val);				
			}			
			return result;
		} catch (SolverException e) {
			throw new RuntimeException(e);
		}
	}
}
