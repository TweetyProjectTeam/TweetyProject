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

import java.util.LinkedHashMap;
import java.util.Map;

import org.tweetyproject.arg.aba.reasoner.AdmissibleReasoner;
import org.tweetyproject.arg.aba.reasoner.CompleteReasoner;
import org.tweetyproject.arg.aba.reasoner.ConflictFreeReasoner;
import org.tweetyproject.arg.aba.reasoner.GeneralAbaReasoner;
import org.tweetyproject.arg.aba.reasoner.IdealReasoner;
import org.tweetyproject.arg.aba.reasoner.PreferredReasoner;
import org.tweetyproject.arg.aba.reasoner.SetafReductionReasoner;
import org.tweetyproject.arg.aba.reasoner.StableReasoner;
import org.tweetyproject.arg.aba.reasoner.WellFoundedReasoner;

/**
 * Abstract factory for retrieving ABA reasoners. Semantics with a direct reasoner use it;
 * all other Dung semantics accepted by the SETAF reduction use that (flat theories only).
 *
 * @param <T> the formula type used in ABA
 */
public abstract class GeneralAbaReasonerFactory<T> {

	/**
	 * Prevents direct instantiation.
	 */
	protected GeneralAbaReasonerFactory() {
	}

    /**
     * An enumeration of all available semantics for ABA reasoners.
     */
    public enum Semantics {
        /** ideal */
        ID("id", "Ideal"),
        /** preferred */
        PR("pr", "Preferred"),
        /** complete */
        CO("co", "Complete"),
        /** stable */
        ST("st", "Stable"),
        /** well founded */
        WF("wf", "Well-founded"),
        /** conflict-free */
        CF("cf", "Conflict-free"),
        /** admissible */
        ADM("adm", "Admissible");

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

	/** Dung semantics without a direct reasoner that the SETAF reduction accepts, by id */
	private static final Map<String, org.tweetyproject.arg.dung.semantics.Semantics> REDUCTION = reductionSemantics();

	private static Map<String, org.tweetyproject.arg.dung.semantics.Semantics> reductionSemantics() {
		Map<String, org.tweetyproject.arg.dung.semantics.Semantics> result = new LinkedHashMap<>();
		for (org.tweetyproject.arg.dung.semantics.Semantics s : org.tweetyproject.arg.dung.semantics.Semantics.values()) {
			String id = s.abbreviation().toLowerCase();
			if (Semantics.getSemantics(id) != null)
				continue;
			try {
				new SetafReductionReasoner<>(s);
				result.put(id, s);
			} catch (IllegalArgumentException e) {
				// rejected by the reduction
			}
		}
		return result;
	}

	/**
	 * Gets all available semantics: the direct ones first, then those via the SETAF reduction.
	 *
	 * @return a map from semantics id to label
	 */
	public static Map<String, String> getAvailableSemantics() {
		Map<String, String> result = new LinkedHashMap<>();
		for (Semantics s : Semantics.values())
			result.put(s.id, s.label);
		for (Map.Entry<String, org.tweetyproject.arg.dung.semantics.Semantics> e : REDUCTION.entrySet()) {
			String label = e.getValue().description().replace(" semantics", "");
			result.put(e.getKey(), Character.toUpperCase(label.charAt(0)) + label.substring(1));
		}
		return result;
	}

	/**
	 * Returns a reasoner for the given semantics id.
	 *
	 * @param id a semantics id
	 * @return a direct reasoner if there is one, else a SETAF reduction reasoner, or null for unknown ids
	 */
	@SuppressWarnings("rawtypes")
	public static GeneralAbaReasoner getReasoner(String id) {
		Semantics direct = Semantics.getSemantics(id);
		if (direct != null)
			return getReasoner(direct);
		org.tweetyproject.arg.dung.semantics.Semantics s = REDUCTION.get(id);
		return s == null ? null : new SetafReductionReasoner<>(s);
	}

    /**
     * Creates a new ABA reasoner instance of the given type with default settings.
     *
     * @param sem The desired semantics for the ABA reasoner
     * @return The requested ABA reasoner instance
     */
    /**
     *
     * Return a reasoner
     * @param <T> type
     * @param sem semantics
     * @return a reasoner
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
			case CF:
				return new ConflictFreeReasoner<>();
			case ADM:
				return new AdmissibleReasoner<>();
			default:
				throw new RuntimeException("No reasoner found for semantics " + sem.toString());
		}
	}
}
