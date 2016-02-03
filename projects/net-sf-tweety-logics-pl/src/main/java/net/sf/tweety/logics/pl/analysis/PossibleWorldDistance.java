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
package net.sf.tweety.logics.pl.analysis;

import java.util.Set;

import net.sf.tweety.commons.analysis.InterpretationDistance;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * This class refines interpretation distances to distance on possible worlds. It add
 * some functionalities to measure distance of formulas to possible worlds.
 * @author Matthias Thimm
 *
 */
public abstract class PossibleWorldDistance implements InterpretationDistance<PossibleWorld,PropositionalFormula> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.analysis.InterpretationDistance#distance(net.sf.tweety.commons.Interpretation, net.sf.tweety.commons.Interpretation)
	 */
	@Override
	public abstract double distance(PossibleWorld a, PossibleWorld b);

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.analysis.InterpretationDistance#distance(net.sf.tweety.commons.Formula, net.sf.tweety.commons.Interpretation)
	 */
	@Override
	public double distance(PropositionalFormula f, PossibleWorld b){
		// remove all propositions from b not appearing in f, they have no influence on the distance
		// NOTE: this may not be true for every imaginable distance between possible worlds, but it
		//	is true for e.g. the Dalal distance 
		PossibleWorld b2 = new PossibleWorld();
		for(Proposition p: b)
			if(f.getSignature().contains(p))
				b2.add(p);
		// get models
		Set<PossibleWorld> models = f.getModels();
		// compute minimal distance
		double dist = Double.POSITIVE_INFINITY;
		for(PossibleWorld w: models){
			if(this.distance(w, b2) < dist)
				dist = this.distance(w, b2);
		}
		// Note that the distance is maximal if there is no model.
		return dist;
	}
}
