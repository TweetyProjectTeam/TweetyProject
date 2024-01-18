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
package org.tweetyproject.web.spring_services.delp;

import org.tweetyproject.arg.delp.reasoner.DelpReasoner;
import org.tweetyproject.arg.delp.semantics.DelpAnswer;
import org.tweetyproject.arg.delp.syntax.DefeasibleLogicProgram;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.web.spring_services.Callee;

/**
 * *description missing*
 */
public class DelpCallee extends Callee {
    DefeasibleLogicProgram delp;
    DelpReasoner reasoner;
    Formula f;
		/**
		 * *description missing*
		 * @param delp *description missing*
		 * @param reasoner *description missing*
		 * @param f *description missing*
		 */
		public DelpCallee(DefeasibleLogicProgram delp,DelpReasoner reasoner,Formula f){
			this.delp = delp;
			this.reasoner = reasoner;
            this.f = f;
		}
		@Override
		public DelpAnswer.Type call() throws Exception {
			return this.reasoner.query(this.delp,(FolFormula) this.f);
		}

}
