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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.reasoner;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.util.Cache;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Equivalence;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * 
 * @author Mathias Hofer
 *
 */
public class SatModelReasoner extends AbstractDialecticalFrameworkReasoner {

	/**
	 * A SAT solver
	 */
	private SatSolver solver;

	public SatModelReasoner(SatSolver solver) {
		this.solver = solver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner#getModels(net.
	 * sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public Collection<Interpretation> getModels(AbstractDialecticalFramework bbase) {
		Cache<Argument, PlFormula> cache = new Cache<Argument, PlFormula>(arg -> new Proposition(arg.getName()));
		PlBeliefSet encoding = this.getPropositionalCharacterisation(bbase, cache);
		Set<Interpretation> result = new HashSet<Interpretation>();

		net.sf.tweety.commons.Interpretation<PlBeliefSet, PlFormula> witness = null;
		while ((witness = this.solver.getWitness(encoding)) != null) {
			Collection<PlFormula> literals = new HashSet<PlFormula>();

			// we build a (complete) two-valued interpretation
			Map<Argument, Boolean> model = new HashMap<Argument, Boolean>();
			for (Argument a : bbase) {
				PlFormula prop = a.toPlFormula(cache);
				if (witness.satisfies(prop)) {
					literals.add(prop);
					model.put(a, true);
				} else {
					model.put(a, false);
					literals.add(new Negation(prop));
				}
			}
			result.add(new Interpretation(model));

			// add the newly found witness in negative form to prop
			// so the next witness cannot be the same
			encoding.add(new Negation(new Conjunction(literals)));
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner#getModel(net.sf
	 * .tweety.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Interpretation getModel(AbstractDialecticalFramework bbase) {
		Cache<Argument, PlFormula> cache = new Cache<Argument, PlFormula>(arg -> new Proposition(arg.getName()));
		PlBeliefSet prop = this.getPropositionalCharacterisation(bbase, cache);
		net.sf.tweety.commons.Interpretation<PlBeliefSet, PlFormula> witness = this.solver.getWitness(prop);
		Map<Argument, Boolean> model = new HashMap<Argument, Boolean>();
		// we build a (complete) two-valued interpretation
		for (Argument a : bbase) {
			if (witness.satisfies(a.toPlFormula(cache))) {
				model.put(a, true);
			} else {
				model.put(a, false);
			}
		}
		return new Interpretation(model); // the first found model
	}

	/** 
	 * returns a propositional characterisation of the reasoning problem
	 * @param aaf some ADF
	 * @param cache the cache
	 * @return The propositional encoding of the model semantics.
	 */
	public PlBeliefSet getPropositionalCharacterisation(AbstractDialecticalFramework aaf,
			Cache<Argument, PlFormula> cache) {
		PlBeliefSet beliefSet = new PlBeliefSet();
		for (Argument a : aaf) {
			Equivalence equiv = new Equivalence(a.toPlFormula(cache), aaf.getAcceptanceCondition(a).toPlFormula(cache));
			beliefSet.add(equiv);
		}
		return beliefSet;
	}

}
