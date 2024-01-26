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
package org.tweetyproject.web.services.dung;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleAdmissibleReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleCompleteReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleConflictFreeReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleEagerReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleGroundedReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleIdealReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleInitialReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleNaiveReasoner;
import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleSemiStableReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleStableReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleStageReasoner;
import org.tweetyproject.arg.dung.reasoner.SolidAdmissibleReasoner;
import org.tweetyproject.arg.dung.reasoner.Stage2Reasoner;
import org.tweetyproject.arg.dung.reasoner.StronglyAdmissibleReasoner;
import org.tweetyproject.arg.dung.reasoner.WeaklyAdmissibleReasoner;
import org.tweetyproject.arg.dung.reasoner.WeaklyCompleteReasoner;
import org.tweetyproject.arg.dung.reasoner.WeaklyGroundedReasoner;
import org.tweetyproject.arg.dung.reasoner.WeaklyPreferredReasoner;

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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
/**
 * Main factory for retrieving abstract extension reasoners.
 * @author Jonas Klein
 */
public abstract class AbstractExtensionReasonerFactory {

	/** An enumeration of all available semantics. */
	public enum Semantics{
		/**
		 * *description missing*
		 */
		WAD ("wad", "Weakly Admissable "),
		/**
		 * *description missing*
		 */
		WCO ("wco", "Weakly Complete "),
		/**
		 * *description missing*
		 */
		WGR ("wgr", "Weakly Grounded"),
		/**
		 * *description missing*
		 */
		WPR ("wgr", "Weakly Preffered"),
		/**
		 * *description missing*
		 */
		SOAD ("soad", "Solid Admissible"),
		/**
		 * *description missing*
		 */
		SAD ("sad", "Strongly Admissable "),
		/**
		 * *description missing*
		 */
		STAGE ("stage", "Stage"),
		/**
		 * *description missing*
		 */
		STAGE2 ("stage2", "Stage2"),
		/**
		 * *description missing*
		 */
		SST ("SST", "Semi Stable"),
		/**
		 * *description missing*
		 */
		CF ("cf", "Conflict-free"),
		/**
		 * *description missing*
		 */
		ID ("id", "Ideal"),
		/**
		 * *description missing*
		 */
		GR ("gr", "Grounded"),
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
		EA ("ea", "Eager"),
		/**
		 * *description missing*
		 */
		IN ("in", "Initial"),
		/**
		 * *description missing*
		 */
		AD ("ad", "Admissible"),
		/**
		 * *description missing*
		 */
		NA ("na", "Naive");
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
		 * @return the semantics
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
	 * Creates a new reasoner measure of the given semantics with default
	 * settings.
	 * @param sem some identifier of an semantics.
	 * @return the requested reasoner.
	 */
	public static AbstractExtensionReasoner getReasoner(Semantics sem){
		switch(sem){
			case WAD:
				return new WeaklyAdmissibleReasoner();
			case WCO:
				return new WeaklyCompleteReasoner();
			case WGR:
				return new WeaklyGroundedReasoner();
			case WPR:
				return new WeaklyPreferredReasoner();
			case AD:
				return new SimpleAdmissibleReasoner();
			case SAD:
				return new StronglyAdmissibleReasoner();
			case SOAD:
				return new SolidAdmissibleReasoner();
			case STAGE:
				return new SimpleStageReasoner();
			case STAGE2:
				return new Stage2Reasoner();
			case SST:
				return new SimpleSemiStableReasoner();
			case ID:
				return new SimpleIdealReasoner();
			case GR:
				return new SimpleGroundedReasoner();
			case CO:
				return new SimpleCompleteReasoner();
			case ST:
				return new SimpleStableReasoner();
			case PR:
				return new SimplePreferredReasoner();
			case EA:
				return new SimpleEagerReasoner();
			case IN:
				return new SimpleInitialReasoner();
			case NA:
				return new SimpleNaiveReasoner();
			case CF:
				return new SimpleConflictFreeReasoner();
			default:
				throw new RuntimeException("No reasoner found for semantics " + sem.toString());
		}
	}
}
