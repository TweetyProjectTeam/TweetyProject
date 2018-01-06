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
 /**
 * 
 */
package net.sf.tweety.arg.aba;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.arg.dung.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Reasoner;

/**
 * @author Nils Geilen 
 * This class models a reasoner over ABA formulae
 * Can only be used with flat ABA theories because
 * only those can be transformed into Dung frameworks
 */
public class FlatABAReasoner extends Reasoner {

	Semantics semantics;
	int inferencetype;

	/**
	 * Creates a new instance
	 * 
	 * @param beliefBase
	 *            an ABATheory
	 * @param semantics
	 *            an indicator for the used semantics (c.f.
	 *            net.sf.tweety.arg.dung.semantics.Semantics)
	 * @param inferencetype
	 *            an indicator for the used inference (c.f.
	 *            net.sf.tweety.arg.dung.semantics.Semantics)
	 */
	public FlatABAReasoner(BeliefBase beliefBase, Semantics semantics, int inferencetype) {
		super(beliefBase);
		this.semantics = semantics;
		this.inferencetype = inferencetype;
		if (!(beliefBase instanceof ABATheory))
			throw new IllegalArgumentException("Knowledge base of type ABATheory<?> expected");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.commons.Reasoner#query(net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(Formula query) {
		Argument arg;
		if (query instanceof Assumption)
			arg = new Argument(((Assumption<?>) query).getConclusion().toString());
		else
			throw new RuntimeException("ABAReasoner.query expects input of class Assumption");
		ABATheory<?> abat = (ABATheory<?>) getKnowledgeBase();
		DungTheory dt = abat.asDungTheory();
		AbstractExtensionReasoner aer = AbstractExtensionReasoner.getReasonerForSemantics(dt, semantics, inferencetype);
		return aer.query(arg);
	}

	public Collection<Collection<Assumption<?>>> getExtensions() {
		ABATheory<?> abat = (ABATheory<?>) getKnowledgeBase();
		DungTheory dt = abat.asDungTheory();
		AbstractExtensionReasoner aer = AbstractExtensionReasoner.getReasonerForSemantics(dt, semantics, inferencetype);
		Collection<Collection<Assumption<?>>> result = new HashSet<>();
		for (Extension ext : aer.getExtensions()) {
			Collection<Assumption<?>> abaext = new HashSet<>();
			for (Argument arg : ext) {
				for (Assumption<?> ass : abat.getAssumptions()) {
					if (ass.toString().equals(arg.toString())) {
						abaext.add(ass);
						break;
					}
				}
			}
			result.add(abaext);
		}
		return result;
	}

}
