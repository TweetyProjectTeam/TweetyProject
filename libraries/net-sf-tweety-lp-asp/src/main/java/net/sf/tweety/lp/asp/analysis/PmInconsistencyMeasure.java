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
package net.sf.tweety.lp.asp.analysis;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.util.IncreasingSubsetIterator;
import net.sf.tweety.commons.util.SubsetIterator;
import net.sf.tweety.logics.commons.analysis.InconsistencyMeasure;
import net.sf.tweety.logics.fol.semantics.HerbrandBase;
import net.sf.tweety.logics.fol.syntax.FolAtom;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.lp.asp.reasoner.ASPSolver;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.syntax.ASPAtom;
import net.sf.tweety.lp.asp.syntax.StrictNegation;
import net.sf.tweety.lp.asp.syntax.ASPRule;

/**
 * This class implements the inconsistency measure $I_\pm$ from [Ulbricht,
 * Thimm, Brewka. Measuring Inconsistency in Answer Set Programs. JELIA
 * 2016]<br/>
 * The implememtation is a straightforward brute-force search approach.
 * 
 * @author Matthias Thimm
 *
 */
public class PmInconsistencyMeasure implements InconsistencyMeasure<Program> {

	/** The ASP solver used for determining inconsistency */
	private ASPSolver solver;

	/**
	 * Creates a new inconsistency measure based on the given solver.
	 * 
	 * @param solver
	 *            some ASP solver
	 */
	public PmInconsistencyMeasure(ASPSolver solver) {
		this.solver = solver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.logics.commons.analysis.InconsistencyMeasure#
	 * inconsistencyMeasure(net.sf.tweety.commons.BeliefBase)
	 */
	@Override
	public Double inconsistencyMeasure(Program beliefBase) {
		if (!beliefBase.isGround())
			throw new RuntimeException("Measure only defined for ground programs.");

		if (solver.getModel(beliefBase).size() > 0)
			return 0d;
		int min = Integer.MAX_VALUE;
		SubsetIterator<ASPRule> it_del = new IncreasingSubsetIterator<ASPRule>(beliefBase);
		Set<ASPRule> allFacts = new HashSet<ASPRule>();
		FolSignature sig = beliefBase.getSignature();
		for (FolAtom a : new HerbrandBase(sig).getAtoms()) {
			allFacts.add(new ASPRule(new ASPAtom(a)));
			allFacts.add(new ASPRule(new StrictNegation(new ASPAtom(a))));
		}
		while (it_del.hasNext()) {
			SubsetIterator<ASPRule> it_add = new IncreasingSubsetIterator<ASPRule>(allFacts);
			Set<ASPRule> del = it_del.next();
			// if we already have a result better than what we remove now, we are finished
			if (del.size() > min)
				return new Double(min);
			while (it_add.hasNext()) {
				Set<ASPRule> add = it_add.next();
				// only test if we can improve a previous change
				if (del.size() + add.size() < min) {
					Program p = beliefBase.clone();
					p.removeAll(del);
					p.addAll(add);
					if (solver.getModel(p).size() > 0)
						min = del.size() + add.size();
				}
			}
		}
		return new Double(min);
	}
}
