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
package org.tweetyproject.logics.petri.syntax.reachability_graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.tweetyproject.graphs.Node;
import org.tweetyproject.logics.petri.syntax.Place;

/**
 * A class to describe markings (i.e. a token distribution over all places) of a Petri net
 * @author Benedikt Knopp
 */
public class Marking implements Node, Comparable<Marking> {

	/**
	 * a unique identifier
	 */
	private String id;

	/**
	 * The number of tokens at each place (identified by an id) at this marking
	 */
	private Map<Place, Integer> tokensByPlace;

	/**
	 * Create a new instance
	 */
	public Marking() {
		this.tokensByPlace = new HashMap<>();
	}

	/**
	 * Create a new instance
	 * 
	 * @param places the places that this marking does describe
	 */
	public Marking(Set<Place> places) {
		this.tokensByPlace = new HashMap<>();
		places.forEach(place -> {
			tokensByPlace.put(place, 0);
		});
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param place the place of interest
	 * @return the tokens at the place at this marking
	 */
	public int getTokens(Place place) {
		return tokensByPlace.get(place);
	}

	/**
	 * Specify the number of tokens at the place at this marking
	 * 
	 * @param place the id of the place
	 * @param token   the token
	 */
	
	public void putTokens(Place place, int token) {
		this.tokensByPlace.put(place, token);
	}

	/**
	 * 
	 * @param place hasPlace
	 * @return hasPlace
	 */
	public boolean hasPlace(Place place) {
		return this.tokensByPlace.containsKey(place);
	}

	/**
	 * 
	 * @return getPlaces
	 */
	public Set<Place> getPlaces() {
		return tokensByPlace.keySet();
	}

	/**
	 * check if this marking deeply equals that marking
	 * @param marking that marking to compare this marking to
	 * @return true iff both markings describe the same set of places and the number
	 *         of tokens are equal at each place
	 */
	public boolean equals(Marking marking) {
		if (marking.getPlaces().size() != this.getPlaces().size()) {
			return false;
		}
		for (Entry<Place, Integer> entry : this.tokensByPlace.entrySet()) {
			Place place = entry.getKey();
			Integer tokens = entry.getValue();
			if (!marking.hasPlace(place) || marking.getTokens(place) != tokens) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Sum up the number of tokens at all non-final places at this marking
	 * @return the sum
	 */
	public int getSumOfTokensAtNonFinalPlaces() {
		return this.tokensByPlace.entrySet().stream().filter(entry -> {
			return !entry.getKey().isFinal();
		}).map(entry -> {
			return entry.getValue();
		}).reduce(0, Integer::sum);
	}

	@Override
	public int compareTo(Marking that) {
		// Marking implements Comparable in order to
		// have a fixed (but arbitrary) order among all edges
		// for that purpose we just use the hash code.
		return this.hashCode() - that.hashCode();
	}

}
