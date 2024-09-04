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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.bpm.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.tweetyproject.logics.petri.syntax.Place;
import org.tweetyproject.logics.petri.syntax.reachability_graph.Marking;
import org.tweetyproject.logics.petri.syntax.reachability_graph.MarkovWalk;
import org.tweetyproject.logics.petri.syntax.reachability_graph.ReachabilityGraph;
import org.tweetyproject.math.matrix.Matrix;

/**
 * The DeadEndMeasure
 * @author Benedikt Knopp
 */
public class DeadEndMeasure implements BpmnInconsistencyMeasure{

	/**
	 * the ReachabilityGraph for which the inconsistency value is to find
	 */
	private ReachabilityGraph reachabilityGraph;

	/**
	 * DeadEndInconsistencyMeasure
	 */
	public DeadEndMeasure() {}

	/**
	 * A token sensitive measure assigns higher inconsistency values if more tokens remain in non-final places
	 */
	private boolean tokenSensitive = false;

	/**
	 * the inconsistency value, calculated after performing the Markov walk
	 */
	private Double inconsistencyValue;

	/**
	 * the responsibilities of individual places for the global inconsistency value, calculated after performing the Markov walk
	 */
	private Map<Place, Double> placeCulpabilities = new HashMap<>();

	/**
	 * the responsibilities of individual states for the global inconsistency value, calculated after performing the Markov walk
	 */
	private Map<Marking, Double> markingCulpabilities = new HashMap<>();


	@Override
	public Double inconsistencyMeasure(ReachabilityGraph reachabilityGraph) {
		this.reachabilityGraph = reachabilityGraph;
		MarkovWalk randomWalk = new MarkovWalk(reachabilityGraph);
		randomWalk.initializeWalk();
		randomWalk.performWalk();
		Matrix limit = randomWalk.getMeanState();
		calculateInconsistencyValue(limit);
		return inconsistencyValue;
	}

	/**
	 * calculates the inconsistency and culpabilities after performing the Markov walk
	 * @param limit the mean state limit of the Markov walk
	 */
	private void calculateInconsistencyValue(Matrix limit) {
		double inconsistencyValue = 0;
		for(Place place : reachabilityGraph.getPetriNet().getPlaces()) {
			placeCulpabilities.put(place, 0d);
		}
		for(int j = 0; j < limit.getXDimension(); j++) {
			Marking marking = reachabilityGraph.getNodes().get(j);
			Double markingCulpability = 0d;
			double markingProbability = limit.getEntry(j, 0).doubleValue();
			if(Math.abs(markingProbability) < MEASURE_TOLERANCE) {
				markingCulpabilities.put(marking, markingCulpability);
				continue;
			}
			for(Place place : marking.getPlaces()) {
				int tokensAtPlace = marking.getTokensByPlace(place);
				if(place.isFinal() || tokensAtPlace == 0) {
					continue;
				}
				double placeCulpability = placeCulpabilities.get(place);
				placeCulpability += tokenSensitive? markingProbability*tokensAtPlace : markingProbability;
				markingCulpability += tokenSensitive ? markingProbability*tokensAtPlace : markingProbability;
				placeCulpabilities.put(place, placeCulpability);
			}
			markingCulpabilities.put(marking, markingCulpability);
			inconsistencyValue += markingCulpability;
		}
		this.inconsistencyValue = inconsistencyValue;
	}

	@Override
	/**
	 * Build and return some strings that describe the graph and its inconsistency value
	 * This comprises 1) an ordered list of places and 2) the markings as token distributions
	 * with respect to that ordering and 3) the calculated inconsistency value
	 * @return the info strings
	 */
	public List<String> getInfoStrings() {
		List<String> infoStrings = new ArrayList<String>();
		List<Marking> markings = reachabilityGraph.getMarkings();
		if(markings.size() == 0 || markings.get(0).getPlaces().size() == 0) {
			return infoStrings;
		}
		List<Place> places = markings.get(0).getPlaces().stream().collect(Collectors.toList());
		String infoString = "";
		infoString += "<br>Places / Culpabilities: <br>";
		infoString += places.stream()
				.map(place -> place.getName() + " / " + Math.round(placeCulpabilities.get(place) * 100.0) / 100.0)
				.collect(Collectors.joining(",<br>"));
		infoString += "<br>";
		infoStrings.add(infoString);

		infoString = "";
		infoString += "Markings (w.r.t place ordering above) / Culpabilities:";
		infoStrings.add(infoString);
		List<Marking> orderedMarkings = markings.stream().sorted( (m,n) -> m.getId().compareTo(n.getId()))
				.collect(Collectors.toList());
		for(Marking marking : orderedMarkings) {
			infoString = "";
			infoString += marking.getId() + ": (";
			infoString += places.stream().map(place -> String.valueOf(marking.getTokens(place)))
				.collect(Collectors.joining(", "));
			infoString += ") / " + Math.round(markingCulpabilities.get(marking) * 100.0) / 100.0 + "\n";
			infoStrings.add(infoString);
		}
		infoStrings.add("<br>");
		String sensitivityInfo = "Token Sensitivity: " ;
		sensitivityInfo += tokenSensitive ? "Yes" : "No";
		infoStrings.add(sensitivityInfo);
		infoStrings.add("<i>---Dead-end inconsistency: " + Math.round(inconsistencyValue * 100.0) / 100.0 + "---</i>");
		return infoStrings;
	}

	/**
	 * specify before determining the values whether the value should be token sensitive
	 * A token sensitive measure assigns higher inconsistency values if more tokens remain in non-final places
	 * @param tokenSensitive if token is sensitive
	 */
	public void setTokenSensitivity(boolean tokenSensitive) {
		this.tokenSensitive = tokenSensitive;
	}

	/**
	 * Retrieve for a particular (non-final) place its local culpability for the global inconsistency
	 * A high culpability means that many tokens remain in that place with a high probability
	 * @param place the place
	 * @return the place culpability
	 */
	public Double getPlaceCulpability(Place place) {
		return this.placeCulpabilities.get(place);
	}

	/**
	 * Retrieve for a particular (non-final) state its local culpability for the global inconsistency
	 * A high culpability means that this state features many tokens in non-final places and the state is entered with a high probability
	 * @param marking the state
	 * @return the state culpability
	 */
	public Double getMarkingCulpability(Marking marking) {
		return this.markingCulpabilities.get(marking);
	}

}
