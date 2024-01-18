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

import org.tweetyproject.arg.aba.reasoner.CompleteReasoner;
import org.tweetyproject.arg.aba.reasoner.GeneralAbaReasoner;
import org.tweetyproject.arg.aba.reasoner.IdealReasoner;
import org.tweetyproject.arg.aba.reasoner.PreferredReasoner;
import org.tweetyproject.arg.aba.reasoner.StableReasoner;
import org.tweetyproject.arg.aba.reasoner.WellFoundedReasoner;

/**
 * Main factory for retrieving reasoners for aba.
 * @author Jonas Klein
 */
public abstract class GeneralAbaReasonerFactory {

	/** An enumeration of all available inconsistency measures. */
	public enum Semantics{
		/**
		 * *description missing* 
		 */
		ID ("id", "Ideal"),
		/**
		 * *description missing* 
		 */
		PR ("pr", "Preffered"),
		/**
		 * *description missing* 
		 */
		CO ("co", "Complete"),
		/**
		 * *description missing* 
		 */
		ST ("st", "Stable"),
		/**
		 * *description missing* 
		 */
		WF ("wf", "Well founded");
		/**id*/
		public String id;
		/**label*/
		public String label;

		Semantics(String id, String label){
			this.id = id;
			this.label = label;
		}
		/**
		 *
		 * @param id ID
		 * @return the measure
		 */
		public static Semantics getSemantics(String id){
			for(Semantics m: Semantics.values())
				if(m.id.equals(id))
					return m;
			return null;
		}
	}

	/**
	 * *description missing*
	 * @return *description missing*
	 */
	public static Semantics [] getSemantics(){
		return Semantics.values();
	}

	/**
	 * *description missing*
	 * @param sem *description missing*
	 * @return *description missing*
	 * @param <T> *description missing*
	 */
	public static  <T> GeneralAbaReasoner getReasoner(Semantics sem){
		switch(sem){
			case ID:
				return new IdealReasoner<>();
			case CO:
				return new CompleteReasoner<>();
			case ST:
				return new StableReasoner<>();
			case PR:
				return new PreferredReasoner<>();
			case WF:
				return new WellFoundedReasoner<>();
			default:
				throw new RuntimeException("No reasoner found for semantics " + sem.toString());
		}
	}
}
