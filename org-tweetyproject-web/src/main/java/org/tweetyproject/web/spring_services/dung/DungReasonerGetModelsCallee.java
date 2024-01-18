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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.spring_services.dung;

import java.util.Collection;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.web.spring_services.Callee;

/**
 * *description missing*
 */
public class DungReasonerGetModelsCallee extends Callee {
		AbstractExtensionReasoner reasoner;
		DungTheory bbase;
		/**
		 * *description missing*
		 * @param reasoner *description missing*
		 * @param bbase *description missing*
		 */
		public DungReasonerGetModelsCallee(AbstractExtensionReasoner reasoner, DungTheory bbase){
			this.reasoner = reasoner;
			this.bbase = bbase;
		}
		@Override
		public Collection<Extension<DungTheory>> call() throws Exception {
			return this.reasoner.getModels(this.bbase);
		}

}
