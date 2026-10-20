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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisability;

import java.util.Collection;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleAdmissibleReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * The basis of a serialisation of abstract argumentation frameworks: the base
 * notion τ, the reduct, and the classification of initial sets. The AF
 * serialisation schemes delegate these operations to their basis.
 * <p>
 * Other bases are obtained by overriding any of these methods. Whether a
 * combination is well-behaved (in particular, whether the reduct satisfies
 * contract C4 of {@link org.tweetyproject.serialisability.SerialisationScheme})
 * is not checked.
 *
 * @see "Matthias Thimm. 'Revisiting initial sets in abstract argumentation' Argument &amp; Computation (2022)"
 *
 * @author Lars Bengel
 */
public class AfSerialisationBasis {
	/**
	 * Admissibility with the standard reduct; satisfies C1–C6 [Thimm 2022, Thm. 1, Cor. 1].
	 */
	public static final AfSerialisationBasis ADMISSIBLE = new AfSerialisationBasis(new SimpleAdmissibleReasoner());

	/** the reasoner computing the models of the base notion */
	private final AbstractExtensionReasoner reasoner;

	/**
	 * Creates a basis whose base notion is given by the models of the reasoner
	 * @param reasoner some reasoner
	 */
	public AfSerialisationBasis(AbstractExtensionReasoner reasoner) {
		this.reasoner = reasoner;
	}

	/**
	 * @param af some argumentation framework
	 * @return the models of the base notion
	 */
	public Collection<Extension<DungTheory>> getModels(DungTheory af) {
		return this.reasoner.getModels(af);
	}

	/**
	 * Computes the reduct, i.e. removes the extension and all arguments attacked by it
	 * @param af  some argumentation framework
	 * @param ext some extension
	 * @return the reduct of the framework wrt. the extension
	 */
	public DungTheory getReduct(DungTheory af, Extension<DungTheory> ext) {
		return af.getReduct(ext);
	}

	/**
	 * @param af         some argumentation framework
	 * @param initialSet some initial set
	 * @return true iff no argument of the framework attacks the initial set
	 */
	public boolean isUnattacked(DungTheory af, Extension<DungTheory> initialSet) {
		return af.getAttackers(initialSet).isEmpty();
	}

	/**
	 * @param af         some argumentation framework
	 * @param initialSet some initial set
	 * @param other      another initial set
	 * @return true iff one of the initial sets attacks the other
	 */
	public boolean isConflicting(DungTheory af, Extension<DungTheory> initialSet, Extension<DungTheory> other) {
		return af.isAttacked(initialSet, other) || af.isAttacked(other, initialSet);
	}
}
