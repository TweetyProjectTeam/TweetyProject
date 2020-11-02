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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.commons.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.Formula;

/**
 * This class summarizes the results of computing inconsistency values
 * for some collection of knowledge bases using some set of inconsistency measures,
 * including computation times and some statistics.
 * 
 * @see net.sf.tweety.logics.commons.analysis.InconsistencyMeasureEvaluator
 * 
 * @author Anna Gessler
 */
public class InconsistencyMeasureReport<T extends Formula, U extends BeliefSet<T, ?>> {
	private Map<String, List<InconsistencyMeasureResult>> results;
	private List<U> instances; // The dataset of belief bases.
	private List<String> measures; // Names of all inconsistency measures that are part of this report.

	/**
	 * Creates a new InconsistencyMeasureReport with the given results, the given list of measures used and the 
	 * given list of knowledge bases used.
	 * 
	 * @param results map of InconsistencyMeasure names and lists of computed inconsistency values
	 * @param measures list of strings representing inconsistency measure names 
	 * @param instances list of knowledge bases
	 */
	public InconsistencyMeasureReport( List<String> measures, List<U> instances, Map<String, List<InconsistencyMeasureResult>> results) {
		this.results = results;
		this.measures = measures;
		this.instances = instances;
	}
	
	/**
	 * Returns the result for the ith instance for the given measure.
	 * @param measure name of inconsistency measure
	 * @param i
	 * @return result for ith instance for the given measure
	 */
	public InconsistencyMeasureResult getIthResult(String measure, int i) {
		return results.get(measure).get(i);
	}
	
	/**
	 * Returns a set of all timed out instances of the dataset
	 * wrt. the given measure.
	 * 
	 * @param measure name of inconsistency measure
	 * @return set of timed out instances
	 */
	public Set<U> getTimedOutInstances(String measure) {
		Set<U> timeouts = new HashSet<U>();
		List<InconsistencyMeasureResult> r = results.get(measure);
		for (int i = 0; i < r.size(); i++ ) {
			if (r.get(i).getStatus() == InconsistencyMeasureResult.Status.TIMEOUT)
				timeouts.add(instances.get(i));
		}
		return timeouts;
	}

	/**
	 * Returns the mean time in the results of the given inconsistency measure, not
	 * counting the timed out instances. Returns -1 if all instances are timed out
	 * instances.
	 * 
	 * @param measure name of inconsistency measure
	 * @return mean time
	 */
	public double getMeanTime(String measure) {
		double mean = -1;
		int size = 0;
		for (InconsistencyMeasureResult t : results.get(measure)) {
			long time = t.getElapsedTime();
			if (time >= 0) {
				mean += time; 
				size++;
			}
		}
		return (mean >= 0) ? (mean / size) : -1;
	}

	/**
	 * Returns the median time in the results of the given inconsistency measure,
	 * not counting the timed out instances. Returns -1 if all instances are timed
	 * out instances.
	 * 
	 * @param measure name of inconsistency measure
	 * @return median time
	 */
	public double getMedianTime(String measure) {
		ArrayList<InconsistencyMeasureResult> sortedCopy = new ArrayList<InconsistencyMeasureResult>(results.get(measure));
		Iterator<InconsistencyMeasureResult> it = sortedCopy.iterator();
		// remove timed out instances
		while (it.hasNext()) {
			InconsistencyMeasureResult r = it.next();
			if (r.getStatus() == InconsistencyMeasureResult.Status.TIMEOUT)
				it.remove();
		}
		if (sortedCopy.isEmpty())
			return -1;

		// sort results by times
		Collections.sort(sortedCopy, (a, b) -> (int) (a.getElapsedTime() - b.getElapsedTime()));

		int mid = sortedCopy.size() / 2;
		if (mid != 0 && mid % 2 == 0)
			return (sortedCopy.get(mid).getElapsedTime() + sortedCopy.get(mid - 1).getElapsedTime()) / 2.0;
		return sortedCopy.get(mid).getElapsedTime();
	}

	/**
	 * Returns the maximum time in the results of the given inconsistency measure,
	 * not counting the timed out instances. Returns -1 if all instances are timed
	 * out instances.
	 * 
	 * @param measure name of inconsistency measure
	 * @return maximum time
	 */
	public long getMaxTime(String measure) {
		long max = -1;
		for (InconsistencyMeasureResult t : results.get(measure)) {
			long time = t.getElapsedTime();
			if (time > max)
				max = time;
		}
		return max;
	}

	/**
	 * Returns the minimum time in the results of the given inconsistency measure,
	 * not counting the timed out instances. Returns -1 if all instances are timed
	 * out instances.
	 * 
	 * @param measure name of inconsistency measure
	 * @return minimum time
	 */
	public long getMinTime(String measure) {
		long min = -1;
		for (InconsistencyMeasureResult t : results.get(measure)) {
			long time = t.getElapsedTime();
			if (time >= 0) {
				if (min == -1) 
					min = time;
				else if ( time < min)
					min = time;
			}
		}
		return min;
	}

	/**
	 * Counts the number of timed out entries in the results of the given
	 * inconsistency measure.
	 * 
	 * @param measure name of inconsistency measure
	 * @return number of timeouts
	 */
	public int countTimeouts(String measure) {
		int timeouts = 0;
		for (InconsistencyMeasureResult t : results.get(measure))
			if (t.getStatus() == InconsistencyMeasureResult.Status.TIMEOUT)
				timeouts++;
		return timeouts;
	}
	
	/**
	 * @return true if this report is empty, i.e. if it contains
	 * no measures or no belief set instances and therefore
	 * no results.
	 */
	public boolean isEmpty() {
		return measures.isEmpty() || instances.isEmpty();
	}
	
	/**
	 * Returns a list containing statistics about the given
	 * inconsistency measure: number of timeouts, mean time,
	 * median time and min and max time.
	 * 
	 * @param measure name of inconsistency measure
	 * @return map of statistics for this measure
	 */
	public Map<String, Integer> getStats(String measure) {
		Map<String, Integer> stats = new HashMap<String, Integer>();
		stats.put("# of instances", instances.size());
		stats.put("# of timeouts", countTimeouts(measure));
		stats.put("Mean time", (int) getMeanTime(measure));
		stats.put("Median time", (int) getMedianTime(measure));
		stats.put("Max time", (int) getMaxTime(measure));
		stats.put("Min time", (int) getMinTime(measure));
		return stats;
	}
	
	/**
	 * @return list of the names of all available statistics
	 * (i.e. the ones computed by {@link net.sf.tweety.logics.commons.analysis.InconsistencyMeasureReport#getStats}).  
	 */
	private List<String> getAvailableStats() {
		ArrayList<String> availableStats = new ArrayList<String>();
		availableStats.add("# of instances");
		availableStats.add("# of timeouts");
		availableStats.add("Mean time");
		availableStats.add("Median time");
		availableStats.add("Max time");
		availableStats.add("Min time");
		return availableStats;
	}
	
	/**
	 * Checks if the computed inconsistency values for the ith
	 * instance are different for at least some of the inconsistency
	 * measures. Timed out instances are ignored.
	 * 
	 * @param i
	 * @return true if at least two inconsistency measures produced
	 * different results for the same belief set
	 */
	private boolean resultsAreConflicting(int i) {
		Set<Double> results = new HashSet<Double>();
		for (String im : this.measures) {
			if (getIthResult(im, i).getStatus() == InconsistencyMeasureResult.Status.OK)
				results.add(getIthResult(im, i).getValue());
		}
		return results.size()>1;
	}
	
	/**
	 * Prints the results, times and some statistics like average times and the number of
	 * timeouts for this InconsistencyMeasureReport in table form.
	 * 
	 * @param printFullReport if set to true, the computed inconsistency values and
	 * 						  times for all knowledge bases are included, otherwise
	 * 						  only a minimal report is returned
	 * @param markConflictingResults if set to true, instances that have different values
	 * 								 for at least some of the measures are highlighted. 
	 * 								 Only visible if printFullReport is also true.
	 * @return prettyPrint of this report
	 */
	public String prettyPrint(boolean printFullReport, boolean markConflictingResults) {
		if (this.isEmpty())
			throw new IllegalArgumentException("This report contains no inconsistency measures or no knowledge bases, therefore no results can be printed.");
		
		// Format header column and header row
		int measuresWidth = 5;
		for (String i : this.measures)
			if (i.length() > measuresWidth)
				measuresWidth = i.toString().length();
		measuresWidth++;
		int kbsWidth = String.valueOf(instances.size()).length() + 15;
		String tableResults = "RESULTS\n==========\n";
		String tableTimes = "TIMES\n==========\n";
		String tableStats = "STATISTICS\n==========\n";
		String header = addRowElement(kbsWidth, "");
		for (int i = 0; i < measures.size() - 1; i++) {
			header += addRowElement(measuresWidth, measures.get(i));
		}
		header += addRowEnd(measuresWidth, measures.get(measures.size() - 1));
		tableResults += header;
		tableTimes += header;
		tableStats += header;

		//Format stats
		for (String stat : getAvailableStats()) {
			tableStats += addRowElement(kbsWidth, stat);
			for (int j = 0; j < measures.size() - 1; j++) {
				tableStats += addRowElement(measuresWidth, String.valueOf(getStats(measures.get(j)).get(stat)));
			}
			tableStats += addRowEnd(measuresWidth, String.valueOf(this.getStats(measures.get(measures.size() - 1)).get(stat)));
		}
		if (!printFullReport)
			return tableStats;
		
		//Format inconsistency values
		for (int i = 0; i < instances.size(); i++) {
			String conflictFlag = "";
			if (resultsAreConflicting(i))
				conflictFlag = " !";
			tableResults += addRowElement(kbsWidth, "KB#" + i + conflictFlag);
			tableTimes += addRowElement(kbsWidth, "KB#" + i);
			for (int j = 0; j < measures.size() - 1; j++) {
				tableResults += addRowElement(measuresWidth, String.valueOf(getIthResult(measures.get(j),i).getValue()));
				tableTimes += addRowElement(measuresWidth, String.valueOf(getIthResult(measures.get(j),i).getElapsedTime()));
			}
			tableResults += addRowEnd(measuresWidth, String.valueOf(getIthResult(measures.get(measures.size() - 1),i).getValue()));
			tableTimes += addRowEnd(measuresWidth, String.valueOf(getIthResult(measures.get(measures.size() - 1),i).getElapsedTime()));
		}

		return tableResults + "\n" + tableTimes + "\n" + tableStats;
	}
	
	private String addRowElement(int columnWidth, String element) {
		return String.format("%-" + columnWidth + "s", element);
	}

	private String addRowEnd(int columnWidth, String element) {
		return String.format("%-" + columnWidth + "s\n", element);
	}

}
