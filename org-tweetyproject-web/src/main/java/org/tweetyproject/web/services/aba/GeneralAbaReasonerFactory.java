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
package org.tweetyproject.web.services.aba;

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
/**
 * The GeneralAbaReasonerFactory class is an abstract factory class for creating instances of
 * Argumentation-Based Argumentation (ABA) reasoners with different semantics.
 *
 * @param <T> The type of formula used in ABA
 */
public abstract class GeneralAbaReasonerFactory<T> {

    /**
     * An enumeration of all available semantics for ABA reasoners.
     */
    public enum Semantics {
        ID("id", "Ideal"),
        PR("pr", "Preferred"),
        CO("co", "Complete"),
        ST("st", "Stable"),
        WF("wf", "Well-founded");

        /** The identifier of the semantics */
        public String id;

        /** The label of the semantics */
        public String label;

        /**
         * Constructor for Semantics enumeration.
         *
         * @param id    The identifier of the semantics
         * @param label The label of the semantics
         */
        Semantics(String id, String label) {
            this.id = id;
            this.label = label;
        }

        /**
         * Gets the Semantics enumeration based on the provided identifier.
         *
         * @param id The identifier of the semantics
         * @return The Semantics enumeration for the given identifier
         */
		public static Semantics getSemantics(String id){
			for(Semantics m: Semantics.values())
				if(m.id.equals(id))
					return m;
			return null;
		}
	}

	 /**
     * Gets an array of all available semantics.
     *
     * @return An array of Semantics values
     */
	public static Semantics [] getSemantics(){
		return Semantics.values();
	}

    /**
     * Creates a new ABA reasoner instance of the given type with default settings.
     *
     * @param sem The desired semantics for the ABA reasoner
     * @return The requested ABA reasoner instance
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
