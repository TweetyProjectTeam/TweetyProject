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
package org.tweetyproject.web.spring_services.aba;



import org.tweetyproject.arg.aba.reasoner.GeneralAbaReasoner;
import org.tweetyproject.arg.aba.semantics.AbaExtension;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.web.spring_services.Callee;

/**
 * *description missing*
 * @param <T> *description missing*
 */
public class  AbaReasonerGetModelCallee<T extends Formula> extends Callee {
		GeneralAbaReasoner<T> reasoner;
		AbaTheory<T> bbase;
		/**
		 * *description missing*
		 * @param reasoner *description missing*		  
		 * @param bbase *description missing*
		 */
		public  AbaReasonerGetModelCallee(GeneralAbaReasoner<T> reasoner, AbaTheory<T> bbase){
			this.reasoner = reasoner;
			this.bbase = bbase;
		}
		@Override
		public AbaExtension<T> call() throws Exception {
			return this.reasoner.getModel(this.bbase);
		}
}
