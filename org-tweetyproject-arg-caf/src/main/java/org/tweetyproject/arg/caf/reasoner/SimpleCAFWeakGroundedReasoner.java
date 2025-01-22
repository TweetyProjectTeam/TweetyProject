/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.caf.reasoner;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.caf.syntax.ConstrainedArgumentationFramework;
import org.tweetyproject.arg.dung.semantics.Extension;

/**
 * This reasoner for constrained Dung theories (CAF) performs inference on the weak grounded extension.
 * Extensions are determined by checking whether the CAF has a least element.
 * 
 * @author Sandra Hoffmann
 *
 */
public class SimpleCAFWeakGroundedReasoner extends AbstractCAFReasoner {
	
	
	/**
	 * Computes the Weak C-extension for the given constrained argumentation framework if it exists.
	 * 
	 * @param bbase the constrained argumentation framework
	 * @return A collection containing the weak C-extension (if one exists).
	 */
	public Collection<Extension<ConstrainedArgumentationFramework>> getModels(ConstrainedArgumentationFramework bbase) {
		Collection<Extension<ConstrainedArgumentationFramework>> extensions = new HashSet<Extension<ConstrainedArgumentationFramework>>();
		extensions.add(this.getModel(bbase));
		return extensions;
	}
	
	/**
	 * Computes the Weak C-extension for the given constrained argumentation framework if it exists.
	 * 
	 * @param bbase the constrained argumentation framework
	 * @return The weak C-extension (if one exists).
	 */
    public Extension<ConstrainedArgumentationFramework> getModel(ConstrainedArgumentationFramework bbase){
    	Extension<ConstrainedArgumentationFramework> leastElem = new Extension<>();
    	//check if CAF has least element
		try {
			leastElem = bbase.getLeastElement();
		} catch (RuntimeException e){
			//There is no least element, so there is no weak c-ext.
			return null;
		}
		return bbase.fcafIteration(leastElem);
    }

}
