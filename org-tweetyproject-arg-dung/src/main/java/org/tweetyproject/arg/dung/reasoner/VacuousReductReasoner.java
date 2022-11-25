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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Implements a general vacuous reduct reasoner, as proposed in
 * [Thimm. On Undisputed Sets in Abstract Argumentation. AAAI2023]
 * Given a base reasoner A and a reduct reasoner B, a set E
 * is an extension wrt. to vacuous reduct semantics, if E is a A-extension
 * and the corresponding E-reduct does not contain a non-empty B-extension.
 * 
 * @author Matthias Thimm
 *
 */
public class VacuousReductReasoner extends AbstractExtensionReasoner {

	private AbstractExtensionReasoner baseReasoner;
	private AbstractExtensionReasoner reductReasoner;
	
	/**
	 * Creates a new VacuousReductReasoner with the given base and reduct
	 * reasoners.
	 * @param baseReasoner some extension reasoner
	 * @param reductReasoner some extension reasoner
	 */
	public VacuousReductReasoner(AbstractExtensionReasoner baseReasoner, AbstractExtensionReasoner reductReasoner) {
		this.baseReasoner = baseReasoner;
		this.reductReasoner = reductReasoner;
	}
	
	@Override
	public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
		Collection<Extension<DungTheory>> baseModels = this.baseReasoner.getModels(bbase);
		Collection<Extension<DungTheory>> vrModels = new HashSet<>();
		for(Extension<DungTheory> ext: baseModels) {
			Collection<Extension<DungTheory>> reductModels = this.reductReasoner.getModels(bbase.getReduct(ext));
			if(reductModels.isEmpty() || (reductModels.size() == 1 && reductModels.iterator().next().isEmpty()))
				vrModels.add(ext);
		}
		return vrModels;
	}

	@Override
	public Extension<DungTheory> getModel(DungTheory bbase) {
		Collection<Extension<DungTheory>> baseModels = this.baseReasoner.getModels(bbase);		
		for(Extension<DungTheory> ext: baseModels) {
			Collection<Extension<DungTheory>> reductModels = this.reductReasoner.getModels(bbase.getReduct(ext));
			if(reductModels.isEmpty() || (reductModels.size() == 1 && reductModels.iterator().next().isEmpty()))
				return ext;
		}
		return null;
	}

}
