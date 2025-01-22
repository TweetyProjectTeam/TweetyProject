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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.setaf.reasoners;

import java.util.*;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.setaf.syntax.SetAf;

/**
 * This reasoner for SetAf theories performs inference on the grounded
 * extension.
 * Computes the (unique) grounded extension, i.e., the least fixpoint of the
 * characteristic function faf.
 *
 * @author Matthias Thimm, Sebastian Franke
 *
 */
public class SimpleGroundedSetAfReasoner extends AbstractExtensionSetAfReasoner {

	/** Default */
			public SimpleGroundedSetAfReasoner(){
				// Default
			}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.tweetyproject.arg.setaf.reasoner.AbstractExtensionReasoner#getModels(org.
	 * tweetyproject.arg.setaf.syntax.DungTheory)
	 */
	@Override
	public Collection<Extension<SetAf>> getModels(SetAf bbase) {
		Collection<Extension<SetAf>> extensions = new HashSet<Extension<SetAf>>();
		extensions.add(this.getModel(bbase));
		return extensions;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.tweetyproject.arg.setaf.reasoner.AbstractExtensionReasoner#getModel(org.
	 * tweetyproject.arg.setaf.syntax.DungTheory)
	 */
	@Override
	public Extension<SetAf> getModel(SetAf bbase) {
		Extension<SetAf> ext = new Extension<SetAf>();
		int size;
		do {
			size = ext.size();
			ext = ((SetAf) bbase).faf(ext);
		} while (size != ext.size());
		return ext;
	}

	@Override
	public boolean isInstalled() {
		return true;
	}

}
